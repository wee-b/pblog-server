package com.pblog.common.Handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.pblog.common.utils.SecurityContextUtil;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    // 插入操作时的填充逻辑
    @Override
    public void insertFill(MetaObject metaObject) {
        // 填充创建时间和更新时间（当前时间）
        this.strictInsertFill(metaObject, "create_time", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "update_time", LocalDateTime.class, LocalDateTime.now());

        // 填充创建人和更新人（当前登录用户，从SecurityContext获取）
        String username = SecurityContextUtil.getUsername();
        if(username == null){
            username = "0";
        }

        this.strictInsertFill(metaObject, "create_by", String.class, username);
        this.strictInsertFill(metaObject, "update_by", String.class, username);
    }

    // 更新操作时的填充逻辑
    @Override
    public void updateFill(MetaObject metaObject) {
        // 填充更新时间（当前时间）
        this.strictUpdateFill(metaObject, "update_time", LocalDateTime.class, LocalDateTime.now());

        // 填充更新人（当前登录用户）
        String username = SecurityContextUtil.getUsername();
        if(username == null){
            username = "0";
        }
        this.strictUpdateFill(metaObject, "update_by", String.class, username);
    }
}