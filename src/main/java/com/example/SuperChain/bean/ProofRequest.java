package com.example.SuperChain.bean;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigInteger;
public class ProofRequest {

    private final BigInteger sk;
    private final BigInteger PK;

    @JsonCreator
    public ProofRequest(
            @JsonProperty("sk") String skString,
            @JsonProperty("PK") String pkString
    ) {
        this.sk = new BigInteger(skString);
        this.PK = new BigInteger(pkString);
    }

    // Getters (no need for setters since the fields are final)

    public BigInteger getSk() {
        return sk;
    }

    public BigInteger getPK() {
        return PK;
    }

}
