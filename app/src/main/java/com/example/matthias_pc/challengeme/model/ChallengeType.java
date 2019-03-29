package com.example.matthias_pc.challengeme.model;

public class ChallengeType {

    private int value;
    private double aDouble;
    private double aDouble1;
    private ChallengeAttribute challengeAttribute;
    private ChallengeAttribute challengeAttribute2;



    public ChallengeType(double aDouble, ChallengeAttribute challengeAttribute, double aDouble1, ChallengeAttribute challengeAttribute2) {
        this.aDouble = aDouble;
        this.challengeAttribute = challengeAttribute;
        this.aDouble1 = aDouble1;
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

    public double getaDouble() {
        return aDouble;
    }

    public void setaDouble(double aDouble) {
        this.aDouble = aDouble;
    }

    public double getaDouble1() {
        return aDouble1;
    }

    public void setaDouble1(double aDouble1) {
        this.aDouble1 = aDouble1;
    }

    public ChallengeAttribute getChallengeAttribute2() {
        return challengeAttribute2;
    }

    public void setChallengeAttribute2(ChallengeAttribute challengeAttribute2) {
        this.challengeAttribute2 = challengeAttribute2;
    }
}
