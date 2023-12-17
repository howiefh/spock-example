/*
 * @(#)ConditionsTest 1.0 2023/12/16
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

import spock.lang.Specification

/**
 *
 * @author fenghao
 * @version 1.0
 * @since 1.0
 */
class ConditionsTest extends Specification {
    def stack
    def setup() {
        stack = [] as LinkedList<String>
    }

    def "test boolean expressions condition"() {
        def elem = "1"

        when:
        stack.push(elem)

        then:
        !stack.empty
        stack.size() == 1
        stack.peek() == elem
    }

    def "test with boolean expressions condition"() {
        def map = new HashMap()
        def elem = "1"

        when:
        map.put("key", elem)

        then:
        with(map) {
            !isEmpty()
            size() == 1
            map.get("key")
            get("key") == elem
        }
    }

    def "test verifyAll boolean expressions condition"() {
        def map = new HashMap()
        def elem = "1"

        when:
        map.put("key", elem)

        then:
        verifyAll(map) {
            !isEmpty()
            size() == 1
            map.get("key")
            get("key") == elem
        }
    }

    def "test exception condition thrown, LinkedList cannot pop because it is empty."() {
        when:
        stack.pop()

        then:
        thrown(NoSuchElementException)
        stack.empty
    }

    def "test exception condition notThrown, HashMap accepts null key"() {
        given:
        def map = new HashMap()

        when:
        map.put(null, "elem")

        then:
        notThrown(NullPointerException)
    }
}
