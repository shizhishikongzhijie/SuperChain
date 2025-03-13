package com.example.SuperChain.util;

import com.example.SuperChain.bean.Vote;
import com.example.SuperChain.bean.VoteInfo;

import java.util.*;


public class CountingVote {

    private Map<String, List<String>> question;

    private List<Map<String, List<Integer>>> resultFormatted;

    private List<Map<String, List<String>>> voteResultInfos;

    public CountingVote(Map<String, List<String>> question, List<Map<String, List<String>>> voteResultInfos) {
        this.question = question;
        this.voteResultInfos = voteResultInfos;
    }

    //获取提纯后的投票Vote,只保留Vote的question
    public void getExtractedVote(int voteId) {
        question = new TreeMap<>();
        //调用合约方法 up voteId : return Vote

        Vote voteGot = new Vote();//示例

        Vote vote = new Vote(voteGot);
        question = vote.getQuestion();
    }


    //获取提纯后的投票VoteInfo，如果以List<VoteInfo>形式传入，则需要遍历来去除不必要的信息，只保留VoteInfo的info
    public void getExtractedVoteInfo(int voteId) {

        //调用合约方法 up voteId : return List<VoteInfo>
        List<VoteInfo> voteInfoList = new LinkedList<>();//示例

        voteResultInfos = new LinkedList<>();//①底层数据结构是链表，查询慢，增删快。②线程不安全，效率高。
        for (VoteInfo voteInfo : voteInfoList) {
            Map<String, List<String>> info = voteInfo.getInfo();
            voteResultInfos.add(info);
        }
    }

    //将VoteInfo的info，根据Vote的question格式化
    //如果question的key和result的key匹配，看result这个key的value（list)的值在question这个key的value（list）中的位置
    //最后返回一个List<Map<String,List<Integer>>>
    public void formatVoteInfo() {
        resultFormatted = new LinkedList<>(); // ①底层数据结构是链表，查询慢，增删快。②线程不安全，效率高。

        for (Map<String, List<String>> voteResultInfo : voteResultInfos) {
            Map<String, List<Integer>> resultFormattedMap = new TreeMap<>();//②底层数据结构是树，查询快，增删慢。
            for (Map.Entry<String, List<String>> entry : voteResultInfo.entrySet()) {
                String optionKey = entry.getKey();
                List<String> optionValues = entry.getValue();
                List<Integer> formattedValues = new ArrayList<>();

                for (String optionValue : optionValues) {
                    System.out.println("optionKey:" + optionKey + " question:" + question);
                    List<String> questionOptions = question.get(optionKey);
                    if (questionOptions != null) {
                        // 处理边界情况：如果选项值不在问题列表中，添加-1表示未找到
                        int index = questionOptions.indexOf(optionValue);
                        formattedValues.add(index >= 0 ? index : -1);
                    } else {
                        // 如果question中没有相应的key，记录为-1并继续处理下一个值
                        formattedValues.add(-1);
                    }
                }

                resultFormattedMap.put(optionKey, formattedValues);
            }
            resultFormatted.add(resultFormattedMap);
        }

    }

    //计算投票结果(最终)
    //更具question确定每个List选项的选择的个数;
    //根据resultFormatted，计算投票结果，返回一个Map<String,List<int>>,其中key为question的key，value为每个选项的选择的个数,默认值为0
    public Map<String, List<Integer>> calculateVoteResult() {
        Map<String, List<Integer>> voteResult = new HashMap<>();
        for (Map<String, List<Integer>> resultFormattedMap : resultFormatted) {
            for (Map.Entry<String, List<Integer>> entry : resultFormattedMap.entrySet()) {
                String optionKey = entry.getKey();
                List<Integer> optionValues = entry.getValue();

                // 如果voteResult中还没有这个选项，则初始化一个长度为question中选项个数的列表
                if (!voteResult.containsKey(optionKey)) {
                    List<Integer> initialValues = new ArrayList<>(Collections.nCopies(question.get(optionKey).size(), 0));
                    voteResult.put(optionKey, initialValues);
                    for (int index : optionValues) {
                        if (index >= 0) {
                            voteResult.get(optionKey).set(index, voteResult.get(optionKey).get(index) + 1);
                        }
                    }
                }else {
                    for (int index : optionValues) {
                        if (index >= 0) {
                            voteResult.get(optionKey).set(index, voteResult.get(optionKey).get(index) + 1);
                        }
                    }
                }
            }
        }
        return voteResult;
    }
}
