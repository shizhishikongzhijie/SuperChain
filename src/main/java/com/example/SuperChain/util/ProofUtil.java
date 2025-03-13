package com.example.SuperChain.util;

import com.baidu.xuper.api.Transaction;
import com.example.SuperChain.bean.User;
import com.example.SuperChain.bean.Vote;
import com.example.SuperChain.config.XchainConfig;
import com.example.SuperChain.controller.BlockController;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigInteger;
import java.sql.Date;
import java.util.*;

/**
 * 调用智能合约
 *
 * @author shizhishi
 */
public class ProofUtil {
    public static final Logger logger = LoggerFactory.getLogger(BlockController.class);

    @Value("${xuperchain.client.url}")
    private String url;
    @Value("${xuperchain.keys}")
    private String key;
    @Value("${xuperchain.ConfPath}")
    private String confpath;
    @Value("${xuperchain.contractAccount}")
    private String contractAccount;

    @Value("${xuperchain.contractChainName}")
    private String contractChainName;

    public static int loginCheck(User data) throws JsonProcessingException {
        logger.info("开始调用智能合约 || 登录");
        String contractMethod = "login_check";
        String contractName = "reg_log_a";
        Map<String, String> params = new TreeMap<>();
        params.put("_publicKey", data.getPublicKey());
        params.put("_privateKey", data.getPrivateKey());
        try {
            Transaction tx = XchainConfig.client.queryEVMContract(XchainConfig.account, contractName, contractMethod, params);
            System.out.println("获取:" + tx.getContractResponse().getBodyStr());
            ObjectMapper objectMapper = new ObjectMapper();
            // 将 JSON 数组解析为 Java 对象列表
            JsonNode jsonNodes = objectMapper.readTree(tx.getContractResponse().getBodyStr());

            // 获取第一个对象
            JsonNode firstNode = jsonNodes.get(0);

            // 提取 "0" 键对应的值
            String value = firstNode.get("0").asText();

            // 将字符串转换为整数
            int number = Integer.parseInt(value);

            System.out.println("Extracted number: " + number); // 输出：Extracted number: 19
            return number;
        } catch (Exception e) {
            throw new RuntimeException("Simulated error");
        }


    }

    public static Boolean register(User data) throws JsonProcessingException {
        logger.info("开始调用智能合约 || 注册");
        String contractMethod = "register";
        String contractName = "reg_log_a";
        Map<String, String> params = new TreeMap<>();
        logger.info("data:{}", data);
        params.put("_publicKey", data.getPublicKey());
        params.put("_privateKey", data.getPrivateKey());
        Transaction tx = XchainConfig.client.invokeEVMContract(XchainConfig.account, contractName, contractMethod, params, BigInteger.ZERO);
        System.out.println("获取:" + tx.getContractResponse().getBodyStr());
        ObjectMapper objectMapper = new ObjectMapper();
        // 将 JSON 数组解析为 Java 对象列表
        JsonNode jsonNodes = objectMapper.readTree(tx.getContractResponse().getBodyStr());

        // 获取第一个对象
        JsonNode firstNode = jsonNodes.get(0);

        // 提取 "0" 键对应的值
        String value = firstNode.get("0").asText();

        // 将字符串转换为整数
        Boolean number = Boolean.valueOf(value);

        System.out.println("Extracted number: " + number); // 输出：Extracted number: 19
        return number;
    }

    public static String searchVote(String s, String pk) throws JsonProcessingException {
        logger.info("开始调用智能合约 || 搜索投票");
        String contractMethod = "search_vote";//方法名
        String contractName = "vote_12_9";//合约名
        Map<String, String> params = new TreeMap<>();
        params.put("pattern", "title");// 搜索模式标题/描述/创建人
        params.put("keyword", s);
        params.put("pk", pk);
        Transaction tx = XchainConfig.client.queryEVMContract(XchainConfig.account, contractName, contractMethod, params);
        logger.info("获取:" + tx.getContractResponse().getBodyStr());
        ObjectMapper objectMapper = new ObjectMapper();
        // 将 JSON 数组解析为 Java 对象列表
        JsonNode jsonNodes = objectMapper.readTree(tx.getContractResponse().getBodyStr());
        // 获取第一个对象
        JsonNode firstNode = jsonNodes.get(0);
        // 提取 "0" 键对应的值
        String voteId = firstNode.get("0").asText();
        return voteId;
    }

