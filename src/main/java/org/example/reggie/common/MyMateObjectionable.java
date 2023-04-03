package org.example.reggie.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 自定义填充策略
 */
@Component
@Slf4j
public class MyMateObjectionable implements MetaObjectHandler {

    /**
     * 插入时填充
     *
     * @param metaObject 元数据对象
     */
    @Override
    public void insertFill(MetaObject metaObject) {

        log.info("插入时填充");

        // 设置创建时间
        metaObject.setValue("createTime", LocalDateTime.now());
        log.info("创建时间: {}", metaObject.getValue("createTime"));

        // 设置更新时间
        metaObject.setValue("updateTime", LocalDateTime.now());
        log.info("更新时间: {}", metaObject.getValue("updateTime"));

        // 设置创建人
        metaObject.setValue("createUser", Long.valueOf(BaseContext.getCurrentId()));
        log.info("创建人: {}", metaObject.getValue("createUser"));

        // 设置更新人
        metaObject.setValue("updateUser", Long.valueOf(BaseContext.getCurrentId()));
        log.info("更新人: {}", metaObject.getValue("updateUser"));

    }

    /**
     * 更新时填充
     *
     * @param metaObject 元数据对象
     */
    @Override
    public void updateFill(MetaObject metaObject) {

        log.info("更新时填充");

        // 设置更新时间
        metaObject.setValue("updateTime", LocalDateTime.now());
        log.info("更新时间: {}", metaObject.getValue("updateTime"));

        // 设置更新人
        metaObject.setValue("updateUser", Long.valueOf(BaseContext.getCurrentId()));
        log.info("更新人: {}", metaObject.getValue("updateUser"));

    }
}
