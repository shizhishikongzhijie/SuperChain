package com.example.SuperChain.bean;

import lombok.*;
import org.springframework.context.annotation.Bean;

/**
 * @author shizhishi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class User {
   //Lombok 自动生成的 getter 和 setter 方法会遵循 Java 的命名规范，即首字母小写。
   // 因此，即使你在类中定义了 PublicKey 和 privateKey，
   // Lombok 自动生成的方法名称将是 publicKey 和 privateKey。
    private String publicKey;
    private String privateKey;
    private String secretKey;//Authenticator密钥

    public User(String publicKey, String privateKey) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    public String getSecretKey(String publicKey) {
        return secretKey;
    }

    public void saveUserCredentials(String publicKey, String secretKey) {
        this.publicKey = publicKey;
        this.secretKey = secretKey;
    }
}