    public static String createVote(String creator, String title, String description) throws JsonProcessingException {
        logger.info("开始调用智能合约 || 创建投票");
        String contractMethod = "create_vote";//方法名
        String contractName = "vote_12_9";//合约名
        Map<String, String> params = new TreeMap<>();
        params.put("_creator", creator);// 搜索模式标题/描述/创建人
        params.put("_title", title);
        params.put("_description", description);
        Transaction tx = XchainConfig.client.invokeEVMContract(XchainConfig.account, contractName, contractMethod, params, BigInteger.ZERO);

        System.out.println("获取:" + tx.getContractResponse().getBodyStr());
        ObjectMapper objectMapper = new ObjectMapper();
        // 将 JSON 数组解析为 Java 对象列表
        JsonNode jsonNodes = objectMapper.readTree(tx.getContractResponse().getBodyStr());
        logger.info("搜索结果a：" + jsonNodes);
        // 获取第一个对象
        JsonNode firstNode = jsonNodes.get(0);
        logger.info("搜索结果b：" + firstNode);
        return firstNode.get("0").asText();
    }

    ;


    public static Integer getHasVoteCount(String voteId) throws JsonProcessingException {
        logger.info("开始调用智能合约 || 获取已投票数");
        String contractMethod = "get_hasVoteCount";//方法名
        String contractName = "vote_12_9";//合约名
        Map<String, String> params = new TreeMap<>();
        params.put("__voteId", voteId);
        Transaction tx = XchainConfig.client.queryEVMContract(XchainConfig.account, contractName, contractMethod, params);
        System.out.println("获取:" + tx.getContractResponse().getBodyStr());
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNodes = objectMapper.readTree(tx.getContractResponse().getBodyStr());
        logger.info("搜索结果：" + jsonNodes);
        return jsonNodes.get(0).get("0").asInt();
    }

    public static Vote searchByVoteId(String voteId) throws JsonProcessingException {
        logger.info("开始调用智能合约 || 通过投票id搜索投票");
        String contractMethod = "search_by_voteId";//方法名
        String contractName = "vote_12_9";//合约名
        Map<String, String> params = new TreeMap<>();
        params.put("__voteId", voteId);
        Transaction tx = XchainConfig.client.queryEVMContract(XchainConfig.account, contractName, contractMethod, params);
        System.out.println("获取:" + tx.getContractResponse().getBodyStr());
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNodes = objectMapper.readTree(tx.getContractResponse().getBodyStr());
        logger.info("搜索结果：" + jsonNodes);
        Vote vote = new Vote();
        // 遍历数组中的每个对象
        for (JsonNode node : jsonNodes) {
            logger.info("遍历：" + node);
            if (node.has("voteId")) {
                vote.setVoteId(node.get("voteId") == null ? -1 : node.get("voteId").asInt());
            }
            if (node.has("creator")) {
                vote.setCreator(node.get("creator") == null ? null : node.get("creator").asText());
            }
            if (node.has("uploaderCount")) {
                vote.setUploaderCount(node.get("uploaderCount") == null ? 0 : node.get("uploaderCount").asInt());
            }
            if (node.has("title")) {
                vote.setTitle(node.get("title") == null ? null : node.get("title").asText());
            }
            if (node.has("privacy")) {
                vote.setPrivacy(node.get("privacy") == null ? 0 : node.get("privacy").asInt());
            }
            if (node.has("description")) {
                vote.setDescription(node.get("description") == null ? null : node.get("description").asText());
            }
            if (node.has("startDate")) {
                vote.setStartDate(Date.valueOf(node.get("startDate") == null ? "2023-04-01 03:00:49" : BigTimestampConverter.bigIntegerToDateString(node.get("startDate").asText())));
            }
            if (node.has("updateDate")) {
                vote.setUpdateDate(Date.valueOf(node.get("updateDate") == null ? "2023-04-01 03:00:49" : BigTimestampConverter.bigIntegerToDateString(node.get("updateDate").asText())));
            }
            if (node.has("limitDate")) {
                vote.setLimitDate(Date.valueOf(node.get("limitDate") == null ? "2023-04-01 03:00:49" : BigTimestampConverter.bigIntegerToDateString(node.get("limitDate").asText())));
            }
//            if (node.has("question")) {
//                String questionStr = node.get("question").asText();
//                String[] questionArr = questionStr.split(",");
//                Map<String, List<String>> questionMap = new LinkedHashMap<>();
//                //循环遍历数组
//                for (String question : questionArr) {
//                    logger.info("问题：" + question);
//                    List<String> options = getQuOp(voteId, question);
//                    questionMap.put(question, options);
//                }
//                vote.setQuestion(questionMap);
//            }
        }
        logger.info("搜索结果：" + vote);
        return vote;
    }

