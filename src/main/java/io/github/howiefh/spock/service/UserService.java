/*
 * @(#)UserService 1.0 2023/12/10
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
package io.github.howiefh.spock.service;

import io.github.howiefh.spock.cache.RedisLockService;
import io.github.howiefh.spock.dao.UserDao;
import io.github.howiefh.spock.domain.PageInfo;
import io.github.howiefh.spock.domain.User;
import io.github.howiefh.spock.facade.dto.UserRegisterRequest;
import io.github.howiefh.spock.rpc.UserAuthRpc;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.Lock;

/**
 * 用户服务类.
 *
 * @author fenghao
 * @version 1.0
 * @since 1.0
 */
@Service
@Slf4j
@Validated
public class UserService {
    @Autowired
    private UserDao userDao;
    @Autowired
    private UserAuthRpc userAuthRpc;
    @Autowired
    private RedisLockService redisLockService;

    /**
     * 注册用户.
     *
     * @param request
     */
    public String registerUser(@Valid @NotNull UserRegisterRequest request) {
        User user = new User();
        BeanUtils.copyProperties(request, user);
        return registerUser(user);
    }

    /**
     * 注册用户.
     *
     * @param user
     */
    public String registerUser(User user) {
        Lock lock = redisLockService.getLock(user.getUserNo());
        boolean locked = lock.tryLock();
        if (!locked) {
            return user.getUserNo();
        }
        try {
            String name = userAuthRpc.queryAuthName(user.getUserNo());
            if (!StringUtils.hasText(name)) {
                throw new IllegalStateException("用户未认证");
            }
            user.init();
            user.setUserName(name);
            userDao.save(user);
            return user.getUserNo();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 查询用户信息.
     *
     * @param userNo
     * @return
     */
    @Cacheable(value = "users", key = "#userNo")
    public User queryUser(String userNo) {
        User query = new User();
        query.setUserNo(userNo);
        return userDao.findOne(query);
    }


    /**
     * 分页查询用户信息.
     *
     * @param pageInfo
     * @param condition
     * @return
     */
    public PageInfo<User> queryUserByPage(PageInfo<User> pageInfo, User condition) {
        long count = userDao.countBy(condition);
        List<User> list = null;
        if (count != 0) {
            condition.fillPaging(pageInfo);
            list = userDao.findPageBy(condition);
        } else {
            list = Collections.emptyList();
        }
        pageInfo.setTotal(count);
        pageInfo.setItems(list);
        return pageInfo;
    }
}
