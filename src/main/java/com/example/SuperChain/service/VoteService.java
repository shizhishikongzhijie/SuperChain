package com.example.SuperChain.service;

import com.example.SuperChain.bean.Vote;
import com.example.SuperChain.bean.VoteInfo;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.Map;

public interface VoteService {

    String setVote(Vote vote) throws JsonProcessingException;

    String vote( Map<String, Object> data, String vid,String uploader) throws JsonProcessingException;

    String searchVote(String info,String token) throws Exception;

    String searchVoteById(String vid) throws JsonProcessingException;

    String searchVoteByCreator(String creater) throws JsonProcessingException;

    String searchVoteByPkType(String type, String token) throws Exception;

    String searchManagerCount(String token)throws Exception;

    String searchVoteViewById(String vid) throws JsonProcessingException;
    String countingVote(int voteId) throws JsonProcessingException;
}