    public static void retVoteCount() {
        logger.info("开始调用智能合约 || 获取投票总数");
        String contractMethod = "ret_vote_count";//方法名
        String contractName = "vote_12_9";//合约名
        Map<String, String> params = new TreeMap<>();
        Transaction tx = XchainConfig.client.queryEVMContract(XchainConfig.account, contractName, contractMethod, params);
        System.out.println("获取:" + tx.getContractResponse().getBodyStr());
    }


    public static Boolean addQuestion(String voteId, String question) throws JsonProcessingException {
        logger.info("开始调用智能合约 || 添加问题");
        String contractMethod = "add_question";//方法名
        String contractName = "vote_12_9";//合约名
        Map<String, String> params = new TreeMap<>();
        params.put("__voteId", voteId);
        params.put("_question", question);
        Transaction tx = XchainConfig.client.invokeEVMContract(XchainConfig.account, contractName, contractMethod, params, BigInteger.ZERO);
        System.out.println("获取:" + tx.getContractResponse().getBodyStr());
        ObjectMapper objectMapper = new ObjectMapper();
        // 将 JSON 数组解析为 Java 对象列表
        JsonNode jsonNodes = objectMapper.readTree(tx.getContractResponse().getBodyStr());
        logger.info("搜索结果a：" + jsonNodes);
        // 获取第一个对象
        JsonNode firstNode = jsonNodes.get(0);
        logger.info("搜索结果b：" + firstNode);
        return Boolean.valueOf(firstNode.get("0").asText());
    }

    public static Boolean addQuOp(String voteId, String question, String option) throws JsonProcessingException {
        logger.info("开始调用智能合约 || 添加选项");
        String contractMethod = "add_qu_op";//方法名
        String contractName = "vote_12_9";//合约名
        Map<String, String> params = new TreeMap<>();
        params.put("__voteId", voteId);
        params.put("_question", question);
        params.put("option", option);
        Transaction tx = XchainConfig.client.invokeEVMContract(XchainConfig.account, contractName, contractMethod, params, BigInteger.ZERO);
        System.out.println("获取:" + tx.getContractResponse().getBodyStr());
        ObjectMapper objectMapper = new ObjectMapper();
        // 将 JSON 数组解析为 Java 对象列表
        JsonNode jsonNodes = objectMapper.readTree(tx.getContractResponse().getBodyStr());
        logger.info("搜索结果a：" + jsonNodes);
        return Boolean.valueOf(jsonNodes.get(0).get("0").asText());
    }

    public static List<String> getQuOp(String voteId, String question) throws JsonProcessingException {
        logger.info("开始调用智能合约 || 获取选项");
        String contractMethod = "get_qu_op";//方法名
        String contractName = "vote_12_9";//合约名
        Map<String, String> params = new TreeMap<>();
        params.put("__voteId", voteId);
        params.put("_question", question);
        Transaction tx = XchainConfig.client.queryEVMContract(XchainConfig.account, contractName, contractMethod, params);
        System.out.println("获取:" + tx.getContractResponse().getBodyStr());
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNodes = objectMapper.readTree(tx.getContractResponse().getBodyStr());
        List<String> list = new ArrayList<>();
        String optionStr = jsonNodes.get(0).get("0").asText();
        String[] optionArr = optionStr.split(",");
        for (String option : optionArr) {
            logger.info("选项：" + option);
            list.add(option);
        }
        return list;
    }

