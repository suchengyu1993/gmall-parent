package com.suchengyu.gmall.common.constant;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * EduException
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-06-23
 * @Description:
 */
@Data
@AllArgsConstructor //生成有参构造方法
@NoArgsConstructor  //生成无参构造方法
public class EduException extends RuntimeException {

    @ApiModelProperty(value = "状态码")
    private Integer code;   //状态吗
    private String msg;     //异常信息


    @Override
    public String toString() {
        return "EduException{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                '}';
    }
}
