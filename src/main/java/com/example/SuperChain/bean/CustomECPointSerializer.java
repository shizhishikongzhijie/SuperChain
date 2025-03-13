package com.example.SuperChain.bean;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.bouncycastle.math.ec.ECPoint;

import java.io.IOException;
import java.math.BigInteger;

public class CustomECPointSerializer extends JsonSerializer<ECPoint> {
    @Override
    public void serialize(ECPoint ecPoint, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        // 假设要序列化的是未压缩坐标 (x, y)
        BigInteger x = ecPoint.getXCoord().toBigInteger();
        BigInteger y = ecPoint.getYCoord().toBigInteger();

        gen.writeStartArray();
        gen.writeNumber(x);
        gen.writeNumber(y);
        gen.writeEndArray();
    }
}