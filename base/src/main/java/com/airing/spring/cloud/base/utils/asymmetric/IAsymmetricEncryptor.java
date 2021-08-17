package com.airing.spring.cloud.base.utils.asymmetric;

public interface IAsymmetricEncryptor {
    String sign(String var1, String var2, String var3);

    boolean verify(String var1, String var2, String var3, String var4);

    String encrypt(String var1, String var2, String var3);

    String decrypt(String var1, String var2, String var3);
}
