package com.airing.spring.cloud.base.utils.asymmetric;

import org.apache.commons.lang3.StringUtils;

public abstract class BaseAsymmetricEncryptor implements IAsymmetricEncryptor {
    private static String DEFAULT_CHARSET = "UTF-8";

    public BaseAsymmetricEncryptor() {
    }

    public String decrypt(String cipherTextBase64, String charset, String privateKey) throws RuntimeException {
        try {
            if (StringUtils.isEmpty(cipherTextBase64)) {
                throw new RuntimeException("密文不可为空");
            } else if (StringUtils.isEmpty(privateKey)) {
                throw new RuntimeException("私钥不可为空");
            } else {
                if (StringUtils.isEmpty(charset)) {
                    charset = DEFAULT_CHARSET;
                }

                return this.doDecrypt(cipherTextBase64, charset, privateKey);
            }
        } catch (Exception var6) {
            String errorMessage = this.getAsymmetricType() + "非对称解密遭遇异常，请检查私钥格式是否正确。" + var6.getMessage() + " cipherTextBase64=" + cipherTextBase64 + "，charset=" + charset + "，privateKeySize=" + privateKey.length();
            throw new RuntimeException(errorMessage, var6);
        }
    }

    public String encrypt(String plainText, String charset, String publicKey) throws RuntimeException {
        try {
            if (StringUtils.isEmpty(plainText)) {
                throw new RuntimeException("密文不可为空");
            } else if (StringUtils.isEmpty(publicKey)) {
                throw new RuntimeException("公钥不可为空");
            } else {
                if (StringUtils.isEmpty(charset)) {
                    charset = DEFAULT_CHARSET;
                }

                return this.doEncrypt(plainText, charset, publicKey);
            }
        } catch (Exception var6) {
            String errorMessage = this.getAsymmetricType() + "非对称解密遭遇异常，请检查公钥格式是否正确。" + var6.getMessage() + " plainText=" + plainText + "，charset=" + charset + "，publicKey=" + publicKey;
            throw new RuntimeException(errorMessage, var6);
        }
    }

    public String sign(String content, String charset, String privateKey) throws RuntimeException {
        try {
            if (StringUtils.isEmpty(content)) {
                throw new RuntimeException("待签名内容不可为空");
            } else if (StringUtils.isEmpty(privateKey)) {
                throw new RuntimeException("私钥不可为空");
            } else {
                if (StringUtils.isEmpty(charset)) {
                    charset = DEFAULT_CHARSET;
                }

                return this.doSign(content, charset, privateKey);
            }
        } catch (Exception var6) {
            String errorMessage = this.getAsymmetricType() + "签名遭遇异常，请检查私钥格式是否正确。" + var6.getMessage() + " content=" + content + "，charset=" + charset + "，privateKeySize=" + privateKey.length();
            throw new RuntimeException(errorMessage, var6);
        }
    }

    public boolean verify(String content, String charset, String publicKey, String sign) throws RuntimeException {
        try {
            if (StringUtils.isEmpty(content)) {
                throw new RuntimeException("待验签内容不可为空");
            } else if (StringUtils.isEmpty(publicKey)) {
                throw new RuntimeException("公钥不可为空");
            } else if (StringUtils.isEmpty(sign)) {
                throw new RuntimeException("签名串不可为空");
            } else {
                if (StringUtils.isEmpty(charset)) {
                    charset = DEFAULT_CHARSET;
                }

                return this.doVerify(content, charset, publicKey, sign);
            }
        } catch (Exception var7) {
            String errorMessage = this.getAsymmetricType() + "验签遭遇异常，请检查公钥格式是否正确。" + var7.getMessage() + " content=" + content + "，charset=" + charset + "，publicKey=" + publicKey;
            throw new RuntimeException(errorMessage, var7);
        }
    }

    protected abstract String doDecrypt(String var1, String var2, String var3) throws Exception;

    protected abstract String doEncrypt(String var1, String var2, String var3) throws Exception;

    protected abstract String doSign(String var1, String var2, String var3) throws Exception;

    protected abstract boolean doVerify(String var1, String var2, String var3, String var4) throws Exception;

    protected abstract String getAsymmetricType();
}
