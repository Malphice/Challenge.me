package com.example.matthias_pc.challengeme.model;

public class ChallengeType {

    private int value;
    private String StringValue;
    private String StringValue2;
    private ChallengeAttribute challengeAttribute;
    private ChallengeAttribute challengeAttribute2;



    public ChallengeType(String stringValue, ChallengeAttribute challengeAttribute, String stringValue2, ChallengeAttribute challengeAttribute2) {
        StringValue = stringValue;
        this.challengeAttribute = challengeAttribute;
        StringValue2 = stringValue2;
        this.challengeAttribute2 = challengeAttribute2;
    }

    public ChallengeType() {
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public ChallengeAttribute getChallengeAttribute() {
        return challengeAttribute;
    }

    public void setChallengeAttribute(ChallengeAttribute challengeAttribute) {
        this.challengeAttribute = challengeAttribute;
    }

    public String getStringValue() {
        return StringValue;
    }

    public void setStringValue(String stringValue) {
        StringValue = stringValue;
    }

    public String getStringValue2() {
        return StringValue2;
    }

    public void setStringValue2(String stringValue2) {
        StringValue2 = stringValue2;
    }

    public ChallengeAttribute getChallengeAttribute2() {
        return challengeAttribute2;
    }

    public void setChallengeAttribute2(ChallengeAttribute challengeAttribute2) {
        this.challengeAttribute2 = challengeAttribute2;
    }
}
