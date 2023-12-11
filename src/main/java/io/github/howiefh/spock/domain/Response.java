/*
 * @(#)Response 1.0 2023/12/10
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
package io.github.howiefh.spock.domain;

import lombok.Data;

/**
 *
 * @author fenghao
 * @version 1.0
 * @since 1.0
 */
@Data
public class Response<T> {
    /**
     * 响应码
     */
    private int code;
    /**
     * 响应数据
     */
    private T data;

    /**
     * 构造成功响应.
     *
     * @param data 响应数据
     * @return
     * @param <T>
     */
    public static <T> Response<T> ok(T data) {
        Response<T> response = new Response<>();
        response.setCode(200);
        response.setData(data);
        return response;
    }
}
