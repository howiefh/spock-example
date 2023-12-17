/*
 * @(#)DataResponse 1.0 2023/12/17
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
package io.github.howiefh.spock.facade.dto;

import lombok.Data;

/**
 * 数据响应.
 *
 * @author fenghao
 * @version 1.0
 * @since 1.0
 */
@Data
public class DataResponse<T> {
    /**
     * 响应码
     */
    private int code;
    /**
     * 响应
     */
    private String msg;
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
    public static <T> DataResponse<T> ok(T data) {
        DataResponse<T> response = new DataResponse<>();
        response.setCode(200);
        response.setData(data);
        return response;
    }

    /**
     * 创建一个错误的数据响应对象
     *
     * @param code 错误代码
     * @param msg 错误消息
     * @return 错误的数据响应对象
     */
    public static <T> DataResponse<T> error(int code, String msg) {
        DataResponse<T> response = new DataResponse<>();
        response.setCode(code);
        response.setMsg(msg);
        return response;
    }
}
