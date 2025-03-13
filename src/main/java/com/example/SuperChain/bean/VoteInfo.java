package com.example.SuperChain.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.Map;
/**
 * @author shizhishi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class VoteInfo {
    private int voteId;//Vote的id
    private String uploader;
    private Map<String, List<String>> info;
}
