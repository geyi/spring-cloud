package com.airing.spring.cloud.provider.config;

import com.airing.spring.cloud.provider.enums.ExceptionEnum;

import java.io.Serializable;

public class BusinessException extends RuntimeException implements Serializable {
    /** 错误码 */
    private Integer code;
    /** 错误消息 */
    private String msg;
    /** 异常枚举 */
    private ExceptionEnum exceptionEnum;

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public ExceptionEnum getExceptionEnum() {
        return exceptionEnum;
    }

    public BusinessException(ExceptionEnum exceptionEnum) {
        super(exceptionEnum.getMsg());
        this.exceptionEnum = exceptionEnum;
        this.code = exceptionEnum.getCode();
        this.msg = exceptionEnum.getMsg();
    }

    public BusinessException(ExceptionEnum exceptionEnum, Throwable e) {
        super(exceptionEnum.getMsg(), e);
        this.exceptionEnum = exceptionEnum;
        this.code = exceptionEnum.getCode();
        this.msg = exceptionEnum.getMsg();
    }
}
