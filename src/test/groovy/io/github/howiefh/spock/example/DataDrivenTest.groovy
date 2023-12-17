/*
 * @(#)DataDrivenTest 1.0 2023/12/10
 *
 * Copyright 2023 Feng Hao.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.howiefh.spock.example

import groovy.sql.Sql
import io.github.howiefh.spock.SpockUtils
import spock.lang.Shared
import spock.lang.Specification

/**
 *
 * @author fenghao
 * @version 1.0
 * @since 1.0
 */
class DataDrivenTest extends Specification {
    @Shared sql = Sql.newInstance("jdbc:h2:mem:testdb;MODE=MYSQL;IGNORECASE=FALSE", "org.h2.Driver")

    def setupSpec() {
        sql.execute('CREATE TABLE test ( a INT, b INT, c INT );')
        sql.execute('INSERT INTO test (a, b, c) VALUES (3, 7, 3), (5, 4, 4), (9, 9, 9);')
    }

    def "maximum of #a and #b is #c"() {
        expect:
        Math.max(a, b) == c

        where:
        a << [3, 5, 9]
        b << [7, 4, 9]
        c << [7, 5, 9]
    }

    def "minimum of #a and #b is #c"() {
        expect:
        Math.min(a, b) == c

        where:
        a | b || c
        3 | 7 || 3
        5 | 4 || 4
        9 | 9 || 9
    }

    def "test read sql Math.min(#a, #b) == #c"() {
        expect:
        Math.min(a, b) == c

        where:
        [a, b, c] << sql.rows("select a, b, c from test")
    }

    def "test read csv Math.min(#a, #b) == #c"(Integer a, Integer b, Integer c) {
        expect:
        Math.min(a, b) == c

        where:
        [a, b, c] << SpockUtils.parseCsv("test.csv", Integer.&valueOf)
    }

    def "test read json Math.min(#a, #b) == #c"() {
        expect:
        Math.min(a, b) == c

        where:
        [a, b, c] << SpockUtils.parseJson("test.json")
    }
}
