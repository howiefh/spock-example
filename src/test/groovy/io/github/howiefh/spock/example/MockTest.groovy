/*
 * @(#)MockTest 1.0 2023/12/16
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
class MockTest extends Specification {
    def pub = new Publisher()
    def sub1 = Mock(Subscriber)
    def sub2 = Mock(Subscriber)

    def setup() {
        pub.subscribers << sub1 << sub2
    }

    def "test match mock subscribers receive method argument"() {
        sub1.receive(_) >> { throw new Exception() }

        when:
        pub.send("event1")
        pub.send("event2")

        then:
        1 * sub2.receive("event1")
        1 * sub2.receive("event2")
    }

    def "test mock different return values"() {
        def events = ["event1", "event2", "event3", "event4", "event5", "event6"]
        sub1.receive(_) >>> ["ok", "fail", "ok"] >> { throw new Exception() } >> "ok"
        sub2.receive(_) >> "ok"

        expect:
        pub.send(events) == [["ok", "ok"], ["fail", "ok"], ["ok", "ok"], ["", "ok"], ["ok", "ok"], ["ok", "ok"]]
    }

    def "test mock different return values by closure"() {
        sub1.receive(_) >> { String event -> event.length() > 3 ? "ok" : "fail" }
        sub2.receive(_) >> { String event -> event.isEmpty() ? "fail" : "ok" }

        expect:
        pub.send(e) == result

        where:
        e       || result
        "event" || ["ok", "ok"]
        "eve"   || ["fail", "ok"]
        ""      || ["fail", "fail"]
    }
}


