package com.example.SuperChain.bean;

import com.baidu.xuper.crypto.ECKeyPair;
import com.baidu.xuper.crypto.Ecc;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.bouncycastle.math.ec.ECPoint;

import java.math.BigInteger;

/**
 * @author shizhishi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Account {
    private ECPoint publicKey;
    private BigInteger privateKey;
}
