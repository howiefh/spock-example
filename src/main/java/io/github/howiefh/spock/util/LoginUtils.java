/*
 * @(#)LoginUtils 1.0 2023/12/10
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
package io.github.howiefh.spock.util;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 登录工具类.
 *
 * @author fenghao
 * @version 1.0
 * @since 1.0
 */
public class LoginUtils {
    /**
     * 获取登录id.
     *
     * @param request
     * @return
     */
    public static String getLoginId(HttpServletRequest request) {
        return (String) request.getAttribute("login_id_attribute");
    }
}
