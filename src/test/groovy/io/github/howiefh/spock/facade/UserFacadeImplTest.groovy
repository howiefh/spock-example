/*
 * @(#)UserFacadeImplTest 1.0 2023/12/17
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
package io.github.howiefh.spock.facade

import io.github.howiefh.spock.SpockSpringTest
import io.github.howiefh.spock.facade.dto.UserRegisterRequest
import io.github.howiefh.spock.rpc.UserAuthRpc
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional
import spock.lang.*

/**
 *
 * @author fenghao
 * @version 1.0
 * @since 1.0
 */
@SpockSpringTest
class UserFacadeImplTest extends Specification {
    @Autowired
    UserFacade userFacade

    @SpringBean
    UserAuthRpc userAuthRpc = Mock()

    @Unroll
    @Transactional
    def "#scene test register userNo=#userNo, invitorNo=#invitorNo, userName=#userName -> expectedCode=#expectedCode"() {
        given:
        UserRegisterRequest registerRequest = new UserRegisterRequest(userNo: userNo, invitorNo: invitorNo)
        invokeRpcTimes * userAuthRpc.queryAuthName(_) >> userName

        expect:
        def response = userFacade.register(registerRequest)
        response.code == expectedCode

        where:
        scene          | userNo      | invitorNo | userName   || expectedCode | invokeRpcTimes
        "用户编码为空" | ""          | "1234"    | "子虚乌有" || 400          | 0
        "邀请编码为空" | "zixuwuyou" | ""        | "子虚乌有" || 400          | 0
        "用户未认证"   | "zixuwuyou" | "1234"    | ""         || 400          | 1
        "用户已存在"   | "jack"      | "1234"    | "杰克"     || 400          | 1
        "正常注册"     | "zixuwuyou" | "1234"    | "子虚乌有" || 200          | 1
    }
}
