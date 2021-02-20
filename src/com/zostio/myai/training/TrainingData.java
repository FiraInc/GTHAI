package com.zostio.myai.training;

import java.io.Serializable;

public class TrainingData implements Serializable {
    private double[] data;
    private double[] correctAnswer;
    private String fileName;

    public TrainingData(double[] correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public void setData(double[] data) {
        this.data = data;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public double[] getData() {
        return data;
    }

    public double[] getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(double[] correctAnswer) {
        this.correctAnswer = correctAnswer;
    }
}
