package com.example.SuperChain.bean;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.bouncycastle.asn1.sec.SECNamedCurves;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.Arrays;

import java.io.IOException;
import java.math.BigInteger;

public class CustomECPointDeserializer extends JsonDeserializer<ECPoint> {
    private static byte[] toUnsignedByteArray(BigInteger value) {
        byte[] bytes = value.toByteArray();
        if (bytes[0] == 0) {
            // Remove leading zero for positive numbers
            return Arrays.copyOfRange(bytes, 1, bytes.length);
        }
        return bytes;
    }

    @Override
    public ECPoint deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.readValueAsTree();
        if (!node.isArray() || node.size() != 2) {
            throw ctxt.wrongTokenException(p, JsonToken.START_ARRAY, "Expected an array with two elements for ECPoint coordinates");
        }

        BigInteger x = node.get(0).bigIntegerValue();
        BigInteger y = node.get(1).bigIntegerValue();

        // Load the parameters for the P-256 curve
        X9ECParameters params = SECNamedCurves.getByName("secp256r1");
        ECCurve.Fp curve = (ECCurve.Fp) params.getCurve();

        // Combine x and y into a single byte array for the decodePoint method
        byte[] combinedBytes = new byte[1 + 32 + 32];
        combinedBytes[0] = 0x04; // Uncompressed point format
        System.arraycopy(toUnsignedByteArray(x), 0, combinedBytes, 1, 32);
        System.arraycopy(toUnsignedByteArray(y), 0, combinedBytes, 1 + 32, 32);

        // Create the ECPoint instance using the curve's decodePoint method
        return curve.decodePoint(combinedBytes);
    }
}
