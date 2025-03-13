package com.example.SuperChain.bean;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.bouncycastle.math.ec.ECPoint;

import java.math.BigInteger;

public class Proof {

    private final ECPoint R;
    private final BigInteger z;

    private final BigInteger PK;
    @JsonCreator
    public Proof(
            @JsonProperty("R") ECPoint R,
            @JsonProperty("z") BigInteger z,
            @JsonProperty("PK") BigInteger PK
    ) {
        this.R = R;
        this.z = z;
        this.PK = PK;
    }
    @JsonSerialize(using = CustomECPointSerializer.class)
    public ECPoint getR() {
        return R;
    }

    public BigInteger getZ() {
        return z;
    }

    public BigInteger getPK() {
        return PK;
    }
}
