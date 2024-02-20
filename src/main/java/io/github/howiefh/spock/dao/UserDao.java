/*
 * @(#)UserDao 1.0 2023/12/10
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
package io.github.howiefh.spock.dao;

import io.github.howiefh.spock.domain.User;
import io.github.howiefh.spock.domain.UserStatistics;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户DAO类
 *
 * @author fenghao
 * @version 1.0
 * @since 1.0
 */
@Mapper
public interface UserDao {
    /**
     * 将实体存入数据库
     *
     * @param entity
     *            保存的实体
     * @return 返回实体id
     */
    int save(User entity);

    /**
     * 将实体批量存入数据库
     *
     * @param entities
     *            保存的实体
     * @return 返回影响行数
     */
    int saveAll(@Param("collection") Iterable<User> entities);

    /**
     * 根据条件查找实体
     *
     * @param entity
     *            不能为 {@literal null}.
     * @return 条件 entity 对应的实体，如果没有找到返回{@literal null}
     */
    User findOne(User entity);

    /**
     * 按条件查找，返回找到的所有实体的集合
     * @param entity
     *            不能为 {@literal null}.
     *
     * @return 所有实体
     */
    List<User> findBy(User entity);

    /**
     * 按条件查找，返回找到所有可用的实体的个数
     * @param entity
     *            不能为 {@literal null}.
     *
     * @return 所有实体
     */
    long countBy(User entity);

    /**
     * 根据分页信息查找符合条件的实体集合
     *
     * @param entity
     *            包含查找条件的实体
     * @return
     */
    List<User> findPageBy(User entity);

    /**
     * 更新一个实体
     *
     * @param entity
     * @return
     */
    int update(User entity);

    /**
     * 删除一个实体
     *
     * @param entity
     * @return
     */
    int delete(User entity);

    /**
     * 统计
     * @return
     */
    UserStatistics statistics();

}
