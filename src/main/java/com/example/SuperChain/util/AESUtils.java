package com.example.SuperChain.util;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * 对称加密
 * @author shizhishi
 */

public class AESUtils {

    /**
     * 生成AES密钥
     *
     * @return 生成的AES密钥
     * @throws NoSuchAlgorithmException 如果没有提供AES算法实现
     */
    public static SecretKey generateAESKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256); // 生成 256 位的密钥
        return keyGen.generateKey();
    }



    /**
     * 将SecretKey转换为Base64编码的字符串
     *
     * @param secretKey 要转换的SecretKey
     * @return 转换后的Base64编码字符串
     */
    public static String getSecretKeyString(SecretKey secretKey) {
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }

    /**
     * 使用AES算法加密文本
     *
     * @param text 要加密的文本
     * @param secretKeyStr Base64编码的AES密钥字符串
     * @return 加密后的字符串
     * @throws Exception 如果加密过程中发生错误
     */
    public static String aesEncrypt(String text, String secretKeyStr) throws Exception {
        byte[] decodedKey = Base64.getDecoder().decode(secretKeyStr.getBytes());
        SecretKeySpec secretKeySpec = new SecretKeySpec(decodedKey, "AES");

        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);

        byte[] encryptedBytes = cipher.doFinal(text.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    /**
     * 使用AES算法解密文本
     *
     * @param encryptedText Base64编码的加密文本
     * @param secretKeyStr Base64编码的AES密钥字符串
     * @return 解密后的字符串
     * @throws Exception 如果解密过程中发生错误
     */
    public static String aesDecrypt(String encryptedText, String secretKeyStr) throws Exception {
        byte[] decodedKey = Base64.getDecoder().decode(secretKeyStr);
        SecretKeySpec secretKeySpec = new SecretKeySpec(decodedKey, "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);

        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedText);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return new String(decryptedBytes,StandardCharsets.UTF_8);
    }


}