    public static Boolean setStartDate(String voteId, String startDate) throws JsonProcessingException {
        logger.info("开始调用智能合约 || 设置投票开始时间");
        String contractMethod = "set_startDate";//方法名
        String contractName = "vote_12_9";//合约名
        Map<String, String> params = new TreeMap<>();
        params.put("__voteId", voteId);
        params.put("_newstartDate", startDate);
        Transaction tx = XchainConfig.client.invokeEVMContract(XchainConfig.account, contractName, contractMethod, params, BigInteger.ZERO);
        System.out.println("获取:" + tx.getContractResponse().getBodyStr());
        ObjectMapper objectMapper = new ObjectMapper();
        // 将 JSON 数组解析为 Java 对象列表
        JsonNode jsonNodes = objectMapper.readTree(tx.getContractResponse().getBodyStr());
        logger.info("搜索结果a：" + jsonNodes);
        return Boolean.valueOf(jsonNodes.get(0).get("0").asText());
    }

    public static Boolean setLimitDate(String voteId, String s) throws JsonProcessingException {
        logger.info("开始调用智能合约 || 设置投票结束时间");
        String contractMethod = "set_limitDate";//方法名
        String contractName = "vote_12_9";//合约名
        Map<String, String> params = new TreeMap<>();
        params.put("__voteId", voteId);
        params.put("_newlimitDate", s);
        Transaction tx = XchainConfig.client.invokeEVMContract(XchainConfig.account, contractName, contractMethod, params, BigInteger.ZERO);
        System.out.println("获取:" + tx.getContractResponse().getBodyStr());
        ObjectMapper objectMapper = new ObjectMapper();
        // 将 JSON 数组解析为 Java 对象列表
        JsonNode jsonNodes = objectMapper.readTree(tx.getContractResponse().getBodyStr());
        logger.info("搜索结果a：" + jsonNodes);
        return Boolean.valueOf(jsonNodes.get(0).get("0").asText());
    }

    public static Boolean addPrivacy(String voteId, String privacy) throws JsonProcessingException {
        logger.info("开始调用智能合约 || 设置隐私");
        String contractMethod = "set_privacy";//方法名
        String contractName = "vote_12_9";//合约名
        Map<String, String> params = new TreeMap<>();
        params.put("__voteId", voteId);
        params.put("_value", privacy);
        Transaction tx = XchainConfig.client.invokeEVMContract(XchainConfig.account, contractName, contractMethod, params, BigInteger.ZERO);
        System.out.println("获取:" + tx.getContractResponse().getBodyStr());
        ObjectMapper objectMapper = new ObjectMapper();
        // 将 JSON 数组解析为 Java 对象列表
        JsonNode jsonNodes = objectMapper.readTree(tx.getContractResponse().getBodyStr());
        logger.info("搜索结果a：" + jsonNodes);
        return Boolean.valueOf(jsonNodes.get(0).get("0").asText());
    }

    public static Boolean setIsMultiple(String voteId, String isMultiple) throws JsonProcessingException {
        logger.info("开始调用智能合约 || 设置是否多选");
        String contractMethod = "set_isMultiple";//方法名
        String contractName = "vote_12_9";//合约名

        Map<String, String> params = new TreeMap<>();
        params.put("__voteId", voteId);
        params.put("_value", isMultiple);
        Transaction tx = XchainConfig.client.invokeEVMContract(XchainConfig.account, contractName, contractMethod, params, BigInteger.ZERO);
        System.out.println("获取:" + tx.getContractResponse().getBodyStr());
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNodes = objectMapper.readTree(tx.getContractResponse().getBodyStr());
        logger.info("搜索结果a：" + jsonNodes);
        return Boolean.valueOf(jsonNodes.get(0).get("0").asText());
    }

