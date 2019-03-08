package com.example.matthias_pc.challengeme.model;



public class Challenge {
    private Long startTime, endTime;
    private String description, uid;
    private ChallengeType challengeType;

    public Challenge(Long startTime, Long endTime,String desc, ChallengeType challengeType) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.description = desc;
        this.challengeType = challengeType;
    }

    public Challenge(){}

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ChallengeType getChallengeType() {
        return challengeType;
    }

    public void setChallengeType(ChallengeType challengeType) {
        this.challengeType = challengeType;
    }
}
