package com.example.SuperChain.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Hex;
import org.springframework.util.StringUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Scanner;

/**
 * 谷歌身份验证器工具类
 * @author shizhishi
 */
public class GoogleAuthenticator {

    /**
     * 时间前后偏移量
     * 用于防止客户端时间不精确导致生成的TOTP与服务器端的TOTP一直不一致
     * 如果为0,当前时间为 10:10:15
     * 则表明在 10:10:00-10:10:30 之间生成的TOTP 能校验通过
//     * 如果为1,则表明在
     * 10:09:30-10:10:00
     * 10:10:00-10:10:30
     * 10:10:30-10:11:00 之间生成的TOTP 能校验通过
     * 以此类推
     */
    private static int WINDOW_SIZE = 0;

    /**
     * 加密方式，HmacSHA1、HmacSHA256、HmacSHA512
     */
    private static String CRYPTO = "HmacSHA1";

    /**
     * 生成密钥，每个用户独享一份密钥
     *
     * @return
     */
    public static String getSecretKey() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);
        Base32 base32 = new Base32();
        String secretKey = base32.encodeToString(bytes);
        // make the secret key more human-readable by lower-casing and
        // inserting spaces between each group of 4 characters
        return secretKey.toUpperCase();
    }

    /**
     * 生成二维码内容
     *
     * @param secretKey 密钥
     * @param account   账户名
     * @param issuer    网站地址（可不写）
     * @return
     */
    public static String getQrCodeText(String secretKey, String account, String issuer) {
        String normalizedBase32Key = secretKey.replace(" ", "").toUpperCase();
        try {
            return "otpauth://totp/"
                    + URLEncoder.encode((!StringUtils.isEmpty(issuer) ? (issuer + ":") : "") + account, "UTF-8").replace("+", "%20")
                    + "?secret=" + URLEncoder.encode(normalizedBase32Key, "UTF-8").replace("+", "%20")
                    + (!StringUtils.isEmpty(issuer) ? ("&issuer=" + URLEncoder.encode(issuer, "UTF-8").replace("+", "%20")) : "");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * 获取验证码
     *
     * @param secretKey
     * @return
     */
    public static String getCode(String secretKey) {
        String normalizedBase32Key = secretKey.replace(" ", "").toUpperCase();
        Base32 base32 = new Base32();
        byte[] bytes = base32.decode(normalizedBase32Key);
        String hexKey = Hex.encodeHexString(bytes);
        long time = (System.currentTimeMillis() / 1000) / 30;
        String hexTime = Long.toHexString(time);
        return TOTP.generateTOTP(hexKey, hexTime, "6", CRYPTO);
    }

    /**
     * 检验 code 是否正确
     *
     * @param secret 密钥
     * @param code   code
     * @param time   时间戳
     * @return
     */
    public static boolean checkCode(String secret, long code, long time) {
        Base32 codec = new Base32();
        byte[] decodedKey = codec.decode(secret);
        // convert unix msec time into a 30 second "window"
        // this is per the TOTP spec (see the RFC for details)
        long t = (time / 1000L) / 30L;
        // Window is used to check codes generated in the near past.
        // You can use this value to tune how far you're willing to go.
        long hash;
        for (int i = -WINDOW_SIZE; i <= WINDOW_SIZE; ++i) {
            try {
                hash = verifyCode(decodedKey, t + i);
            } catch (Exception e) {
                // Yes, this is bad form - but
                // the exceptions thrown would be rare and a static
                // configuration problem
                // e.printStackTrace();
                throw new RuntimeException(e.getMessage());
            }
            if (hash == code) {
                return true;
            }
        }
        return false;
    }
    /**
     * 根据时间偏移量计算
     *
     * @param key
     * @param t
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    private static long verifyCode(byte[] key, long t) throws NoSuchAlgorithmException, InvalidKeyException {
        byte[] data = new byte[8];
        long value = t;
        for (int i = 8; i-- > 0; value >>>= 8) {
            data[i] = (byte) value;
        }
        SecretKeySpec signKey = new SecretKeySpec(key, CRYPTO);
        Mac mac = Mac.getInstance(CRYPTO);
        mac.init(signKey);
        byte[] hash = mac.doFinal(data);
        int offset = hash[20 - 1] & 0xF;
        // We're using a long because Java hasn't got unsigned int.
        long truncatedHash = 0;
        for (int i = 0; i < 4; ++i) {
            truncatedHash <<= 8;
            // We are dealing with signed bytes:
            // we just keep the first byte.
            truncatedHash |= (hash[offset + i] & 0xFF);
        }
        truncatedHash &= 0x7FFFFFFF;
        truncatedHash %= 1000000;
        return truncatedHash;
    }

    //在控制台打印二维码
    public static void printQRcode(String content) {
        int width = 1; // 二维码宽度
        int height = 1; // 二维码高度

        // 定义二维码的参数
        HashMap<EncodeHintType, Serializable> hints = new HashMap<EncodeHintType, java.io.Serializable>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");//编码方式
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);//纠错等级

        // 打印二维码
        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);
            for (int j = 0; j < bitMatrix.getHeight(); j++) {
                for (int i = 0; i < bitMatrix.getWidth(); i++) {
                    if (bitMatrix.get(i, j)) {
                        System.out.print("■");
                    } else {
                        System.out.print("  ");
                    }

                }
                System.out.println();
            }
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
            String secretKey = getSecretKey();
            System.out.println("secretKey：" + secretKey);
            //生成二维码
            printQRcode(getQrCodeText(secretKey, "admin", ""));
            //等待输入
            Scanner scanner = new Scanner(System.in);
            System.out.println("请输入用户名：");
            String username = scanner.nextLine();
            String code = getCode(username);
            System.out.println("code：" + code);
            boolean b = checkCode(username, Long.parseLong(code), System.currentTimeMillis());
            System.out.println("isSuccess：" + b);
    }
}