    public static Boolean addInfo(String vid, String publisher, String voteInfo) throws JsonProcessingException {
        logger.info("开始调用智能合约 || 添加投票信息");
        String contractMethod = "add_info";//方法名
        String contractName = "vote_12_9";//合约名
        Map<String, String> params = new TreeMap<>();
        params.put("__voteId", vid);
        params.put("identifier", publisher);
        params.put("content", voteInfo);
        Transaction tx = XchainConfig.client.invokeEVMContract(XchainConfig.account, contractName, contractMethod, params, BigInteger.ZERO);
        System.out.println("获取:" + tx.getContractResponse().getBodyStr());
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNodes = objectMapper.readTree(tx.getContractResponse().getBodyStr());
        return Boolean.valueOf(jsonNodes.get(0).get("0").asText());
    }

    public static Boolean addPublicKey(String voteId, String pk) throws JsonProcessingException {
        logger.info("开始调用智能合约 || 添加投票者公钥");
        String contractMethod = "add_publicKey";//方法名
        String contractName = "vote_12_9";//合约名
        Map<String, String> params = new TreeMap<>();
        params.put("__voteId", voteId);
        params.put("publickey", pk);
        Transaction tx = XchainConfig.client.invokeEVMContract(XchainConfig.account, contractName, contractMethod, params, BigInteger.ZERO);
        System.out.println("获取:" + tx.getContractResponse().getBodyStr());
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNodes = objectMapper.readTree(tx.getContractResponse().getBodyStr());
        return Boolean.valueOf(jsonNodes.get(0).get("0").asText());
    }

    public static Boolean addVotedpk(String voteId, String pk) throws JsonProcessingException {
        logger.info("开始调用智能合约 || 添加已投票者公钥");
        String contractMethod = "add_votedpk";//方法名
        String contractName = "vote_12_9";//合约名
        Map<String, String> params = new TreeMap<>();
        params.put("__voteId", voteId);
        params.put("pk", pk);
        Transaction tx = XchainConfig.client.invokeEVMContract(XchainConfig.account, contractName, contractMethod, params, BigInteger.ZERO);
        System.out.println("获取:" + tx.getContractResponse().getBodyStr());
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNodes = objectMapper.readTree(tx.getContractResponse().getBodyStr());
        return Boolean.valueOf(jsonNodes.get(0).get("0").asText());
    }

    public static Boolean addHasVoteCount(String vid) throws JsonProcessingException {
        logger.info("开始调用智能合约 || 添加已投票数");
        String contractMethod = "add_hasVoteCount";//方法名
        String contractName = "vote_12_9";//合约名
        Map<String, String> params = new TreeMap<>();
        params.put("__voteId", vid);
        Transaction tx = XchainConfig.client.invokeEVMContract(XchainConfig.account, contractName, contractMethod, params, BigInteger.ZERO);
        System.out.println("获取:" + tx.getContractResponse().getBodyStr());
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNodes = objectMapper.readTree(tx.getContractResponse().getBodyStr());
        return Boolean.valueOf(jsonNodes.get(0).get("0").asText());
    }

    public static Boolean setUploaderCount(String vid, String num) throws JsonProcessingException {
        logger.info("开始调用智能合约 || 设置上传者数量");
        String contractMethod = "set_uploaderCount";//方法名
        String contractName = "vote_12_9";//合约名
        Map<String, String> params = new TreeMap<>();
        params.put("__voteId", vid);
        params.put("num", num);
        Transaction tx = XchainConfig.client.invokeEVMContract(XchainConfig.account, contractName, contractMethod, params, BigInteger.ZERO);
        System.out.println("获取:" + tx.getContractResponse().getBodyStr());
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNodes = objectMapper.readTree(tx.getContractResponse().getBodyStr());
        return Boolean.valueOf(jsonNodes.get(0).get("0").asText());

    }

