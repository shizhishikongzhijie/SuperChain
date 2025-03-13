package com.example.SuperChain.util;

import com.alibaba.fastjson2.JSON;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.SuperChain.bean.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author shizhishi
 * @description: Jwt工具类，生成JWT和认证
 *
 */
@Component
public class JwtUtil {

//    @Autowired
//    RedisTemplate<String, Object> redisTemplate;


    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);
    /**
     * 密钥
     */
    private static final String SECRET = "my_secret";

    /**
     * 过期时间
     **/
    private static final long EXPIRATION = 1800L;//单位为秒


    /**
     * 生成用户token,设置token超时时间
     */
    public static String createToken(String data) {
        // 创建过期时间，单位为毫秒
        // 通过当前时间加上过期时长（EXPIRATION）来计算过期时间
        Date expireDate = new Date(System.currentTimeMillis() + EXPIRATION * 1000);

        Map<String, Object> map = new HashMap<>();
        map.put("alg", "HS256");
        map.put("typ", "JWT");
        String token = JWT.create()
                .withHeader(map)// 添加头部
                //可以将基本信息放到claims中
                .withClaim("token", data)//token
                .withExpiresAt(expireDate) //超时设置,设置过期的日期
                .withIssuedAt(new Date()) //签发时间
                .sign(Algorithm.HMAC256(SECRET)); //SECRET加密
        return token;
    }

    /**
     * 校验token并解析token
     */
    public static Map<String, String> verifyToken(String token) {
        token = token.split(" ")[1];
        DecodedJWT jwt = null;
        Map<String, String> result = new HashMap<>();
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(SECRET)).build();
            jwt = verifier.verify(token);
            //是否过期
            if (jwt.getExpiresAt().before(new Date())) {
                logger.error("token过期");
                result.put("error", "TokenExpiredException");
                return result;
            }
            //decodedJWT.getClaim("属性").asString()  获取负载中的属性值
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.error("token解码异常");
            result.put("error", "TokenDecodeException");
            //解码异常则抛出异常
            return result;
        }
        Map<String, Claim> claims = jwt.getClaims();
        return claims.entrySet().stream().collect(HashMap::new, (m, v) -> m.put(v.getKey(), v.getValue().asString()), HashMap::putAll);
    }

    /**
     * 获取具体的值
     */

   public User getTokenVal(String token,RedisTemplate<String, Object> redisTemplate) throws Exception {
       if (token == null) {
           throw new IllegalArgumentException("Token cannot be null");
       }
       Map<String, String> result = verifyToken(token);
       String userString = result.get("token");
       // 打印 userString
//       System.out.println("userString: " + userString);
       User user = JSON.parseObject(userString, User.class);
       System.out.println("user: " + user.getSecretKey());
       // 获取 RSA 密钥
       if (user == null) {
           throw new RuntimeException("User not found");
       }
       if (user.getSecretKey() == null) {
           throw new RuntimeException("Secret key not found");
       }
       if(redisTemplate==null){
           throw new RuntimeException("Secret key not found in Redis");
       }
       String rsaKey = (String) redisTemplate.opsForValue().get(user.getSecretKey());
       if (rsaKey == null) {
           throw new RuntimeException("RSA key not found in Redis");
       }
       user.setPublicKey(RSAUtils.rsaDecrypt(user.getPublicKey(), rsaKey));
       user.setPrivateKey(RSAUtils.rsaDecrypt(user.getPrivateKey(), rsaKey));
       return user;
   }



}