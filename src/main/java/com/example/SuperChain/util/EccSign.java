package com.example.SuperChain.util;

import com.baidu.xuper.crypto.Ecc;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequenceGenerator;
import org.bouncycastle.asn1.nist.NISTNamedCurves;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.signers.ECDSASigner;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.encoders.Hex;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;

/**
 * 这个类用于使用椭圆曲线密码学（ECC）生成数字签名。
 */
public class EccSign extends Ecc {


    /**
     * 用于签名的椭圆曲线名称。
     */
    static final String curveName = "P-256";
    /**
     * 用于签名的椭圆曲线参数。
     */
    static final X9ECParameters curve = NISTNamedCurves.getByName(curveName);
    /**
     * 用于签名的域参数。
     */
    static final ECDomainParameters domain = new ECDomainParameters(curve.getCurve(), curve.getG(), curve.getN(), curve.getH());

    /**
     * 公钥字符串
     */
    static final String PublicKey = "f6273ec9574b940b9b68b310f85f92dfacbf9e39ad8272e6da1e081699f177bd,c5f3b7691a816a250f84798601bfcb0fe96f1bf17576c2bf759daba1a8a29d41";
    /**
     * 私钥字符串
     */
    static final BigInteger PrivateKey =new BigInteger("5639104732628095548736271505281764947360298485635428659529823004166529229050");


    /**
     * 使用给定私钥对指定哈希值生成数字签名。
     *
     * @param hash       需要签名的哈希值
     * @param privateKey 用于签名的私钥
     * @return 生成的数字签名
     * @throws IOException 如果发生I/O错误时抛出异常
     */
    public static byte[] sign(byte[] hash, BigInteger privateKey) throws IOException {
        // 创建一个ECDSASigner实例，用于生成签名
        ECDSASigner signer = new ECDSASigner();
        // 初始化签名器，设置为签名模式，并传入私钥参数
        signer.init(true, new ECPrivateKeyParameters(privateKey, domain));
        // 生成签名
        BigInteger[] signature = signer.generateSignature(hash);
        // 创建一个字节数组输出流，用于存储序列化后的签名数据
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // 创建一个DERSequenceGenerator，用于将签名序列化为DER编码格式
        DERSequenceGenerator seq = new DERSequenceGenerator(baos);
        // 将签名的两个部分添加到序列中
        seq.addObject(new ASN1Integer(signature[0]));
        seq.addObject(new ASN1Integer(signature[1]));
        // 关闭序列生成器，完成序列化过程
        seq.close();
        // 返回序列化后的签名数据
        return baos.toByteArray();
    }

    /**
     * 验证给定的数字签名是否与公钥和原始消息的哈希值匹配。
     *
     * @param hash      原始消息的哈希值
     * @param signature 数字签名
     * @param publicKey 用于验证签名的公钥
     * @return 如果签名有效则返回true，否则返回false
     */
    public static boolean verifySignature(byte[] hash, byte[] signature, ECPoint publicKey) {
        try {
            // 使用ECDSASigner实例进行签名验证
            ECDSASigner verifier = new ECDSASigner();

            // 初始化验证器，设置为验证模式，并传入公钥参数
            CipherParameters pubKeyParams = new ECPublicKeyParameters(publicKey, domain);
            verifier.init(false, pubKeyParams);

            // 解析DER编码的签名数据
            ASN1Sequence asn1Seq = ASN1Sequence.getInstance(signature);
            BigInteger r = ((ASN1Integer) asn1Seq.getObjectAt(0)).getValue();
            BigInteger s = ((ASN1Integer) asn1Seq.getObjectAt(1)).getValue();

            // 执行签名验证
            return verifier.verifySignature(hash, r, s);
        } catch (Exception e) {
            // 异常通常意味着签名验证失败
            return false;
        }
    }
    /**
     * 解析公钥
     * 此方法用于将表示椭圆曲线上的点的x和y坐标（以十六进制字符串形式提供）解析为ECPoint对象。
     *
     * @param xHex 表示椭圆曲线点的x坐标的十六进制字符串。
     * @param yHex 表示椭圆曲线点的y坐标的十六进制字符串。
     * @return ECPoint对象，表示解析后的椭圆曲线点。
     */
    public static ECPoint parsePublicKey(String xHex, String yHex) {
        // 将十六进制字符串转换为 BigInteger 对象
        BigInteger x = new BigInteger(xHex, 16);
        BigInteger y = new BigInteger(yHex, 16);

        // 使用椭圆曲线参数将 BigInteger 转换为 ECFieldElement 对象
        ECFieldElement xField = curve.getCurve().fromBigInteger(x);
        ECFieldElement yField = curve.getCurve().fromBigInteger(y);

        // 将 ECFieldElement 对象转换为 ECPoint 对象
        return curve.getCurve().createPoint(xField.toBigInteger(), yField.toBigInteger());
    }


    public static void main(String[] args) throws IOException {
        EccSign eccSign = new EccSign();
        BigInteger privateKey = new BigInteger("5639104732628095548736271505281764947360298485635428659529823004166529229050");
        String message = "helloWorld";
        byte[] hash = message.getBytes();
        byte[] signMessage = sign(hash, privateKey);
        System.out.println("signMessage: " + Hex.toHexString(signMessage));

        // 解析提供的公钥字符串
        String publicKeyStr = "f6273ec9574b940b9b68b310f85f92dfacbf9e39ad8272e6da1e081699f177bd,c5f3b7691a816a250f84798601bfcb0fe96f1bf17576c2bf759daba1a8a29d41";
        String[] coords = publicKeyStr.split(",");
        String xHex = coords[0];
        String yHex = coords[1];
        // 使用 ECFieldElement 对象创建 ECPoint
        ECPoint publicKey = parsePublicKey(xHex, yHex);
        // 验证签名
        boolean isValid = verifySignature(hash, signMessage, publicKey);
        System.out.println("Signature is valid: " + isValid);
    }
    //私钥加密
    public String privateEncrypt(String data) throws IOException {
        byte[] hash = data.getBytes();
        byte[] signMessage = sign(hash);
        System.out.println("signMessage: " + Hex.toHexString(signMessage));
        return Hex.toHexString(signMessage);
    }
    //公钥解密
    public Boolean publicDecrypt(String signedData) throws IOException {
        String[] coords = PublicKey.split(",");
        String xHex = coords[0];
        String yHex = coords[1];
        // 使用 ECFieldElement 对象创建 ECPoint
        ECPoint publicKey = parsePublicKey(xHex, yHex);
        byte[] hash = signedData.getBytes();
        // 验证签名
        boolean isValid = verifySignature(hash, signedData.getBytes(), publicKey);
        System.out.println("是否未被篡改: " + isValid);
        return isValid;
    }

    private byte[] sign(byte[] hash) throws IOException {
        return sign(hash, PrivateKey);
    }
}