    public static String searchByCreator(String creator) throws JsonProcessingException {
        logger.info("开始调用智能合约 || 根据创建者搜索");
        String contractMethod = "search_by_creator";//方法名
        String contractName = "vote_12_9";//合约名
        Map<String, String> params = new TreeMap<>();
        params.put("keyword", creator);
        params.put("pk", creator);
        Transaction tx = XchainConfig.client.queryEVMContract(XchainConfig.account, contractName, contractMethod, params);
        System.out.println("获取:" + tx.getContractResponse().getBodyStr());
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNodes = objectMapper.readTree(tx.getContractResponse().getBodyStr());
        return jsonNodes.get(0).get("0").asText();
    }

    public static String searchVoteByPkType(String pk, String type) throws JsonProcessingException {
        logger.info("开始调用智能合约 || 根据公钥和类型搜索");
        String contractMethod = "search_vote_by_pk_type";//方法名
        String contractName = "vote_12_9";//合约名
        Map<String, String> params = new TreeMap<>();
        params.put("publickey", pk);
        params.put("Type", type);//Type:  0:已投 1:未投 2:所有
        Transaction tx = XchainConfig.client.queryEVMContract(XchainConfig.account, contractName, contractMethod, params);
        logger.info("获取:" + tx.getContractResponse().getBodyStr());
        ObjectMapper objectMapper = new ObjectMapper();
        // 将 JSON 数组解析为 Java 对象列表
        JsonNode jsonNodes = objectMapper.readTree(tx.getContractResponse().getBodyStr());
        // 获取第一个对象
        JsonNode firstNode = jsonNodes.get(0);
        // 提取 "0" 键对应的值
        String voteId = firstNode.get("0").asText();
        return voteId;
    }

    public static String getVotedPk(String voteId) throws JsonProcessingException {
        logger.info("开始调用智能合约 || 已投票人序列");
        String contractMethod = "get_votedpk";//方法名
        String contractName = "vote_12_9";//合约名
        Map<String, String> params = new TreeMap<>();
        params.put("__voteId", voteId);
        Transaction tx = XchainConfig.client.queryEVMContract(XchainConfig.account, contractName, contractMethod, params);
        logger.info("获取:" + tx.getContractResponse().getBodyStr());
        ObjectMapper objectMapper = new ObjectMapper();
        // 将 JSON 数组解析为 Java 对象列表
        JsonNode jsonNodes = objectMapper.readTree(tx.getContractResponse().getBodyStr());
        // 获取第一个对象
        JsonNode firstNode = jsonNodes.get(0);
        String pkString = firstNode.get("0").asText();
        logger.info("获取pkString:" + pkString);
        return pkString;
    }

    public static String getInfo(String voteId,String pk) throws JsonProcessingException {
        logger.info("开始调用智能合约 || 用户此投票的信息");
        String contractMethod = "get_info";//方法名
        String contractName = "vote_12_9";//合约名
        Map<String, String> params = new TreeMap<>();
        params.put("__voteId", voteId);
        params.put("identifier", pk);
        Transaction tx = XchainConfig.client.queryEVMContract(XchainConfig.account, contractName, contractMethod, params);
        logger.info("用户此投票的信息获取:" + tx.getContractResponse().getBodyStr());
        ObjectMapper objectMapper = new ObjectMapper();
        // 将 JSON 数组解析为 Java 对象列表
        JsonNode jsonNodes = objectMapper.readTree(tx.getContractResponse().getBodyStr());
        // 获取第一个对象
        JsonNode firstNode = jsonNodes.get(0);
        System.out.println("用户此投票的信息获取0:" + firstNode.get("0").asText());
        return firstNode.get("0").asText();
    }

    /**
     * 测试
     */
    public static void t1() {
        logger.info("开始调用智能合约 || 测试一");
        String contractMethod = "t1";//方法名
        String contractName = "test_str_list";//合约名
        Map<String, String> params = new TreeMap<>();
        Transaction tx = XchainConfig.client.queryEVMContract(XchainConfig.account, contractName, contractMethod, params);
        System.out.println("获取:" + tx.getContractResponse().getBodyStr());
    }

