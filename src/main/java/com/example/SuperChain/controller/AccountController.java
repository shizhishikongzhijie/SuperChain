package com.example.SuperChain.controller;

import com.alibaba.fastjson2.JSONObject;
import com.example.SuperChain.bean.SqlUsers;
import com.example.SuperChain.bean.User;
import com.example.SuperChain.serviceImpl.AccountServiceImpl;
import com.example.SuperChain.util.AESUtils;
import com.example.SuperChain.util.JwtUtil;
import com.example.SuperChain.util.ProofUtil;
import com.example.SuperChain.util.RSAUtils;
import com.example.SuperChain.util.resUtils.R;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * @author shizhishi
 */
@RestController
public class AccountController {
    Logger logger = LoggerFactory.getLogger(AccountController.class);
    ObjectMapper objectMapper = new ObjectMapper();


    @Autowired
    AccountServiceImpl accountService;

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @GetMapping("/sendCaptcha")
    public String sendCaptcha(String email,HttpServletRequest request) throws Exception {
        String linkKey = (String) request.getAttribute("linkKey");
        logger.info("email: {}", email);
        return  AESUtils.aesEncrypt(accountService.sendCaptcha(email), linkKey);
    }
    //注册
    @GetMapping("/register")
    public String register(String linkId, String type, String data) throws Exception {
        logger.info("user: {}",data);
        logger.info("linkId: {}", linkId);
        String linkKey = (String) redisTemplate.opsForValue().get(linkId);
        //解析
        String decrypted = null;
        if (linkKey != null) {
            decrypted = AESUtils.aesDecrypt(data, linkKey);
        }else{
            logger.info("链接已过期");
        }
        //得出解析后的数据（用户登录信息）
        SqlUsers result = objectMapper.readValue(decrypted, SqlUsers.class);
        String email = result.getEmail();
        String captcha = result.getCaptcha();
        logger.info("email: {}", email);
        logger.info("captcha: {}", captcha);
        return AESUtils.aesEncrypt(accountService.register(email, captcha), linkKey) ;
    }

    // 登录
    @GetMapping("/login")
    public String login(String linkId, String type, String data) throws Exception {
        logger.info("user: {}", data);
        logger.info("linkId: {}", linkId);
        String linkKey = (String) redisTemplate.opsForValue().get(linkId);
        //解析
        String decrypted = null;
        if (linkKey != null) {
            decrypted = AESUtils.aesDecrypt(data, linkKey);
        }else{
            logger.info("链接已过期");
        }
        //得出解析后的数据（用户登录信息）
        User result = objectMapper.readValue(decrypted, User.class);
        User user = new User(result.getPublicKey(), result.getPrivateKey());
        logger.info("验证用户信息 up decrypted : {}", decrypted);
        return AESUtils.aesEncrypt(accountService.login(user,linkKey), linkKey);
    }

    //创建单个账户

    @GetMapping("/createAccount")
    public String createAccount() {
        return accountService.createAccount();
    }

    //创建多个账户
    @GetMapping("/creatAccountList")
    public Map<String, String> creatAccountList(int num) {
        return accountService.createAccountList(num);
    }

}
