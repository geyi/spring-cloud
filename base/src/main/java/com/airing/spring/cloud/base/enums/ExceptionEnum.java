package com.airing.spring.cloud.base.enums;

/**
 * 异常枚举类
 *
 * @author GEYI
 * @date 2021年03月31日 9:51
 */
public enum ExceptionEnum {
    SYS_ERROR(5000, "sys.error", "Error! Please contact customer service."),
    NOT_SUPPORT_ERROR(5002, "not.support.error", "不支持的操作{}"),

    PARAM_ERROR(4000, "param.error", "parameter {0} is error!"),
    SIGN_ERROR(4001, "sign.error", "params sign error!"),
    AUTH_ERROR(403, "auth.error", "login"),
    LIMIT_ERROR(4004, "limit.error", "limit"),

    JUJU(1, "juju", "Error！Error！"),
    ;

    /** 错误码 */
    private Integer code;
    /** 错误key */
    private String key;
    /** 错误描述 */
    private String msg;

    public Integer getCode() {
        return code;
    }

    public String getKey() {
        return key;
    }

    public String getMsg() {
        return msg;
    }

    ExceptionEnum(Integer code, String key, String msg) {
        this.code = code;
        this.key = key;
        this.msg = msg;
    }
}
