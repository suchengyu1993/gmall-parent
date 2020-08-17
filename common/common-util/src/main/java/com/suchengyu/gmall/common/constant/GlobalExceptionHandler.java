package com.suchengyu.gmall.common.constant;

import com.suchengyu.gmall.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 *统一异常处理
 * @Author: 苏成瑜
 * @CreateTime: 2020-06-23
 * @Description:
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // 全局异常处理
    @ExceptionHandler(Exception.class) //指定出现什么异常执行这个方法
    @ResponseBody   //为了返回数据
    public Result fail(Exception e){
        e.printStackTrace();
        return Result.fail().message("执行了全局异常处理");
    }

    // 特点异常处理
    @ExceptionHandler(ArithmeticException.class) //指定出现算术异常执行此方法
    @ResponseBody   //为了返回数据
    public Result fail(ArithmeticException e){
        e.printStackTrace();
        return Result.fail().message("出现算术异常,执行了特定异常处理");
    }

    // 自定义异常异常处理
    @ExceptionHandler(EduException.class) //指定出现自定义异常执行此方法
    @ResponseBody   //为了返回数据
    public Result fail(EduException e){
        log.error(ExceptionUtil.getMessage(e));
        e.printStackTrace();
        return Result.fail().code(e.getCode()).message(e.getMsg());
    }


}
