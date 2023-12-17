/*
 * @(#)Publisher 1.0 2023/12/16
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

class Publisher {
    def subscribers = []

    def send(List<String> events) {
        return events.collect {
            return send(it)
        }
    }

    def send(String event) {
        return subscribers.collect {
            try {
                return it.receive(event)
            } catch (Exception e) {}
            return ''
        }
    }
}