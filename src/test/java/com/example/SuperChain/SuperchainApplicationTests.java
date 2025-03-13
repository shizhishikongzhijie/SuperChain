package com.example.SuperChain;

import com.example.SuperChain.util.AESUtils;
import com.example.SuperChain.util.ProofUtil;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

@SpringBootTest
public class SuperchainApplicationTests {
    private static final String SECRET_KEY = "/p/YLVnbGYp0nLPcPisTwP/+o8IXbVpUq8Ig42E7ugM="; // 16 bytes key for AES
    private static final String TEST_STRING = "Hello, World!";
    private String encryptedText ="bT0yWIKbrpviHcNHHj00HsRMvwMVnHfd4LGRFwOduvErYFBZAQ+bAQlvvTzmvToQV+ilbIe6sgpd/3BaaASeFFqfcTwMUD7nD3WwBEjCn38r9qEuzvUYFh9c9gzstjN6KPEXHLn/jbZa8hM4OQmSejDl4U5FhycFIB6+dNuPekYTizEEjCEd8U+0PHbbs3W7/GKFxKd6jr5o12rYRtMhie93Lh5vw56gcgyJuY7ln8G0MWGGH6cz67N9FEQ1H5pp5TVh7KbOcKQBo1gwoW6NYOLKnR60yq90BxDaRWfkw5eW/jEyy2mCIYI2WxJgt7qToXTfu/SM8sb3D5f88NT5uj8ooX0Q/P+ZJljCX6KyM0zey0ue17kIZ3W4UzMMwSv9";

    @Test
    public void contextLoads() throws Exception {
        String decryptedText = AESUtils.aesDecrypt(encryptedText, SECRET_KEY);
        System.out.println(decryptedText);
    }

    @Test
    public void testEncryptDecrypt() throws Exception {
        ProofUtil.addPublicKey("2","H4sIAAAAAAAAAHXBwRFCMQgFwHZ0JgfIAwLlhAT6L+Ef9Ki7L+AYL40jNsvmukdkq3lr5l3CoqHVu3Vy5exSqKAKhMViiKEuewk8KnzazuhO85wkpujLtMRDkVZQz0DwPlzR9zq4jAaP/qIPpj/6t/N+AO8tTQvGAAAA");
    }

}

