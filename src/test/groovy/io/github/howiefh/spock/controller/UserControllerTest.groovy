/*
 * @(#)UserControllerTest 1.0 2023/12/10
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
package io.github.howiefh.spock.controller

import io.github.howiefh.spock.SpockSpringTest
import io.github.howiefh.spock.rpc.UserAuthRpc
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.transaction.annotation.Transactional
import spock.lang.*

import static org.hamcrest.Matchers.is
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 *
 * @author fenghao
 * @version 1.0
 * @since 1.0
 */
@SpockSpringTest
class UserControllerTest extends Specification {

    @Autowired
    MockMvc mvc;

    @SpringBean
    UserAuthRpc userAuthRpc = Mock()

    def setup() {
    }

    def "#scene test get userNo=#userNo -> userName=#userName"() {
        expect:
        mvc.perform(MockMvcRequestBuilders.get("/users/${userNo}")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code", is(200)))
                .andExpect(jsonPath("data.userName", is(userName)))
                .andDo(MockMvcResultHandlers.print());

        where:
        scene      | userNo || userName
        "查询正常" | "jack" || "杰克"
    }

    @Unroll
    @Transactional
    def "#scene test register loginId=#loginId, invitorNo=#invitorNo, userName=#userName -> code=#code"() {
        given:
        invokeRpcTimes * userAuthRpc.queryAuthName(_) >> userName

        expect:
        mvc.perform(MockMvcRequestBuilders.post("/users")
                .content("{\"invitorNo\":\"${invitorNo}\"}")
                .requestAttr("login_id_attribute", loginId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code", is(code)))
                .andDo(MockMvcResultHandlers.print());

        where:
        scene        | loginId     | invitorNo | userName   || code | invokeRpcTimes
        "参数错误"   | "zixuwuyou" | ""        | "子虚乌有" || 400  | 0
        "用户未认证" | "zixuwuyou" | "1234"    | ""         || 400  | 1
        "用户已存在" | "jack"      | "1234"    | "杰克"     || 400  | 1
        "正常注册"   | "zixuwuyou" | "1234"    | "子虚乌有" || 200  | 1
    }

}