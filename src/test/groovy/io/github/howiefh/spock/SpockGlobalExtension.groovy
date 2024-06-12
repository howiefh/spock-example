/*
 * @(#)SpockGlobalExtension 1.0 2024/6/12
 *
 * Copyright 2024 Feng Hao.
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
package io.github.howiefh.spock

import com.github.fppt.jedismock.RedisServer
import lombok.extern.slf4j.Slf4j
import org.spockframework.runtime.extension.IGlobalExtension
import org.spockframework.runtime.model.SpecInfo

/**
 *
 * @author fenghao
 * @version 1.0
 * @since 2024/5/31
 */
@Slf4j
class SpockGlobalExtension implements IGlobalExtension {
    static RedisServer server = RedisServer.newRedisServer(6379)
    static started = false

    @Override
    void start() {
        server.start()
        started = true
        println "redis started"
    }

    @Override
    void visitSpec(SpecInfo specInfo) {
    }

    @Override
    void stop() {
        if (started) {
            server.stop()
            started = false
            println "redis stopped"
        }
    }
}
