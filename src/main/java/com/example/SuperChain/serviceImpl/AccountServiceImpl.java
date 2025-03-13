package com.example.SuperChain.serviceImpl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baidu.xuper.crypto.ECKeyPair;
import com.example.SuperChain.SuperChainApplication;
import com.example.SuperChain.bean.RSAKey;
import com.example.SuperChain.bean.User;
import com.example.SuperChain.mapper.ReactMapper;
import com.example.SuperChain.service.AccountService;
import com.example.SuperChain.util.*;
import com.example.SuperChain.util.cache.PoetryCache;
import com.example.SuperChain.util.resUtils.R;
import com.fasterxml.jackson.core.JsonProcessingException;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.bouncycastle.crypto.engines.RSAEngine;
import org.bouncycastle.math.ec.ECPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.StringWriter;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import static com.alibaba.fastjson2.JSONWriter.Feature.LargeObject;


/**
 * @author shizhishi
 */
@Service
public class AccountServiceImpl implements AccountService {
    @Resource
    private final ReactMapper reactMapper;
    private final MailUtils mailUtil;

    @Autowired
    RedisTemplate<String, Object> redisTemplate;
    Logger log = LoggerFactory.getLogger(AccountServiceImpl.class);

    public AccountServiceImpl(ReactMapper reactMapper, MailUtils mailUtil) {
        this.reactMapper = reactMapper;
        this.mailUtil = mailUtil;
    }

    private static StringWriter getStringWriter(String email, int i) throws IOException, TemplateException {
        Map<String, Object> model = new HashMap<>();
        model.put("name", "shizhishi");
        model.put("email", email);
        model.put("captcha", i);

        // 配置Freemarker
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_0); // 创建模板配置对象，指定模板配置版本为2.3.0
        ClassLoader loader = SuperChainApplication.class.getClassLoader(); // 获取DemoApplication类的类加载器
        configuration.setClassLoaderForTemplateLoading(loader, "ftl"); // 设置模板配置对象的类加载器为上述获取到的类加载器，并指定模板文件夹路径为"ftl"


        // 获取模板
        Template template = configuration.getTemplate("mailTemplate.ftl");

        // 创建StringWriter对象，用于将模板渲染后的结果保存
        StringWriter text = new StringWriter();

        // 将模型传递给模板进行渲染
        template.process(model, text);
        return text;
    }

    // 创建账户
    @Override
    public String createAccount() {
        ECKeyPair ecKeyPair = ECKeyPair.create();
        ECPoint publicKey = ecKeyPair.getPublicKey();
        BigInteger privateKey = ecKeyPair.getPrivateKey();
        System.out.println("---创建账户---");
        System.out.println("公钥：" + publicKey);
        System.out.println("私钥：" + privateKey);
        JSONObject json = new JSONObject();
        JSON.config(LargeObject, true);
        json.put("PublicKey", publicKey.toString());
        json.put("PrivateKey", privateKey.toString());
        return json.toJSONString();
    }

