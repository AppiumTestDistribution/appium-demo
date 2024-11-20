package com.atd.test;

public class AIResponse {
    private final Object answer;      // Can be String or String[]
    private final Object explanation; // Can be String or String[]

    public AIResponse(Object answer, Object explanation) {
        this.answer = answer;
        this.explanation = explanation;
    }

    public Object getAnswer() {
        return answer;
    }

    public Object getExplanation() {
        return explanation;
    }

    // Helper methods for answer
    public boolean isAnswerArray() {
        return answer instanceof String[];
    }

    public String[] getAnswerAsArray() {
        if (answer instanceof String[]) {
            return (String[]) answer;
        }
        throw new IllegalStateException("Answer is not an array");
    }

    public String getAnswerAsString() {
        if (answer instanceof String) {
            return (String) answer;
        }
        throw new IllegalStateException("Answer is not a string");
    }

    // Helper methods for explanation
    public boolean isExplanationArray() {
        return explanation instanceof String[];
    }

    public String[] getExplanationAsArray() {
        if (explanation instanceof String[]) {
            return (String[]) explanation;
        }
        throw new IllegalStateException("Explanation is not an array");
    }

    public String getExplanationAsString() {
        if (explanation instanceof String) {
            return (String) explanation;
        }
        throw new IllegalStateException("Explanation is not a string");
    }

    @Override
    public String toString() {
        String answerStr = isAnswerArray() ? java.util.Arrays.toString(getAnswerAsArray()) : getAnswerAsString();
        String explanationStr = isExplanationArray() ? java.util.Arrays.toString(getExplanationAsArray()) : getExplanationAsString();
        return "Answer: " + answerStr + ", Explanation: " + explanationStr;
    }
}
