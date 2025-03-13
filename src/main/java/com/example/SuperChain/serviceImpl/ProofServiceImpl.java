package com.example.SuperChain.serviceImpl;

import com.example.SuperChain.bean.Proof;
import com.example.SuperChain.service.ProofService;
import org.bouncycastle.asn1.nist.NISTNamedCurves;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.math.ec.ECPoint;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * @author shizhishi
 */
@Service
public class ProofServiceImpl implements ProofService {

    private static final String HASH_ALGORITHM = "SHA-256";
    private final String curveName = "P-256";
    /**
     * 用于签名的椭圆曲线参数。
     */
    private final X9ECParameters curve = NISTNamedCurves.getByName(curveName);
    private final ECPoint basePoint = curve.getG();

    @Override
    public Proof generateProof(BigInteger sk, BigInteger PK) {
        // Alice: 均匀随机选择 r
        BigInteger r = generateRandomBigInteger();

        // Alice: 计算 R = r * G (G为基点)
        ECPoint R = multiplyBasePoint(r);

        // Alice: 计算 c = Hash(R, PK)
        byte[] dataToHash = concatenate(R.getEncoded(false), PK.toByteArray());
        BigInteger c = new BigInteger(1, hash(dataToHash));

        // Alice: 计算 z = r + c * sk
        BigInteger z = r.add(c.multiply(sk));

        // Alice: 生成证明 (R, z)
        return new Proof(R, z, PK);
    }

    @Override
    public boolean verifyProof(ECPoint R, BigInteger z, BigInteger PK) {
        // Bob(或者任意一个验证者)：计算 e = Hash(PK, R)
        byte[] dataForBobToHash = concatenate(PK.toByteArray(), R.getEncoded(true));
        BigInteger e = new BigInteger(1, hash(dataForBobToHash));

        // Bob(或者任意一个验证者)：验证 z * G == R + e * PK
        ECPoint leftSide = multiplyBasePoint(z);
        ECPoint rightSide = R.add(basePoint.multiply(e)); // 修正后的代码行

        return leftSide.equals(rightSide);
    }

    private BigInteger generateRandomBigInteger() {
        // 使用SecureRandom生成安全的随机数
        SecureRandom secureRandom = new SecureRandom();

        // 设置随机数的位数，根据实际需求调整
        int numBits = 256; // 示例：生成256位的大整数

        // 生成指定位数的随机大整数
        BigInteger randomBigInt = new BigInteger(numBits, secureRandom);

        return randomBigInt;
    }

    public ECPoint multiplyBasePoint(BigInteger scalar) {
        // 确保输入的标量在正确范围内
        scalar = scalar.mod(curve.getN());

        // 使用Bouncy Castle进行点乘运算
        return basePoint.multiply(scalar);
    }

    private byte[] hash(byte[] data) {
        try {
            MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM);
            return md.digest(data);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to initialize hash algorithm: " + HASH_ALGORITHM, e);
        }
    }

    private byte[] concatenate(byte[] array1, byte[] array2) {
        byte[] combined = Arrays.copyOf(array1, array1.length + array2.length);
        System.arraycopy(array2, 0, combined, array1.length, array2.length);
        return combined;
    }
}
//
//
//package com.example.chaindemo.serviceImpl;
//
//import com.baidu.xuper.crypto.ECKeyPair;
//import com.example.chaindemo.bean.Proof;
//import com.example.chaindemo.service.ProofService;
//import org.bouncycastle.asn1.nist.NISTNamedCurves;
//import org.bouncycastle.asn1.x9.X9ECParameters;
//import org.bouncycastle.math.ec.ECPoint;
//import org.springframework.stereotype.Service;
//
//import java.math.BigInteger;
//import java.nio.ByteBuffer;
//import java.security.MessageDigest;
//import java.security.NoSuchAlgorithmException;
//import java.security.SecureRandom;
//
//@Service
//public class ProofServiceImpl implements ProofService {
//
//    private static final String HASH_ALGORITHM = "SHA-256";
//    private final String curveName = "P-256";
//    /**
//     * 用于签名的椭圆曲线参数。
//     */
//    private final X9ECParameters curve = NISTNamedCurves.getByName(curveName);
//    private final ECPoint basePoint = curve.getG();
//    private ECKeyPair ecKeyPair;
//
//    @Override
//    public Proof generateProof(BigInteger sk, BigInteger PK) {
//        if (sk == null || PK == null) {
//            throw new IllegalArgumentException("私钥和公钥不能为空");
//        }
//
//        BigInteger r = generateRandomBigInteger();
//
//        BigInteger R = multiplyBasePoint(r);
//
//        byte[] dataToHash = concatenate(R.toByteArray(), PK.toByteArray());
//        BigInteger c = new BigInteger(1, hash(dataToHash));
//
//        BigInteger z = r.add(c.multiply(sk));
//
//        return new Proof(R, z, PK);
//    }
//
//    @Override
//    public boolean verifyProof(BigInteger R, BigInteger z, BigInteger PK) {
//        if (R == null || z == null || PK == null) {
//            throw new IllegalArgumentException("证明数据不能为空");
//        }
//
//        byte[] dataForBobToHash = concatenate(PK.toByteArray(), R.toByteArray());
//        BigInteger e = new BigInteger(1, hash(dataForBobToHash));
//
//        BigInteger leftSide = multiplyBasePoint(z);
//        BigInteger rightSide = R.add(e.multiply(PK));
//
//        return leftSide.equals(rightSide);
//    }
//
//    private BigInteger generateRandomBigInteger() {
//        SecureRandom secureRandom = new SecureRandom();
//        int numBits = 256; // 示例：生成256位的大整数
//        BigInteger randomBigInt = new BigInteger(numBits, secureRandom);
//        return randomBigInt;
//    }
//
//    public BigInteger multiplyBasePoint(BigInteger scalar) {
//        if (scalar == null) {
//            throw new IllegalArgumentException("标量不能为空");
//        }
//        // 确保输入的标量在正确范围内
//        scalar = scalar.mod(curve.getN());
//
//        // 转换ECPoint到其对应的X和Y坐标（BigInteger）
//        BigInteger x = basePoint.getAffineXCoord().toBigInteger();
//        BigInteger y = basePoint.getAffineYCoord().toBigInteger();
//
//        // 对X和Y分别与scalar进行模曲线阶数的点乘运算
//        BigInteger newX = x.multiply(scalar).mod(curve.getN());
//        BigInteger newY = y.multiply(scalar).mod(curve.getN());
//
//        // 返回一个新的BigInteger，通常情况下，ECPoint的坐标表示为X和Y拼接起来的字节流
//        return BigInteger.valueOf(newX.longValue()).shiftLeft(128).or(BigInteger.valueOf(newY.longValue()));
//    }
//
//
//    private byte[] hash(byte[] data) {
//        try {
//            MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM);
//            return md.digest(data);
//        } catch (NoSuchAlgorithmException e) {
//            // 记录日志或采取其他措施
//            throw new RuntimeException("Failed to initialize hash algorithm: " + HASH_ALGORITHM, e);
//        }
//    }
//
//    private byte[] concatenate(byte[] array1, byte[] array2) {
//        ByteBuffer combined = ByteBuffer.allocate(array1.length + array2.length);
//        combined.put(array1);
//        combined.put(array2);
//        return combined.array();
//    }
//}
