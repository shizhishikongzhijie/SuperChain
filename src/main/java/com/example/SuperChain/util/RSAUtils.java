package com.example.SuperChain.util;

import com.example.SuperChain.bean.RSAKey;

import javax.crypto.Cipher;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * @author shizhishi
 */
public class RSAUtils {
    public static String  cleanKeyStr(String publicKeyStr){
        return  publicKeyStr
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s+", ""); // 移除所有空格和换行符
    }

    public static RSAKey generateRSAKeyPair() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048); // 你可以根据需要调整密钥长度
        KeyPair RSAKey = keyPairGenerator.generateKeyPair();
        RSAKey rsaKey = new RSAKey();
        rsaKey.setPublicKey(getPublicKeyAsString(RSAKey.getPublic()));
        rsaKey.setPrivateKey(getPrivateKeyAsString(RSAKey.getPrivate()));
        return rsaKey;
    }

    private static String getPublicKeyAsString(PublicKey publicKey) {
        byte[] publicKeyBytes = publicKey.getEncoded();
        return Base64.getEncoder().encodeToString(publicKeyBytes);
    }

    private static String getPrivateKeyAsString(PrivateKey privateKey) {
        byte[] privateKeyBytes = privateKey.getEncoded();
        return Base64.getEncoder().encodeToString(privateKeyBytes);
    }

    public static String rsaEncrypt(String text, String publicKeyStr) throws Exception {
        // 去掉 PEM 格式的头尾注释行和换行符
        String cleanedPublicKeyStr = cleanKeyStr(publicKeyStr);

        byte[] encodedKey = Base64.getDecoder().decode(cleanedPublicKeyStr);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encodedKey);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(keySpec);

        // 加密操作
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);

        byte[] encryptedBytes = cipher.doFinal(text.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public static String rsaDecrypt(String encryptedText, String privateKeyStr) throws Exception {
        // 去掉 PEM 格式的头尾注释行和换行符
        String cleanedPrivateKeyStr = cleanKeyStr(privateKeyStr);

        byte[] encodedKey = Base64.getDecoder().decode(cleanedPrivateKeyStr);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encodedKey);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);

        // 解密操作
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);

        byte[] decodedBytes = Base64.getDecoder().decode(encryptedText);
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);
        return new String(decryptedBytes);
    }

}