//    private String getCodeMail(int i) {
//        return String.format(mailUtil.getMailText(),
//                "SuperChain",
//                String.format(MailUtils.imMail, PoetryUtil.getAdminUser().getUsername()),
//                PoetryUtil.getAdminUser().getUsername(),
//                String.format(codeFormat, i),
//                "",
//                "SuperChain");
//    }

    @Override
    public Map<String, String> createAccountList(int num) {
        Map<String, String> map = new TreeMap<>();
        System.out.println("---创建账户列表---");
        for (int i = 0; i < num; i++) {
            ECKeyPair ecKeyPair = ECKeyPair.create();
            ECPoint publicKey = ecKeyPair.getPublicKey();
            BigInteger privateKey = ecKeyPair.getPrivateKey();
            System.out.println("---创建账户---");
            System.out.println("公钥：" + publicKey);
            System.out.println("私钥：" + privateKey);
            map.put(publicKey.toString(), privateKey.toString());
        }
        return map;
    }

    @Override
    public String sendCaptcha(String email) {
        // TODO: 发送验证码
        try {
            if (PoetryCache.get(email) == null) {
                System.out.println("---发送验证码---");
                int i = new Random().nextInt(900000) + 100000;
                // 创建模型，用于传递给Freemarker模板
                StringWriter text = getStringWriter(email, i);
                mailUtil.sendHtmlMail(email, "验证码", text.toString());
                PoetryCache.put(email, i, 300);
                return R.captchaSendOk().toJSONString();
            } else {
                return R.captchaFrequent().toJSONString();
            }
        } catch (Exception e) {
            log.error("验证码发送失败！", e);
            return  R.captchaSendFail().toJSONString();
        }
    }

    @Override
    public String login(User user, String linkKey) throws Exception {
        int isLogin = ProofUtil.loginCheck(user);
        // 验证用户信息 up decrypted : return true or false
        //需要1：验证公私钥（用于登录）是否存在且匹配 ，返回信息 0（（公钥）用户不存在） or 1（匹配）or 2（不匹配，既私钥错误）
        //需要2：上传公私钥（用于注册），返回信息 0（（公钥）用户已存在） or 1（创建成功）

        log.info("验证用户信息 up : {}", user);

        //示例
        if(isLogin==1) {
            Map<String, Object> resData = new HashMap<>();
            //生成加密token信息的RSA
            RSAKey rsaKey = RSAUtils.generateRSAKeyPair();
            user.setSecretKey(rsaKey.getPublicKey());
            user.setPublicKey(RSAUtils.rsaEncrypt(user.getPublicKey(),rsaKey.getPublicKey()));
            user.setPrivateKey(RSAUtils.rsaEncrypt(user.getPrivateKey(),rsaKey.getPublicKey()));
            redisTemplate.opsForValue().set(rsaKey.getPublicKey(), rsaKey.getPrivateKey(),1, TimeUnit.DAYS);
            JSONObject jsonObj = (JSONObject) JSON.toJSON(user);
            String jsonStr = jsonObj.toString();
            resData.put("token", JwtUtil.createToken(jsonStr));
            return R.loginOk().data(resData).toJSONString();
        }else{
            if(isLogin==0)
                return R.userNotExist().toJSONString();
            else
                return R.privateKeyError().toJSONString();
        }
    }

    @Override
    public String register(String email, String captcha) throws JsonProcessingException {
        // TODO: 验证码验证
        System.out.println("---注册---");
        Object cacheCaptcha;
        log.info("邮箱：{}", email);
        if (PoetryCache.get(email) == null) {
            return R.captchaExpired().toJSONString();
        }else{
            cacheCaptcha = PoetryCache.get(email);
            //获取值cacheCaptcha
            log.info("验证码：{}", cacheCaptcha);
        }
        if (!String.valueOf(cacheCaptcha).equals(captcha)) {
            return R.captchaError().toJSONString();
        }
        //在mysql中查看用户是否存在
        User userRes = reactMapper.getUserByEmail(email);
        if(userRes!=null){
            log.info("用户已存在:{}", userRes);
            return R.userExist().toJSONString();
        }
        //获取一个公私钥
        ECKeyPair ecKeyPair = ECKeyPair.create();
        String publicKey = StringCompressor.gzip(String.valueOf(ecKeyPair.getPublicKey()));
//        JSONObject publicKey1 = JSONObject.parseObject(publicKey);
        BigInteger privateKey = ecKeyPair.getPrivateKey();
        System.out.println("---创建账户---");
        System.out.println("公钥：" + publicKey);
        System.out.println("私钥：" + privateKey.toString());
        String formatPublicKey = publicKey;
        String formatPrivateKey = privateKey.toString();
        User user = new User(formatPublicKey, formatPrivateKey);
        boolean result = ProofUtil.register(user);
        Map<String, Object> resData = new HashMap<>();
        resData.put("publicKey", formatPublicKey);
        resData.put("privateKey", formatPrivateKey);
        if(result){
            //存入数据库
            reactMapper.saveUserCredentials(formatPublicKey,email);
            return R.registerOk().data(resData).toJSONString();
        }else{
            do {
                ecKeyPair = ECKeyPair.create();
                publicKey = StringCompressor.gzip(String.valueOf(ecKeyPair.getPublicKey()));
                privateKey = ecKeyPair.getPrivateKey();
                formatPublicKey = publicKey;
                formatPrivateKey = privateKey.toString();
                user = new User(formatPublicKey, formatPrivateKey);
                result = ProofUtil.register(user);
            } while (!result);
            //存入数据库
            reactMapper.saveUserCredentials(formatPublicKey,email);
            return R.registerOk().data(resData).toJSONString();
        }
    }
}