/*
 * @(#)User 1.0 2023/12/10
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

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户信息类.
 *
 * @author fenghao
 * @version 1.0
 * @since 1.0
 */
@Data
@ToString(callSuper = true)
public class User implements Serializable {
    private static final long serialVersionUID = -1952516537390031872L;
    public static final int NORMAL = 0;
    /**
     * id
     */
    private Long id;

    /** 用户编号 */
    private String userNo;
    /** 用户名称 */
    private String userName;
    /** 版本号 */
    private Integer version;
    /** 0:未删除,1:已删除 */
    private Integer deleteFlag;
    /** 创建者 */
    private String createdBy;
    /** 更新者 */
    private String modifiedBy;
    /** 建立时间 */
    private Date createdDate;
    /** 修改时间 */
    private Date modifiedDate;

    /** 邀请码 */
    @NotBlank(message = "请填写邀请码")
    private String invitorNo;
    /**
     * 页码
     */
    private int page;
    /**
     * 页面记录条数
     */
    private int rows;

    public int getOffset() {
        return (this.page - 1) * this.rows;
    }

    public void fillPaging(PageInfo<?> pageInfo) {
        setPage(pageInfo.getPageNum());
        setRows(pageInfo.getPageSize());
    }

    public void init() {
        this.setDeleteFlag(NORMAL);
        this.setVersion(0);
    }
}