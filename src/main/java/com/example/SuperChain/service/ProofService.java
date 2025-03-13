package com.example.SuperChain.service;
//导入椭圆曲线

import com.example.SuperChain.bean.Proof;
import org.bouncycastle.math.ec.ECPoint;

import java.math.BigInteger;

public interface ProofService {

    /**
     * Alice: 均匀随机选择 r，并依次计算：
     * R = r * G
     * c = Hash(R, PK)
     * z = r + c * sk
     * 返回生成的证明 (R, z)
     *
     * @param sk Alice 的私钥
     * @param PK Alice 的公钥
     * @return 生成的证明 (R, z)
     */
    Proof generateProof(BigInteger sk, BigInteger PK);

    /**
     * Bob(或者任意一个验证者)：计算 e = Hash(PK, R)，并验证 z * G == R + c * PK
     *
     * @param R Alice 生成的证明 (R, z)
     * @param z  Alice 生成的证明 (R, z)
     * @param PK Alice 的公钥
     * @return 验证结果，true 表示验证成功，false 表示验证失败
     */
    boolean verifyProof(ECPoint R, BigInteger z, BigInteger PK);
}
