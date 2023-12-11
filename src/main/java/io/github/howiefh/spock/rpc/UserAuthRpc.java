/*
 * @(#)AuthRpc 1.0 2023/12/10
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
package io.github.howiefh.spock.rpc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * 用户认证RPC类.
 *
 * @author fenghao
 * @version 1.0
 * @since 1.0
 */
@Service
public class UserAuthRpc {
    @Autowired
    private RestTemplate restTemplate;

    /**
     * 获取认证名称.
     *
     * @param loginId
     * @return
     */
    public String queryAuthName(String loginId) {
        RequestEntity<Void> requestEntity = RequestEntity.get("https://mock.apifox.com/m1/3732898-0-default/users/auth?loginId=" + loginId).accept(MediaType.APPLICATION_JSON).build();
        ResponseEntity<Map<String, Object>> responseEntity = restTemplate.exchange(requestEntity, new ParameterizedTypeReference<Map<String, Object>>(){});
        Map<String, Object> body = responseEntity.getBody();
        if (body == null) {
            return null;
        }
        return String.valueOf(body.get("data"));
    }
}
