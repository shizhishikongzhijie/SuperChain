package com.example.SuperChain.serviceImpl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.TypeReference;
import com.example.SuperChain.bean.User;
import com.example.SuperChain.bean.Vote;
import com.example.SuperChain.bean.VoteInfo;
import com.example.SuperChain.service.VoteService;
import com.example.SuperChain.util.CountingVote;
import com.example.SuperChain.util.JwtUtil;
import com.example.SuperChain.util.ProofUtil;
import com.example.SuperChain.util.resUtils.R;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author shizhishi
 */
@Service
@Slf4j
public class VoteServiceImpl implements VoteService {

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Override
    public String setVote(Vote vote) throws JsonProcessingException {
        System.out.println("---创建投票:vote: " + vote);
        System.out.println("---创建投票---");
        //查看代码隐私度
        System.out.println("---投票隐私度---");
        int privacy = vote.getPrivacy();//示例
        System.out.println("投票隐私度：" + privacy);
        //首先创建投票：
        String voteId = ProofUtil.createVote(vote.getCreator(), vote.getTitle(), vote.getDescription());
        //创建成功，继续上传投票信息
        //上传问题，需要每次上传一个问题，循环上传
        for (String key : vote.getQuestion().keySet()) {
            //上传问题
            //调用合约方法，up voteId,key,vote.getQuestion().get(key) : return true or false
            Boolean questionBoolean = ProofUtil.addQuestion(voteId, key);//示例
            if (questionBoolean) {
                //上传选项
                //调用合约方法，up voteId,key,vote.getQuestion().get(key) : return true or false
                //循环获得List<String> 的选项
                for (String option : vote.getQuestion().get(key)) {
                    //调用合约方法，up voteId,key,option : return true or false
                    Boolean optionBoolean = ProofUtil.addQuOp(voteId, key, option);//示例
                }
            }
        }
        //上传投票开始时间
        Boolean startDateBoolean = ProofUtil.setStartDate(voteId, String.valueOf(vote.getStartDate()).replace("-", "").replace(" ", "").replace(":", ""));

        //上传投票结束时间
        Boolean limitDateBoolean = ProofUtil.setLimitDate(voteId, String.valueOf(vote.getLimitDate()).replace("-", "").replace(" ", "").replace(":", ""));

        //设置isMultiple
        Boolean isMultipleBoolean = ProofUtil.setIsMultiple(voteId, vote.getIsMultiple());
        //设置uploaderCount
        Boolean uploaderCountBoolean = ProofUtil.setUploaderCount(voteId, String.valueOf(vote.getUploaderCount()));

        //设置隐私度
        Boolean privacyBoolean = ProofUtil.addPrivacy(voteId, String.valueOf(privacy));
        if (privacy == 1) {
            //上传公钥数组
            vote.getPublicKeyList().forEach(pk -> {
                try {
                    Boolean pkBoolean = ProofUtil.addPublicKey(voteId, pk);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        if (startDateBoolean && limitDateBoolean && isMultipleBoolean && uploaderCountBoolean && privacyBoolean) {
            return R.setVoteOk().toJSONString();
        } else {
            return R.setVoteFail().toJSONString();
        }
    }

    @Override
    public String vote(Map<String, Object> data, String vid, String uploader) throws JsonProcessingException {
        //将data转换为Map<String, List<String>>
        Map<String, List<String>> info = new HashMap<>();
        for (String key : data.keySet()) {
            Object value = data.get(key);
            if (value instanceof List) {
                List<String> list = (List<String>) value;
                info.put(key, list);
            } else if (value instanceof String) {
                List<String> list = Collections.singletonList((String) value);
                info.put(key, list);
            } else {
                throw new IllegalArgumentException("Unsupported value type: " + value.getClass().getName());
            }
        }
        VoteInfo voteInfo = new VoteInfo(Integer.parseInt(vid), uploader, info);
        //打印info
        System.out.println("---投票---");
        System.out.println("投票信息: " + voteInfo);
        JSONObject json = new JSONObject();
        //查看选择的投票隐私度，更具隐私度做出不同操作
        //调用合约方法，up data.getVoteId() : return 0 or 1 or 2

        int privacy = 0;//示例
        System.out.println("---投票隐私度---");
        System.out.println("投票隐私度：" + privacy);
        //如果为0
        if (privacy == 0) {
            //打印投票信息
            System.out.println("投票信息: " + voteInfo);
            //调用合约方法,上传投票，up data : return true or false
            Boolean voteBoolean = ProofUtil.addInfo(vid, uploader, JSON.toJSONString(info));
            System.out.println("投票结果：" + voteBoolean);

//            ProofUtil.setUploaderCount(vid, "1000");
            Boolean hasVoteCountBoolean = ProofUtil.addHasVoteCount(vid);
            Boolean addVotedpk = ProofUtil.addVotedpk(vid, uploader);
            //上传投票（返回值）
            if (voteBoolean) {

                json.put("code", 200);
                json.put("title", "投票成功");
            } else {
                json.put("code", "400");
                json.put("title", "投票失败");
            }
        } else if (privacy == 1) {
            //调用合约方法,上传投票，up data : return 可信证明
            String proof = "";//示例
            if (proof != null) {
                json.put("code", 200);
                json.put("title", "投票成功");
                json.put("proof", proof);
            } else {
                json.put("code", "400");
                json.put("title", "投票失败");
            }
        } else {
            //调用合约方法,上传投票，up data : return 可信证明
            String proof = "";//示例
            if (proof != null) {
                json.put("code", 200);
                json.put("title", "投票成功");
                json.put("proof", proof);
                //调用合约方法，销毁用户信息，up data.getVoteId() : return true or false
                Boolean destroyBoolean = true;//示例
                if (destroyBoolean) {
                    json.put("destroy", "销毁成功");
                } else {
                    //销毁失败
                    //调用合约方法，销毁投票信息，up data : return true or false
                    json.replace("code", "400");
                    json.replace("title", "投票失败:用户销毁失败");
                    json.remove("proof");
                }
            } else {
                json.put("code", "400");
                json.put("title", "投票失败");
            }
        }
        return json.toJSONString();
    }

    @Override
    public String searchVote(String info, String token) throws Exception {
        //去除空格以及换行符
        info = info.replaceAll("\\s*", "");
//        ProofUtil.createVote("admin", "test", "test");
//        ProofUtil.retVoteCount();
//        ProofUtil.searchByVoteId("0");
//        ProofUtil.t1();
//        ProofUtil.t2();
//        ProofUtil.addQuestion("1","question2");
//        ProofUtil.addQuOp("1","question1","option1_2");
//        ProofUtil.getQuOp("1", "question1");
        JwtUtil jwtUtil = new JwtUtil();
        User user = jwtUtil.getTokenVal(token, redisTemplate);
        String voteIdStr = ProofUtil.searchVote(info, user.getPublicKey());
        if (voteIdStr.isEmpty()) {
            return R.searchResultNull().toJSONString();
        } else {
            String[] voteIdList = voteIdStr.split(",");
            List<Vote> voteList = new ArrayList<>();
            for (String voteId : voteIdList) {
                System.out.println(voteId);
                Vote res = ProofUtil.searchByVoteId(voteId);
                res.setHasVotedCount(ProofUtil.getHasVoteCount(voteId));
                voteList.add(res);
                //等待 1秒
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
            }
            return R.searchSuccess().data("voteList", voteList).toJSONString();
        }
    }

    @Override
    public String searchVoteById(String vid) throws JsonProcessingException {
        Vote res = ProofUtil.searchByVoteId(vid);
        res.setHasVotedCount(ProofUtil.getHasVoteCount(vid));
        return R.searchSuccess().data("voteView", res).toJSONString();
    }

    @Override
    public String searchVoteByCreator(String creator) throws JsonProcessingException {
        String voteIdStr = ProofUtil.searchByCreator(creator);
        log.info("searchVoteByCreator: " + voteIdStr);
        if (voteIdStr.isEmpty()) {
            return R.searchResultNull().toJSONString();
        } else {
            String[] voteIdList = voteIdStr.split(",");
            List<Vote> voteList = new ArrayList<>();
            for (String voteId : voteIdList) {
                log.info(voteId);
                Vote res = ProofUtil.searchByVoteId(voteId);
                res.setHasVotedCount(ProofUtil.getHasVoteCount(voteId));
                voteList.add(res);
                //等待 1秒
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
            }
            return R.searchSuccess().data("voteList", voteList).toJSONString();
        }
    }

    @Override
    public String searchVoteByPkType(String type, String token) throws Exception {
        JwtUtil jwtUtil = new JwtUtil();
        User user = jwtUtil.getTokenVal(token, redisTemplate);
        String voteIdStr = ProofUtil.searchVoteByPkType(type, user.getPublicKey());
        if (voteIdStr.isEmpty()) {
            return R.searchResultNull().toJSONString();
        } else {
            String[] voteIdList = voteIdStr.split(",");
            List<Vote> voteList = new ArrayList<>();
            for (String voteId : voteIdList) {
                System.out.println(voteId);
                Vote res = ProofUtil.searchByVoteId(voteId);
                res.setHasVotedCount(ProofUtil.getHasVoteCount(voteId));
                voteList.add(res);
//                //等待 1秒
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
            }
            return R.searchSuccess().data("voteList", voteList).toJSONString();
        }
    }

    @Override
    public String searchManagerCount(String token) throws Exception {
        log.info("searchManagerCount:{}", token);
        JwtUtil jwtUtil = new JwtUtil();
        User user = jwtUtil.getTokenVal(token, redisTemplate);
        log.info("user:{}", user);
        String pk = user.getPublicKey();
        List<Integer> managerCountList = new ArrayList<>();
        //管理
        String voteIdStrA = ProofUtil.searchByCreator(pk);
        if (voteIdStrA.isEmpty()) {
            managerCountList.add(0);
        } else {
            String[] voteIdListA = voteIdStrA.split(",");
            managerCountList.add(voteIdListA.length);
        }
        //已
        String voteIdStrB = ProofUtil.searchVoteByPkType("0", pk);
        if (voteIdStrB.isEmpty()) {
            managerCountList.add(0);
        } else {
            String[] voteIdListB = voteIdStrB.split(",");
            managerCountList.add(voteIdListB.length);
        }
        //未
        String voteIdStrC = ProofUtil.searchVoteByPkType("1", pk);
        if (voteIdStrC.isEmpty()) {
            managerCountList.add(0);
        } else {
            String[] voteIdListC = voteIdStrC.split(",");
            managerCountList.add(voteIdListC.length);
        }
        return R.searchSuccess().data("managerCountList", managerCountList).toJSONString();
    }

    @Override
    public String searchVoteViewById(String vid) throws JsonProcessingException {
        Vote res = ProofUtil.searchViewByVoteId(vid);
        res.setHasVotedCount(ProofUtil.getHasVoteCount(vid));
        return R.searchSuccess().data("voteView", res).toJSONString();
    }

    @Override
    public String countingVote(int voteId) throws JsonProcessingException {
        String pkListString = ProofUtil.getVotedPk(String.valueOf(voteId));
        List<String> pkList = new ArrayList<>();
        if (!pkListString.isEmpty()) {
            String[] pkListArray = pkListString.split(",");
            pkList.addAll(Arrays.asList(pkListArray));
        }
        Map<String, List<String>> question = ProofUtil.searchViewByVoteId(String.valueOf(voteId)).getQuestion();
        List<Map<String, List<String>>> resultList = new ArrayList<>();
        for (String pk : pkList) {
            String resultString = ProofUtil.getInfo(String.valueOf(voteId), pk);
            Map<String, List<String>> info = JSON.parseObject(resultString, new TypeReference<Map<String, List<String>>>() {
            });
            resultList.add(info);
        }
        CountingVote countingVote = new CountingVote(question, resultList);
        countingVote.formatVoteInfo();
        Map<String, List<Integer>> calculateVoteResult = countingVote.calculateVoteResult();
        log.info("countingVote:{}", calculateVoteResult);
        Map<String, Object> result = new HashMap();
        result.put("question", question);
        result.put("result", calculateVoteResult);
        return R.ok().data(result).toJSONString();
    }
}

