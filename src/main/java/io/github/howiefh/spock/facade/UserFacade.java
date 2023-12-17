/*
 * @(#)UserFacade 1.0 2023/12/17
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
package io.github.howiefh.spock.facade;

import io.github.howiefh.spock.facade.dto.DataResponse;
import io.github.howiefh.spock.facade.dto.UserRegisterRequest;

/**
 * 用户接口.
 *
 * @author fenghao
 * @version 1.0
 * @since 1.0
 */
public interface UserFacade {
    /**
     * 注册用户
     *
     * @param request 用户对象，包含用户的信息
     * @return 响应对象，表示注册是否成功
     */
    DataResponse<Boolean> register(UserRegisterRequest request);

}
