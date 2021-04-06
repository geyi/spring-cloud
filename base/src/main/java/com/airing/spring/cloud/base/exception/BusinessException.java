package com.airing.spring.cloud.base.exception;

import com.airing.spring.cloud.base.enums.ExceptionEnum;

import java.io.Serializable;

/**
 * 业务异常
 *
 * @author GEYI
 * @date 2021年03月31日 9:51
 */
public class BusinessException extends RuntimeException implements Serializable {

    /** 错误码 */
    private Integer code;
    /** 错误消息 */
    private String msg;
    /** 异常枚举 */
    private ExceptionEnum exceptionEnum;
    /** 错误消息中的动态参数 */
    private String[] args;

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public ExceptionEnum getExceptionEnum() {
        return exceptionEnum;
    }

    public String[] getArgs() {
        return args;
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

    public BusinessException(ExceptionEnum exceptionEnum, String... args) {
        super(exceptionEnum.getMsg());
        this.exceptionEnum = exceptionEnum;
        this.code = exceptionEnum.getCode();
        this.msg = exceptionEnum.getMsg();
        this.args = args;
    }
}
