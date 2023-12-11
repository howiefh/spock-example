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

import io.github.howiefh.spock.dao.UserDao;
import io.github.howiefh.spock.domain.PageInfo;
import io.github.howiefh.spock.domain.User;
import io.github.howiefh.spock.rpc.UserAuthRpc;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;

/**
 * 用户服务类.
 *
 * @author fenghao
 * @version 1.0
 * @since 1.0
 */
@Service
@Slf4j
public class UserService {
    @Autowired
    private UserDao userDao;
    @Autowired
    private UserAuthRpc userAuthRpc;

    /**
     * 注册用户.
     *
     * @param user
     */
    public String registerUser(User user) {
        String name = userAuthRpc.queryAuthName(user.getUserNo());
        if (!StringUtils.hasText(name)) {
            throw new IllegalStateException("用户未认证");
        }
        user.init();
        user.setUserName(name);
        userDao.save(user);
        return user.getUserNo();
    }

    /**
     * 查询用户信息.
     *
     * @param userNo
     * @return
     */
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
