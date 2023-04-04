package org.example.reggie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器
 */
@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 处理SQLIntegrityConstraintViolationException异常
     *
     * @param e 异常
     * @return R
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException e) {
        // 打印异常
        log.error(e.getMessage());

        // 判断异常信息
        if (e.getMessage().contains("Duplicate entry"))
            return R.error(e.getMessage().split(" ")[2] + "已存在");

        // 返回
        return R.error("未知异常");
    }


    /**
     * 处理CustomException异常
     *
     * @param e 异常
     * @return 异常信息
     */
    @ExceptionHandler(CustomException.class)
    public R<String> exceptionHandler(CustomException e) {
        // 打印异常
        log.error(e.getMessage());

        // 返回
        return R.error(e.getMessage());
    }
}
