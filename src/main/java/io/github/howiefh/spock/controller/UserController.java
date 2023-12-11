/*
 * @(#)UserController 1.0 2023/12/10
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

import io.github.howiefh.spock.domain.Response;
import io.github.howiefh.spock.domain.User;
import io.github.howiefh.spock.service.UserService;
import io.github.howiefh.spock.util.LoginUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器.
 *
 * @author fenghao
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping("/users")
@Validated
public class UserController {
    /**
     * 用户服务
     */
    @Autowired
    private UserService userService;

    @GetMapping(value = "/{userNo}")
    public Response<User> get(@PathVariable String userNo) {
        User user = userService.queryUser(userNo);
        return Response.ok(user);
    }

    @PostMapping(value = "")
    public Response<Boolean> register(@RequestBody @Validated() User user, HttpServletRequest request) {
        user.setUserNo(LoginUtils.getLoginId(request));
        user.setCreatedBy(LoginUtils.getLoginId(request));
        user.setModifiedBy(LoginUtils.getLoginId(request));
        userService.registerUser(user);
        return Response.ok(true);
    }

}
