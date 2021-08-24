package com.airing.spring.cloud.base;

public class Constant {
    public static final String DEFAULT_CHARSET = "utf-8";
    public static final String DEFAULT_LANG = "en_US";
    public static final String LANG_SPLIT = "_";
    public static final String SIGN_KEY = "friday";

    //---------------------------------------------------------------------
    // 自定义请求头
    //---------------------------------------------------------------------
    public static final String LANGUAGE_HEAD = "x-acs-signature-language";
    public static final String TIMESTAMP_HEAD = "x-acs-signature-timestamp";
    public static final String DEVICE_ID_HEAD = "x-acs-signature-deviceid";
    public static final String APP_VERSION_HEAD = "x-acs-signature-version";
    public static final String NONCE_HEAD = "x-acs-signature-nonce";
    public static final String TOKEN_HEAD = "token";
    public static final String SIGNATURE_HEAD = "signature";
    public static final String VERSION_HEAD = "version";
    public static final String APP_ID_HEAD = "appid";

    public static final String SIGN_HEAD_PREFIX = "x-acs-signature-";

    public static final String GET_METHOD = "GET";
    public static final String POST_METHOD = "POST";


    public static final String DEFAULT_BLOOM_FILTER_NAME = "DEFAULT_BLOOM_FILTER_NAME";

    //---------------------------------------------------------------------
    // 加解密
    //---------------------------------------------------------------------
    public static final String SIGN_ALGORITHMS = "SHA1WithRSA";
    public static final String SIGN_TYPE_RSA = "RSA";
    public static final String SIGN_SHA256RSA_ALGORITHMS = "SHA256WithRSA";
    public static final String SIGN_TYPE_RSA2 = "RSA2";
}
