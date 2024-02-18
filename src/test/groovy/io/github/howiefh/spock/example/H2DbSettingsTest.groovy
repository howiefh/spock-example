package io.github.howiefh.spock.example

import groovy.sql.Sql
import org.h2.jdbc.JdbcSQLNonTransientException
import org.h2.jdbc.JdbcSQLSyntaxErrorException
import spock.lang.Specification
import spock.lang.Unroll


/**
 *
 * @author fenghao
 * @version 1.0
 * @since 2024/2/5
 */
class H2DbSettingsTest extends Specification {

    @Unroll("DB_CLOSE_DELAY=#dbCloseDelay #scene")
    def "test DB_CLOSE_DELAY"() {
        def sql = Sql.newInstance("jdbc:h2:mem:testdbsettings;DB_CLOSE_DELAY=${dbCloseDelay}", "org.h2.Driver")
        sql.execute("DROP TABLE IF EXISTS test_db;")
        sql.execute("CREATE TABLE test_db (x int );");
        sql.execute("INSERT INTO test_db (x) SELECT 1 x;");
        def querySql = "SELECT x FROM test_db WHERE x = 1;"
        def throwsException = false

        when:
        def rows = sql.rows(querySql)
        sql.close()

        then:
        rows.isEmpty() == expectedEmpty

        expect:
        try {
            def sql2 = Sql.newInstance("jdbc:h2:mem:testdbsettings;DB_CLOSE_DELAY=${dbCloseDelay}", "org.h2.Driver")
            def rows2 = sql2.rows(querySql)
            sql2.close()
            rows2 == rows
        } catch (Exception e) {
            throwsException == expectedException
        }

        where:
        scene                      | dbCloseDelay || expectedEmpty | expectedException
        "关闭连接重新连接可以查到" | -1           || false         | false
        "关闭连接重新连接查不到"   | 0            || false         | true
    }

    @Unroll("#scene")
    def "test IGNORECASE"() {
        def sql = Sql.newInstance("jdbc:h2:mem:testdbsettings;IGNORECASE=${ignoreCase}", "org.h2.Driver")
        sql.execute("CREATE TABLE test_values (x VARCHAR(50));");
        sql.execute("INSERT INTO test_values (x) SELECT 'foo' x;");

        when:
        def querySql = "SELECT x FROM test_values WHERE x IN (${foo});"
        def rows = sql.rows(querySql)

        then:
        rows.isEmpty() == expectedEmpty

        cleanup:
        sql.close()

        where:
        scene                               | ignoreCase | foo   || expectedEmpty
        "文本不忽略大小写，通过小写可以查到" | "FALSE"    | "foo" || false
        "文本不忽略大小写，通过大写查不到"   | "FALSE"    | "FOO" || true
        "文本忽略大小写，通过小写可以查到"   | "TRUE"     | "foo" || false
        "文本忽略大小写，通过大写可以查到"   | "TRUE"     | "FOO" || false
    }

    @Unroll("#scene")
    def "test DATABASE_TO_UPPER & DATABASE_TO_LOWER"() {
        when:
        def sql = Sql.newInstance("jdbc:h2:mem:testdbsettings;DATABASE_TO_UPPER=${databaseToUpper};DATABASE_TO_LOWER=${databaseToLower}", "org.h2.Driver")
        sql.execute("CREATE TABLE TEST(a INT)")
        sql.execute("INSERT INTO " + table + "(" + column + ") SELECT 1;")

        and:
        def rs = sql.firstRow("SELECT " + column + " from " + table)
        def keySet = rs.keySet()

        then:
        notThrown(JdbcSQLNonTransientException)

        and:
        keySet.contains(resultColumn)

        cleanup:
        sql.close()

        where:
        scene                                                  | databaseToUpper | databaseToLower | table  | column | resultColumn
        "默认配置，转大写，可以通过大写查，查到的字段名大写"      | "TRUE"          | "FALSE"         | "TEST" | "A"    | "A"
        "默认配置，转大写，可以通过小写查，查到的字段名大写"      | "TRUE"          | "FALSE"         | "test" | "a"    | "A"
        "转小写，可以通过小写查，查到的字段名小写"               | "FALSE"         | "TRUE"          | "test" | "a"    | "a"
        "转小写，可以通过大写查，查到的字段名小写"               | "FALSE"         | "TRUE"          | "TEST" | "A"    | "a"
        "不转大小写，可以通过建表时的大小写查，查到的字段名小写" | "FALSE"         | "FALSE"         | "TEST" | "a"    | "a"
    }

