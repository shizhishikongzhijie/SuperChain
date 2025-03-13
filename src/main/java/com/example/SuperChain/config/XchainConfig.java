package com.example.SuperChain.config;

import com.alibaba.fastjson2.JSONObject;
import com.baidu.xuper.api.Account;
import com.baidu.xuper.api.Transaction;
import com.baidu.xuper.api.XuperClient;
import com.baidu.xuper.config.Config;
import com.baidu.xuper.crypto.ECKeyPair;
import com.example.SuperChain.util.StringCompressor;
import lombok.*;
import org.bouncycastle.math.ec.ECPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;


import javax.annotation.PostConstruct;
import java.math.BigInteger;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author shizhishi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Configuration
public class XchainConfig {

    public static final Logger logger = LoggerFactory.getLogger(XchainConfig.class);
    public static XuperClient client;
    public static Account account;
    @Value("${xuperchain.client.url}")
    private String url;
    @Value("${xuperchain.keys}")
    private String key;
    @Value("${xuperchain.ConfPath}")
    private String confpath;
    @Value("${xuperchain.contractAccount}")
    private String contractAccount;
    @Value("${xuperchain.contractName}")
    private String contractName;
    @Value("${xuperchain.contractChainName}")
    private String contractChainName;
    //PostConstruct注解，在bean初始化后执行

    // 定时任务，每隔一段时间重新初始化,因为grpc服务器有时间限制或一定间隔内次数限制
    //2024-09-22 19:22:47.565 ERROR 31212 --- [nio-8080-exec-1] o.a.c.c.C.[.[.[/].[dispatcherServlet]    :
    // Servlet.service() for servlet [dispatcherServlet] in context with path [] threw exception
    // [Request processing failed; nested exception is io.grpc.StatusRuntimeException:
    // UNAVAILABLE: End of stream or IOException] with root cause
    //io.grpc.StatusRuntimeException: UNAVAILABLE: End of stream or IOException


//    @Scheduled(fixedRate = 1200000) // 每隔20分钟执行一次
//    public void scheduledInitialization() {
//        initialize();
//    }

    // 初始化字段
    @PostConstruct
    public void initField() {
        initialize();
    }

    public void initialize() {
        String keyPath = System.getProperty("user.dir") + key;         //  由于Account.create（）方法需要绝对路径，所以需要用此代码获取完整的路径
//        account = Account.getAccountFromFile(keyPath, "761007");
        account = Account.create(keyPath);
//        Config.setConfigPath("D:\\SuperChain\\src\\main\\resources\\conf\\sdk.yaml");
        //// 在调用合约时，如果 SetContractAccount，那么此次调用的发起者为合约账户。即：msg.sender 为合约账户转换后的EVM地址。
        account.setContractAccount(contractAccount);
        client = new XuperClient(url);  // 创建客户端
        client.setChainName(contractChainName);
        logger.info("hasConfigFile:" + Config.hasConfigFile());
        String contractMethod = "get";
        Map<String, String> params = new TreeMap<>();
        params.put("key", "xchain");
        System.out.println(account);
        System.out.println("path: " + keyPath);
        System.out.println("地址：" + account.getAddress());
        System.out.println("合约账户：" + account.getContractAccount());
        System.out.println("余额：" + client.getBalance(contractAccount));
        System.out.println("助记词：" + account.getMnemonic());
//        System.out.println(contractName);// 合约名称
        System.out.println(contractMethod);
        System.out.println(params);
        System.out.println(BigInteger.ZERO);
        ECKeyPair ecKeyPair = ECKeyPair.create();
        String publicKey = String.valueOf(ecKeyPair.getPublicKey());
        BigInteger privateKey = ecKeyPair.getPrivateKey();
        System.out.println("---创建账户---");
        System.out.println("地址：" + publicKey);
        System.out.println("公钥：" + StringCompressor.gzip(publicKey));
        System.out.println("私钥：" + privateKey.toString());
        System.out.println();
        // 开放网络不允许转账，所以在调用合约时 amount 参数要给0
//        Transaction tx = client.invokeEVMContract(account, contractName, contractMethod, params, BigInteger.ZERO);
//        Transaction tx1 = client.queryEVMContract(account, contractName, contractMethod, params);
//        System.out.println("获取:"+tx.getContractResponse().getBodyStr());
//        System.out.println("获取:" + tx1.getContractResponse().getBodyStr());
    }
}
