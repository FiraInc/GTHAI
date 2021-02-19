package com.zostio.myai.controller;

import com.zostio.myai.StatUtils;
import com.zostio.myai.training.*;

import java.util.ArrayList;

class LaunchProgram {

    ArrayList<TrainingData> trainingDataSet;
    ArrayList<TrainingData> testData;

    public void startProgram() {
        prepareDataSet();
        NeuralNetworkManager neuralNetworkManager = new NeuralNetworkManager(new int[]{64,16,16}, 2,60000);
        neuralNetworkManager.setProgressPrintout(true);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                neuralNetworkManager.startTraining(trainingDataSet);
            }
        });
        thread.start();

        neuralNetworkManager.setOnTrainingFinishedListener(new OnTrainingFinishedListener() {
            @Override
            public void trainingFinished() {
                for (int i = 0; i < testData.size(); i++) {
                    double[] testDataResults = neuralNetworkManager.testNeuralNetwork(testData.get(i));
                    String endGuess;
                    if (testDataResults[0] > testDataResults[1]) {
                        endGuess = "boy";
                    }else {
                        endGuess = "girl";
                    }
                    StatUtils.printMessage(testData.get(i).getFileName() + ": " + endGuess + ". Results: " + testDataResults[0] + ":" + testDataResults[1]);
                }
            }
        });
    }

    private void prepareDataSet() {
        TrainingDataEncoder trainingDataEncoder = new TrainingDataEncoder();
        try {
            trainingDataSet = trainingDataEncoder.getTrainingDataSet("/Users/johansvartdal/Desktop/AI/Trainingdata/Projectglasses/girls", new double[]{0,1});
            trainingDataSet.addAll(trainingDataEncoder.getTrainingDataSet("/Users/johansvartdal/Desktop/AI/Trainingdata/Projectglasses/boys", new double[]{1,0}));
            testData = trainingDataEncoder.getTrainingDataSet("/Users/johansvartdal/Desktop/AI/Trainingdata/Projectglasses/test", new double[]{0,1});
        } catch (DifferentResolutionsException e) {
            e.printStackTrace();
        }
    }
}
