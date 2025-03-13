package com.example.SuperChain.controller;

import com.baidu.xuper.api.XuperClient;
import com.baidu.xuper.pb.XchainOuterClass;
import com.example.SuperChain.bean.Proof;
import com.example.SuperChain.bean.ProofRequest;
import com.example.SuperChain.config.XchainConfig;
import com.example.SuperChain.serviceImpl.AccountServiceImpl;
import com.example.SuperChain.serviceImpl.BlockServiceImpl;
import com.example.SuperChain.serviceImpl.ProofServiceImpl;
import com.example.SuperChain.util.AESUtils;
import com.example.SuperChain.util.RSAUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bouncycastle.jcajce.provider.asymmetric.RSA;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

/**
 * @author shizhishi
 * //
 */
@RestController
@CrossOrigin(origins = "*")
public class BlockController {
    public static final Logger logger = LoggerFactory.getLogger(BlockController.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final ProofServiceImpl proofService;
    AccountServiceImpl accountService;
    BlockServiceImpl blockService = new BlockServiceImpl();
    //生成RSA加密公私钥
//    @GetMapping("/getRSA")
//    public Map<String, String> getRSA(int num){
//        Map<String, String> map = new HashMap<>();
//        try {
//            map = RSAUtils.rsaEncrypt("hello", num)
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return map;
//    }
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public BlockController(ProofServiceImpl proofService) {
        this.proofService = proofService;
    }

    @GetMapping("/getBlockHeight")
    public Map<String, Object> getBlockHeight() {
        // 获取区块链客户端
        XuperClient client = XchainConfig.client;
        logger.info("获取区块链状态");
        // 获取区块链状态
        XchainOuterClass.BCStatus bcs = client.getBlockchainStatus("xuper");
        // 获取区块信息
        XchainOuterClass.InternalBlock getBlocks = bcs.getBlock();
        // 获取区块高度
        long height = getBlocks.getHeight();
        logger.info("区块高度为：{}", height);
        // 构建返回结果
        Map<String, Object> map = new TreeMap<>();
        map.put("code", 200);
        map.put("blockHeight", height);
        logger.info("区块高度为：{}", height);
        // 返回结果
        return map;
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/getBlockList")
    public List<Map<String, Object>> getBlockList() {
        // 构建一个包含多个 Map 对象的数组
        List<Map<String, Object>> blockList = new ArrayList<>();

        // 构建第一个 Map 对象
        Map<String, Object> block1 = new TreeMap<>();
        block1.put("id", "height1");
        block1.put("title", "height1");

        // 构建第二个 Map 对象
        Map<String, Object> block2 = new TreeMap<>();
        block2.put("id", "height2");
        block2.put("title", "height2");

        // 将 Map 对象添加到数组中
        blockList.add(block1);
        blockList.add(block2);

        // 返回数组
        return blockList;
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/createProof")
    public Proof createProof(@RequestBody ProofRequest request) {
        logger.info("sk:" + request.getSk());
        logger.info("PK:" + request.getPK());

        return proofService.generateProof(request.getSk(), request.getPK());
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/verifyProof")
    public boolean verifyProof(@RequestBody Proof request) {
        logger.info("R:" + request.getR());
        logger.info("z:" + request.getZ());
        logger.info("PK:" + request.getPK());
        logger.info(request.toString());
        return proofService.verifyProof(request.getR(), request.getZ(), request.getPK());
    }

    @GetMapping("/getBalance")
    public String getBalance() {
        return blockService.getBalance();
    }

    //测试
    @GetMapping("/Test")
    public void getBlockHeight(Map<String, List<String>> data) {
        System.out.println(data.get("sex[]"));
    }

    //与前端建立连接
    @GetMapping("/link")
    public String link(String publicKey) throws Exception {
        if (redisTemplate == null) {
            throw new IllegalStateException("redisTemplate is not initialized");
        }

        if (publicKey == null) {
            throw new IllegalArgumentException("publicKey cannot be null");
        }
        //将publicKey（uri)解析
        publicKey = UriUtils.decode(publicKey, "UTF-8");
        String secretKeyString = AESUtils.getSecretKeyString(AESUtils.generateAESKey());
        logger.info("publicKey: {}", publicKey);
        logger.info("SecretKeyString: {}", secretKeyString);//对称加密的密钥

        // 放入redis //设置过期时间为60秒
        redisTemplate.opsForValue().set(RSAUtils.cleanKeyStr(publicKey), secretKeyString, 6000, TimeUnit.SECONDS);

        // 获取值
        String value = (String) redisTemplate.opsForValue().get(RSAUtils.cleanKeyStr(publicKey));
        logger.info("value: {}", value);

        return RSAUtils.rsaEncrypt(secretKeyString, publicKey);//返回加密后的对称密钥
    }
}