    public static void t2() {
        logger.info("开始调用智能合约 || 测试二");
        String contractMethod = "t2";//方法名
        String contractName = "test_str_list";//合约名
        Map<String, String> params = new TreeMap<>();
        Transaction tx = XchainConfig.client.queryEVMContract(XchainConfig.account, contractName, contractMethod, params);
        System.out.println("获取:" + tx.getContractResponse().getBodyStr());
    }

    public static Vote searchViewByVoteId(String voteId) throws JsonProcessingException {
        logger.info("开始调用智能合约 || 通过投票id搜索投票（搜索选项）");
        String contractMethod = "search_by_voteId";//方法名
        String contractName = "vote_12_9";//合约名
        Map<String, String> params = new TreeMap<>();
        params.put("__voteId", voteId);
        Transaction tx = XchainConfig.client.queryEVMContract(XchainConfig.account, contractName, contractMethod, params);
        System.out.println("获取:" + tx.getContractResponse().getBodyStr());
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNodes = objectMapper.readTree(tx.getContractResponse().getBodyStr());
        logger.info("搜索结果：" + jsonNodes);
        Vote vote = new Vote();
        // 遍历数组中的每个对象
        for (JsonNode node : jsonNodes) {
            logger.info("遍历：" + node);
            if (node.has("voteId")) {
                vote.setVoteId(node.get("voteId") == null ? -1 : node.get("voteId").asInt());
            }
            if (node.has("creator")) {
                vote.setCreator(node.get("creator") == null ? null : node.get("creator").asText());
            }
            if (node.has("uploaderCount")) {
                vote.setUploaderCount(node.get("uploaderCount") == null ? 0 : node.get("uploaderCount").asInt());
            }
            if (node.has("title")) {
                vote.setTitle(node.get("title") == null ? null : node.get("title").asText());
            }
            if (node.has("privacy")) {
                vote.setPrivacy(node.get("privacy") == null ? 0 : node.get("privacy").asInt());
            }
            if (node.has("description")) {
                vote.setDescription(node.get("description") == null ? null : node.get("description").asText());
            }
            if (node.has("startDate")) {
                vote.setStartDate(Date.valueOf(node.get("startDate") == null ? "2023-04-01 03:00:49" : BigTimestampConverter.bigIntegerToDateString(node.get("startDate").asText())));
            }
            if (node.has("updateDate")) {
                vote.setUpdateDate(Date.valueOf(node.get("updateDate") == null ? "2023-04-01 03:00:49" : BigTimestampConverter.bigIntegerToDateString(node.get("updateDate").asText())));
            }
            if (node.has("limitDate")) {
                vote.setLimitDate(Date.valueOf(node.get("limitDate") == null ? "2023-04-01 03:00:49" : BigTimestampConverter.bigIntegerToDateString(node.get("limitDate").asText())));
            }
            if (node.has("question")) {
                String questionStr = node.get("question").asText();
                String[] questionArr = questionStr.split(",");
                Map<String, List<String>> questionMap = new LinkedHashMap<>();
                //循环遍历数组
                for (String question : questionArr) {
                    logger.info("问题：" + question);
                    List<String> options = getQuOp(voteId, question);
                    questionMap.put(question, options);
                }
                vote.setQuestion(questionMap);
            }
        }
        logger.info("搜索结果：" + vote);
        return vote;
    }


    public Boolean verifyAccount(User user) {
        String contractName = "jsqsy";
        String contractMethod = "get";
        Map<String, String> params = new TreeMap<>();
        params.put("key", "xchain");
        // 开放网络不允许转账，所以在调用合约时 amount 参数要给0
//        Transaction tx = client.invokeEVMContract(account, contractName, contractMethod, params, BigInteger.ZERO);
        Transaction tx1 = XchainConfig.client.queryEVMContract(XchainConfig.account, contractName, contractMethod, params);
//        System.out.println("获取:"+tx.getContractResponse().getBodyStr());
        System.out.println("获取:" + tx1.getContractResponse().getBodyStr());
        return Boolean.valueOf(tx1.getContractResponse().getBodyStr());
    }
}
