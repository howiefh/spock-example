/*
 * @(#)InvocationOrderTest 1.0 2023/12/16
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

import spock.lang.Shared
import spock.lang.Specification

/**
 *
 * @author fenghao
 * @version 1.0
 * @since 1.0
 */
class BaseTest extends Specification {
    static order = 0
    def baseField = { println "BaseTest field initializer #${order++}" }()
    @Shared
    def baseSharedField = { println "BaseTest shared field initializer #${order++}" }()
    static baseStaticField = { println "BaseTest static field initializer #${order++}" }()

    def setupSpec() {
        println("BaseTest#setupSpec #${order++}")
    }    // runs once -  before the first feature method

    def setup() {
        println("BaseTest#setup #${order++}")
    }        // runs before every feature method

    def cleanup() {
        println("BaseTest#cleanup #${order++}")
    }      // runs after every feature method

    def cleanupSpec() {
        println("BaseTest#cleanupSpec #${order++}")
    }  // runs once -  after the last feature method

    def "test base"() {
        given:
        println("test base #${order++}")
    }
}

class InvocationOrderTest extends BaseTest {
    def subField = { println "InvocationOrderTest field initializer #${order++}" }()
    @Shared
    def subSharedField = { println "InvocationOrderTest shared field initializer #${order++}" }()
    static subStaticField = { println "InvocationOrderTest static field initializer #${order++}" }()

    def setupSpec() {
        println("InvocationOrderTest#setupSpec #${order++}")
    }    // runs once -  before the first feature method

    def setup() {
        println("InvocationOrderTest#setup #${order++}")
    }        // runs before every feature method

    def cleanup() {
        println("InvocationOrderTest#cleanup #${order++}")
    }      // runs after every feature method

    def cleanupSpec() {
        println("InvocationOrderTest#cleanupSpec #${order++}")
    }  // runs once -  after the last feature method

    def "test invocation order"() {
        given:
        println("test invocation order #${order++}")
    }

    def "test data driven invocation order index=#index"() {
        given:
        println("test invocation order ${index} #${order++}")

        where:
        index | _
        1     | _
        2     | _
    }
}
