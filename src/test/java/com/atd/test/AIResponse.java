package com.atd.test;

public class AIResponse {
    private final boolean conditionSatisfied;
    private final String explanation;

    public AIResponse(boolean conditionSatisfied, String explanation) {
        this.conditionSatisfied = conditionSatisfied;
        this.explanation = explanation;
    }

    public boolean isConditionSatisfied() {
        return conditionSatisfied;
    }

    public String getExplanation() {
        return explanation;
    }

    @Override
    public String toString() {
        return "AIResponse{" +
                "conditionSatisfied=" + conditionSatisfied +
                ", explanation='" + explanation + '\'' +
                '}';
    }
}