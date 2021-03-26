package com.airing.spring.cloud.provider.enums;

public enum ExceptionEnum {
    SYS_ERROR(500, "sys.error", "Error! Please contact customer service."),
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
