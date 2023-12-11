/*
 * @(#)DefaultExceptionHandler 1.0 2023/12/10
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
package io.github.howiefh.spock.controller;

import io.github.howiefh.spock.util.LoginUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 默认异常处理类.
 *
 * @author fenghao
 * @version 1.0
 * @since 1.0
 */
@ControllerAdvice
@Slf4j
public class DefaultExceptionHandler {

    /**
     * 处理全局异常.
     * <p>
     * 处理 RequestBody 参数
     * @param request
     * @param ex
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ModelAndView methodArgumentNotValidException(HttpServletRequest request, MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
        List<FieldError> fieldErrors = result.getFieldErrors();
        String msg = fieldErrors.stream().map(FieldError::getDefaultMessage).collect(Collectors.joining(";"));
        return buildErrorModelAndView(msg, request, ex);
    }

    /**
     * 处理全局异常.
     * <p>
     * 处理RequestParam PathVariable 参数
     *
     * @param request
     * @param ex
     * @return
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ModelAndView constraintViolationException(HttpServletRequest request, ConstraintViolationException ex) {
        String msg = ex.getConstraintViolations().stream().map(ConstraintViolation::getMessage).collect(Collectors.joining(";"));
        return buildErrorModelAndView(msg, request, ex);
    }

    /**
     * 处理全局异常.
     *
     * @param request
     * @param ex
     * @return
     */
    @ExceptionHandler(IllegalStateException.class)
    public ModelAndView handleException(HttpServletRequest request, IllegalStateException ex) {
        return buildErrorModelAndView(ex.getMessage(), request, ex);
    }

    /**
     * 处理全局异常.
     *
     * @param request
     * @param ex
     * @return
     */
    @ExceptionHandler(DuplicateKeyException.class)
    public ModelAndView handleException(HttpServletRequest request, DuplicateKeyException ex) {
        return buildErrorModelAndView("记录已存在", request, ex);
    }


    /**
     * 处理全局异常.
     *
     * @param request
     * @param ex
     * @return
     */
    @ExceptionHandler(Exception.class)
    public ModelAndView handleException(HttpServletRequest request, Exception ex) {
        return buildErrorModelAndView("系统开小差了", request, ex);
    }

    /**
     * 构建模型视图
     * @param msg
     * @param request
     * @param ex
     * @return
     */
    private ModelAndView buildErrorModelAndView(String msg, HttpServletRequest request, Exception ex) {
        MappingJackson2JsonView view = new MappingJackson2JsonView();
        Map<String, Object> attributes = Map.of("code", 400, "msg", StringUtils.hasText(msg) ? msg : ex.getMessage());
        view.setAttributesMap(attributes);

        log.error("#handleException loginId: {}, Result: {}, URL: {}, Ex: ", LoginUtils.getLoginId(request), attributes, request.getRequestURL(), ex);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setView(view);
        return modelAndView;
    }

}
