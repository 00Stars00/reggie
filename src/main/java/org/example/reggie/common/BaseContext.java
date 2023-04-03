package org.example.reggie.common;


import lombok.extern.slf4j.Slf4j;

/**
 * 线程上下文
 */
@Slf4j
public class BaseContext {
    private static final ThreadLocal<String> context = new ThreadLocal<>();


    /**
     * 设置登录状态线程id
     *
     * @param id id
     */
    public static void setCurrentId(String id) {
        log.info("设置登录状态线程id: {}", Thread.currentThread().getId());
        context.set(id);
    }


    /**
     * 获取登录状态线程id
     *
     * @return id
     */
    public static String getCurrentId() {
        log.info("获取登录状态线程id: {}", Thread.currentThread().getId());
        return context.get();
    }
}
