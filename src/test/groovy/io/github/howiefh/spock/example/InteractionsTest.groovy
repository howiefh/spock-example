/*
 * @(#)InteractionsTest 1.0 2023/12/16
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
class InteractionsTest extends Specification {
    def pub = new Publisher()
    def sub1 = Mock(Subscriber)
    def sub2 = Mock(Subscriber)

    def setup() {
        pub.subscribers << sub1 << sub2
    }

    def "test all subscribers receive event"() {
        when:
        pub.send("event")

        then:
        1 * sub1.receive("event")
        1 * sub2.receive("event")
    }
}