    @Unroll("#scene")
    def "test DATABASE_TO_UPPER & DATABASE_TO_LOWER all false syntax error"() {
        when:
        def sql = Sql.newInstance("jdbc:h2:mem:testdbsettings;DATABASE_TO_UPPER=${databaseToUpper};DATABASE_TO_LOWER=${databaseToLower}", "org.h2.Driver")
        sql.execute("CREATE TABLE TEST(a INT)")
        sql.execute("INSERT INTO " + table + "(" + column + ") SELECT 1;")

        then:
        thrown(JdbcSQLSyntaxErrorException)

        cleanup:
        sql.close()

        where:
        scene                                        | databaseToUpper | databaseToLower | table  | column
        "不转大小写，表名字段名大写，报语法错误"       | "FALSE"         | "FALSE"         | "TEST" | "A"
        "不转大小写，表名小写字段名大写写，报语法错误" | "FALSE"         | "FALSE"         | "test" | "A"
        "不转大小写，表名字段名小写，报语法错误"       | "FALSE"         | "FALSE"         | "test" | "a"
    }

    def "test DATABASE_TO_UPPER & DATABASE_TO_LOWER all true is not supported"() {
        when:
        Sql.newInstance("jdbc:h2:mem:testdbsettings;DATABASE_TO_UPPER=TRUE;DATABASE_TO_LOWER=TRUE", "org.h2.Driver")

        then:
        thrown(JdbcSQLNonTransientException)
    }

    @Unroll("#scene")
    def "test DATABASE_TO_UPPER & DATABASE_TO_LOWER & CASE_INSENSITIVE_IDENTIFIERS"() {
        when:
        def sql = Sql.newInstance("jdbc:h2:mem:testdbsettings;DATABASE_TO_UPPER=${databaseToUpper};DATABASE_TO_LOWER=${databaseToLower};CASE_INSENSITIVE_IDENTIFIERS=${caseInsensitiveIdentifiers}", "org.h2.Driver")
        sql.execute("CREATE TABLE TEST(a INT)")
        sql.execute("INSERT INTO " + table + "(" + column + ") SELECT 1;")

        and:
        def rs = sql.firstRow("SELECT " + column + " from " + table)
        def keySet = rs.keySet()

        then:
        notThrown(JdbcSQLNonTransientException)

        and:
        keySet.contains(resultColumn)

        cleanup:
        sql.close()

        where:
        scene                                                                         | databaseToUpper | databaseToLower | caseInsensitiveIdentifiers | table  | column | resultColumn
        "转大写，标识符大小写不敏感，可以通过大写查，查到的字段名大写"                   | "TRUE"          | "FALSE"         | "TRUE"                     | "TEST" | "A"    | "A"
        "转大写，标识符大小写不敏感，可以通过小写查，查到的字段名大写"                   | "TRUE"          | "FALSE"         | "TRUE"                     | "test" | "a"    | "A"
        "转小写，标识符大小写不敏感，可以通过小写查，查到的字段名小写"                   | "FALSE"         | "TRUE"          | "TRUE"                     | "test" | "a"    | "a"
        "转小写，标识符大小写不敏感，可以通过大写查，查到的字段名小写"                   | "FALSE"         | "TRUE"          | "TRUE"                     | "TEST" | "A"    | "a"
        "不转大小写，标识符大小写不敏感，可以通过建表的大小写查，查到的字段名小写"       | "FALSE"         | "FALSE"         | "TRUE"                     | "TEST" | "a"    | "a"
        "不转大小写，标识符大小写不敏感，可以通过表名字段名大写查，查到的字段名小写"     | "FALSE"         | "FALSE"         | "TRUE"                     | "TEST" | "A"    | "a"
        "不转大小写，标识符大小写不敏感，可以通过表名小写字段名大写查，查到的字段名小写" | "FALSE"         | "FALSE"         | "TRUE"                     | "test" | "a"    | "a"
        "不转大小写，标识符大小写不敏感，可以通过表名字段名小写查，查到的字段名小写"     | "FALSE"         | "FALSE"         | "TRUE"                     | "test" | "A"    | "a"
    }
}
