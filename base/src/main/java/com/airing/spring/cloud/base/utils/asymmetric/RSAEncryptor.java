package com.airing.spring.cloud.base.utils.asymmetric;

import com.airing.spring.cloud.base.Constant;
import com.airing.spring.cloud.base.utils.StreamUtil;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.Cipher;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class RSAEncryptor extends BaseAsymmetricEncryptor {
    /**
     * RSA最大加密明文大小(1024/8-11=117)
     */
    private static final int MAX_ENCRYPT_BLOCK_SIZE = 117;

    /**
     * RSA最大解密密文大小(1024/8=128)
     */
    private static final int MAX_DECRYPT_BLOCK_SIZE = 128;

    protected String getSignAlgorithm() {
        return Constant.SIGN_ALGORITHMS;
    }

    protected String getAsymmetricType() {
        return Constant.SIGN_TYPE_RSA;
    }

    protected int getMaxDecryptBlockSize() {
        return MAX_DECRYPT_BLOCK_SIZE;
    }

    protected int getMaxEncryptBlockSize() {
        return MAX_ENCRYPT_BLOCK_SIZE;
    }

    protected String doDecrypt(String cipherTextBase64, String charset, String privateKey) throws Exception {
        int maxDecrypt = getMaxDecryptBlockSize();
        PrivateKey priKey = getPrivateKeyFromPKCS8(Constant.SIGN_TYPE_RSA,
                new ByteArrayInputStream(privateKey.getBytes()));
        Cipher cipher = Cipher.getInstance(Constant.SIGN_TYPE_RSA);
        cipher.init(Cipher.DECRYPT_MODE, priKey);
        byte[] encryptedData = StringUtils.isEmpty(charset)
                ? Base64.decodeBase64(cipherTextBase64.getBytes())
                : Base64.decodeBase64(cipherTextBase64.getBytes(charset));
        int inputLen = encryptedData.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > maxDecrypt) {
                cache = cipher.doFinal(encryptedData, offSet, maxDecrypt);
            } else {
                cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * maxDecrypt;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();

        return StringUtils.isEmpty(charset) ? new String(decryptedData)
                : new String(decryptedData, charset);

    }

    protected String doEncrypt(String plainText, String charset, String publicKey) throws Exception {
        int maxEncrypt = getMaxEncryptBlockSize();
        PublicKey pubKey = getPublicKeyFromX509(Constant.SIGN_TYPE_RSA,
                new ByteArrayInputStream(publicKey.getBytes()));
        Cipher cipher = Cipher.getInstance(Constant.SIGN_TYPE_RSA);
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        byte[] data = StringUtils.isEmpty(charset) ? plainText.getBytes()
                : plainText.getBytes(charset);
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > maxEncrypt) {
                cache = cipher.doFinal(data, offSet, maxEncrypt);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * maxEncrypt;
        }
        byte[] encryptedData = Base64.encodeBase64(out.toByteArray());
        out.close();

        return StringUtils.isEmpty(charset) ? new String(encryptedData)
                : new String(encryptedData, charset);
    }

    protected String doSign(String content, String charset, String privateKey) throws Exception {
        PrivateKey priKey = getPrivateKeyFromPKCS8(Constant.SIGN_TYPE_RSA,
                new ByteArrayInputStream(privateKey.getBytes()));

        Signature signature = Signature.getInstance(getSignAlgorithm());

        signature.initSign(priKey);

        if (StringUtils.isEmpty(charset)) {
            signature.update(content.getBytes());
        } else {
            signature.update(content.getBytes(charset));
        }

        byte[] signed = signature.sign();

        return new String(Base64.encodeBase64(signed));
    }

    protected boolean doVerify(String content, String charset, String publicKey, String sign) throws Exception {
        PublicKey pubKey = getPublicKeyFromX509("RSA",
                new ByteArrayInputStream(publicKey.getBytes()));

        Signature signature = Signature.getInstance(getSignAlgorithm());

        signature.initVerify(pubKey);

        if (StringUtils.isEmpty(charset)) {
            signature.update(content.getBytes());
        } else {
            signature.update(content.getBytes(charset));
        }

        return signature.verify(Base64.decodeBase64(sign.getBytes()));
    }

    public static PrivateKey getPrivateKeyFromPKCS8(String algorithm,
                                                    InputStream ins) throws Exception {
        if (ins == null || StringUtils.isEmpty(algorithm)) {
            return null;
        }

        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);

        byte[] encodedKey = StreamUtil.readText(ins).getBytes();

        encodedKey = Base64.decodeBase64(encodedKey);

        return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(encodedKey));
    }

    public static PublicKey getPublicKeyFromX509(String algorithm,
                                                 InputStream ins) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);

        StringWriter writer = new StringWriter();
        StreamUtil.io(new InputStreamReader(ins), writer);

        byte[] encodedKey = writer.toString().getBytes();

        encodedKey = Base64.decodeBase64(encodedKey);

        return keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));
    }
}
