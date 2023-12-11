CREATE TABLE IF NOT EXISTS sys_user (
    id bigint NOT NULL AUTO_INCREMENT COMMENT '自增编号',
    user_no varchar(32) NOT NULL COMMENT '用户编号',
    user_name varchar(64) NOT NULL COMMENT '用户名称',
    version int NOT NULL DEFAULT '0' COMMENT '版本号',
    delete_flag int NOT NULL DEFAULT '0' COMMENT '0:未删除,1:已删除',
    created_by varchar(64) COMMENT '创建者',
    modified_by varchar(64) COMMENT '更新者',
    created_date datetime NOT NULL COMMENT '创建时间',
    modified_date timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改日期',
    PRIMARY KEY (id),
    CONSTRAINT uk_sys_user UNIQUE(user_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT = '用户';