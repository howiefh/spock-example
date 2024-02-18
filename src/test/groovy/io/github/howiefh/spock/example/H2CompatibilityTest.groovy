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
class H2CompatibilityTest extends Specification {

    def "test same name key throw exception"() {
        def sql = Sql.newInstance("jdbc:h2:mem:testcompatibility;MODE=MYSQL;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE;", "org.h2.Driver")

        when:
        sql.execute("CREATE TABLE test_uniq_key1 (x VARCHAR(50), KEY `idx_x` (`x`));")
        sql.execute("CREATE TABLE test_uniq_key2 (x VARCHAR(50), KEY `idx_x` (`x`));")
        sql.execute("INSERT INTO test_uniq_key1 (x) SELECT 'foo' x;")
        sql.execute("INSERT INTO test_uniq_key2 (x) SELECT 'foo' x;")

        then:
        thrown(Exception)

        cleanup:
        sql.close()
    }

    @Unroll("#scene")
    def "test user-defined function"() {
        when:
        def sql = Sql.newInstance("jdbc:h2:mem:testcompatibility;MODE=MYSQL;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE;", "org.h2.Driver")
        sql.execute('''CREATE ALIAS IF NOT EXISTS `date_format` AS '
        import java.util.Date;
        import java.text.SimpleDateFormat;
        @CODE
        String dateFormat(Date date, String pattern) {
            if (date == null || pattern == null) {
                return null;
            }
            SimpleDateFormat dateFormat = new SimpleDateFormat(pattern.replace("%Y", "yyyy").replace("%m", "MM").replace("%d", "dd").replace("%H", "HH").replace("%I", "mm").replace("%s", "ss"));
            return dateFormat.format(date);
        }
        ';''')
        sql.execute("CREATE TABLE test_x (x DATETIME);");
        sql.execute("INSERT INTO test_x (x) SELECT '2024-02-18 00:00:00';")

        and:
        def rs = sql.firstRow(querySql)
        def valueSet = rs.values()

        then:
        valueSet.contains(expectedValue)

        cleanup:
        sql.close()

        where:
        scene                | querySql                                                 || expectedValue
        "格式化年月日"       | "select date_format(x, '%Y-%m-%d') from test_x"          || "2024-02-18"
        "格式化年月日时分秒" | "select date_format(x, '%Y-%m-%d %H:%I:%s') from test_x" || "2024-02-18 00:00:00"
    }

}
