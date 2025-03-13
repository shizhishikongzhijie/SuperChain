package com.example.SuperChain.bean;

import lombok.*;

import java.sql.Date;
import java.util.List;
import java.util.Map;

/**
 * @author shizhishi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Setter
public class Vote {
    private int voteId;
    private String creator;//创建者的名字
    private int hasVotedCount;//已经投票的人数
    private int uploaderCount;//投票人数
    private String title;
    private int privacy; // 隐私度 0 or 1，隐私度为1时只有上传的公钥List以及creator可以查看信息
    private String description;
    private Date startDate;//投票开始时间
    private Date updateDate;//(管理员）更新投票的时间
    private Date limitDate;//投票截止的时间
    private List<String> publicKeyList;//上传的收集的学生的公钥集合（当隐私度为1时上传，否则为空)
    private Map<String, List<String>> question;//问题
    private String isMultiple;//是否为多选的字符串，0或1(默认为0 单选)

    public Vote(Vote voteGot) {
        this.voteId = voteGot.getVoteId();
        this.creator = voteGot.getCreator();
        this.hasVotedCount = voteGot.getHasVotedCount();
        this.uploaderCount = voteGot.getUploaderCount();
        this.title = voteGot.getTitle();
        this.privacy = voteGot.getPrivacy();
        this.description = voteGot.getDescription();
        this.startDate = voteGot.getStartDate();
        this.updateDate = voteGot.getUpdateDate();
        this.limitDate = voteGot.getLimitDate();
        this.publicKeyList = voteGot.getPublicKeyList();
        this.question = voteGot.getQuestion();
        this.isMultiple = voteGot.getIsMultiple();
    }

    public Vote(int voteId, String creator, int hasVotedCount,int uploaderCount, String title, int privacy, String description, String startDate,String updateDate, String limitDate) {
        this.voteId = voteId;
        this.creator = creator;
        this.hasVotedCount = hasVotedCount;
        this.uploaderCount = uploaderCount;
        this.title = title;
        this.privacy = privacy;
        this.description = description;
        this.startDate = Date.valueOf(startDate);
        this.updateDate = Date.valueOf(updateDate);
        this.limitDate = Date.valueOf(limitDate);

    }
}
