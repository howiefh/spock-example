/*
 * @(#)UserRegisterRequest 1.0 2023/12/17
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

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求.
 *
 * @author fenghao
 * @version 1.0
 * @since 1.0
 */
@Data
public class UserRegisterRequest implements Serializable {

    /**
     * 序列号
     */
    private static final long serialVersionUID = 3536452826772230547L;
    /** 用户编号 */
    @NotBlank(message = "请填写用户编号")
    private String userNo;
    /** 用户名称 */
    private String userName;
    /** 创建者 */
    private String createdBy;
    /** 更新者 */
    private String modifiedBy;

    /** 邀请码 */
    @NotBlank(message = "请填写邀请码")
    private String invitorNo;

}
