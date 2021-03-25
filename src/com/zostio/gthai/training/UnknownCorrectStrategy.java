package com.zostio.gthai.training;

import com.zostio.gthai.StatUtils;
import com.zostio.gthai.networkcomponents.NeuralNetwork;

import java.util.ArrayList;

public class UnknownCorrectStrategy extends NeuralNetworkStrategy {

    private ReadyForFeedbackListener feedbackListener;
    private TrainingData correctAnswer;

    public UnknownCorrectStrategy(int[] hiddenLayersNeurons, int outputLayerNeurons) {
        super(hiddenLayersNeurons, outputLayerNeurons);
    }

    public UnknownCorrectStrategy(int[] hiddenLayersNeurons, int outputLayerNeurons, int iterations) {
        super(hiddenLayersNeurons, outputLayerNeurons, iterations);
    }

    public void startTraining(TrainingData trainingData, ReadyForFeedbackListener feedbackListener) {
        this.feedbackListener = feedbackListener;
        status = "Starting training...";
        if (trainingData == null) {
            StatUtils.printMessage("trainingData cannot be null");
            status = "Error";
            return;
        }

        correctAnswer = trainingData;

        int[] layerNeurons = new int[hiddenLayersNeurons.length + 2];
        for (int i = 0; i < layerNeurons.length; i++) {
            if (i==0) {
                layerNeurons[i] = correctAnswer.getData().length;
                continue;
            }else if (i==layerNeurons.length-1) {
                layerNeurons[i] = outputLayerNeurons;
                continue;
            }
            layerNeurons[i] = hiddenLayersNeurons[i-1];
        }

        neuralNetwork = new NeuralNetwork(layerNeurons);

        continueTraining();
    }

    public void continueTraining() {
        currentIteration = 0;

        if (enableVisuals) {
            VisualRepresentationManager visualRepresentationManager = new VisualRepresentationManager();
            visualRepresentationManager.startRepresenting(this);
        }


        //start posting percentage of progress
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                postPercentage();
            }
        });
        thread.start();

        train();
    }

    public void train() {
        for(int i = currentIteration; i < iterations || iterations == 0; i++) {
            if (i % 5 == 0) {
                iterationStartTime = System.currentTimeMillis();
            }

            if (iterations == 0) {
                status = "Training (Iterations: " + currentIteration + ")";
            }else {
                double percentage = (double)currentIteration/(double)iterations*100;
                status = "Training: " + numberFormat.format(percentage) + "% (" + currentIteration + "/" + iterations + ")";
            }


            if (savingIteration != 0 && currentIteration % savingIteration == 0) {
                saveNeuralNetwork(networkSaveDir);
            }
            currentIteration = i;
            neuralNetwork.forward(correctAnswer.getData());
            feedbackListener.readyForFeedback(neuralNetwork.getResult());
            neuralNetwork.backward(learningRate, correctAnswer);

            if (i % 5 == 0) {
                iterationEndTime = System.currentTimeMillis();
                iterationTime = (int)(iterationEndTime-iterationStartTime);
            }
        }
        if (onTrainingFinishedListener != null) {
            status = "Running training complete tasks...";
            onTrainingFinishedListener.trainingFinished();
        }
        status = "Training process finished! Idling";
    }

    public void provideFeedback(TrainingData correctAnswer) {
        this.correctAnswer = correctAnswer;
    }
}
