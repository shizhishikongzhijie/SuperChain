package com.example.SuperChain.controller;

import com.alibaba.fastjson2.JSONObject;
import com.example.SuperChain.bean.RequestData;
import com.example.SuperChain.bean.Vote;
import com.example.SuperChain.serviceImpl.AccountServiceImpl;
import com.example.SuperChain.serviceImpl.VoteServiceImpl;
import com.example.SuperChain.util.AESUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author shizhishi
 */
@RestController
public class VoteController {
    private static final Logger logger = LoggerFactory.getLogger(VoteController.class);

    @Autowired
    AccountServiceImpl accountService;

    @Autowired
    VoteServiceImpl voteService;

    @GetMapping("/searchVote")
    public String searchVote(String info, HttpServletRequest request) throws Exception {
        String linkKey = (String) request.getAttribute("linkKey");
        logger.info("info: {}", info);
        String s = AESUtils.aesDecrypt(info, linkKey);
        logger.info("info: {}", s);
        String token = request.getHeader("authorization");
        return AESUtils.aesEncrypt(voteService.searchVote(s, token), linkKey);
    }

    @RequestMapping("/searchVoteByCreator")
    public String searchVoteByCreator(String creator, HttpServletRequest request) throws Exception {
        String linkKey = (String) request.getAttribute("linkKey");
        creator = AESUtils.aesDecrypt(creator, linkKey);
        return AESUtils.aesEncrypt(voteService.searchVoteByCreator(creator), linkKey);
    }

    @RequestMapping("/searchVoteByPkType")
    public String searchVoteByPkType(String type, HttpServletRequest request) throws Exception {
        String linkKey = (String) request.getAttribute("linkKey");
        logger.info("type: {}", type);
        type = AESUtils.aesDecrypt(type, linkKey);
        logger.info("type: {}", type);
        String token = request.getHeader("authorization");
        return AESUtils.aesEncrypt(voteService.searchVoteByPkType(type, token), linkKey);
    }

    @RequestMapping("/searchManagerCount")
    public String searchManagerCount(HttpServletRequest request) throws Exception {
        String token = request.getHeader("authorization");
        String linkKey = (String) request.getAttribute("linkKey");
        return AESUtils.aesEncrypt(voteService.searchManagerCount(token), linkKey);
    }

    //创建投票
    @GetMapping("/setVote")
    public String setVote(String vote, HttpServletRequest request) throws Exception {
        String linkKey = (String) request.getAttribute("linkKey");
        vote = AESUtils.aesDecrypt(vote, linkKey);
        logger.info("vote: {}", vote);
        Vote voteObj = JSONObject.parseObject(vote, Vote.class);
        logger.info("voteObj: {}", voteObj);
        return AESUtils.aesEncrypt(voteService.setVote(voteObj), linkKey);
    }

    //进行投票
    @RequestMapping("/vote")
    public String vote(String data, String voteId, String uploader, HttpServletRequest request) throws Exception {
        String linkKey = (String) request.getAttribute("linkKey");
        Map<String, Object> dataObj = JSONObject.parseObject(AESUtils.aesDecrypt(data, linkKey), Map.class);
        return voteService.vote(dataObj, voteId, uploader);
    }

    @GetMapping("/voteView")
    public String voteView(String requestData, HttpServletRequest request) throws Exception {
        String linkKey = (String) request.getAttribute("linkKey");
        RequestData requestDataObj = JSONObject.parseObject(AESUtils.aesDecrypt(requestData, linkKey), RequestData.class);
        logger.info("requestDataObj: {}", requestDataObj);
        return AESUtils.aesEncrypt(voteService.searchVoteViewById(requestDataObj.getVid()), linkKey);
    }

    //计票
    @GetMapping("/countVote")
    public String countingVote(int voteId, HttpServletRequest request) throws Exception {
        String linkKey = (String) request.getAttribute("linkKey");
//        voteId = AESUtils.aesDecrypt(voteId, linkKey);

        return AESUtils.aesEncrypt(voteService.countingVote(voteId), linkKey);

    }
}