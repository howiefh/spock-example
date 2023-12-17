/*
 * @(#)ExceptionUtils 1.0 2023/12/17
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

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

import java.util.stream.Collectors;

/**
 *
 * @author fenghao
 * @version 1.0
 * @since 1.0
 */
public final class ExceptionUtils {

    private ExceptionUtils() {
    }


    /**
     * 构建异常信息的方法
     *
     * @param e 异常对象
     * @return 异常信息字符串
     */
    public static String buildMessage(Exception e) {
        if (e instanceof ConstraintViolationException) {
            return ((ConstraintViolationException) e).getConstraintViolations().stream().map(ConstraintViolation::getMessage).collect(Collectors.joining(";"));
        } else {
            return e != null ? e.getMessage() : "系统开小差了";
        }
    }
}
