package com.zostio.gthai.training;

import com.zostio.gthai.StatUtils;
import com.zostio.gthai.networkcomponents.NeuralNetwork;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class KnownDatasetStrategy extends NeuralNetworkStrategy {

    public KnownDatasetStrategy(int[] hiddenLayersNeurons, int outputLayerNeurons) {
        super(hiddenLayersNeurons, outputLayerNeurons);
    }

    public KnownDatasetStrategy(int[] hiddenLayersNeurons, int outputLayerNeurons, int iterations) {
        super(hiddenLayersNeurons, outputLayerNeurons, iterations);
    }

    public void startTraining(ArrayList<TrainingData> trainingDataSet) {
        status = "Starting training...";
        if (trainingDataSet == null) {
            StatUtils.printMessage("trainingDataSet cannot be null");
            status = "Error";
            return;
        }

        this.trainingDataSet = trainingDataSet;

        int[] layerNeurons = new int[hiddenLayersNeurons.length + 2];
        for (int i = 0; i < layerNeurons.length; i++) {
            if (i==0) {
                layerNeurons[i] = this.trainingDataSet.get(0).getData().length;
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
        if (trainingDataSet == null) {
            StatUtils.printMessage("Cannot find any training data");
            status = "Error";
            return;
        }

        if (trainingDataSet.size() == 0) {
            StatUtils.printMessage("Could not find any training data");
            status = "Error";
            return;
        }

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
            for(int j = 0; j < trainingDataSet.size(); j++) {
                neuralNetwork.forward(trainingDataSet.get(j).getData());
                neuralNetwork.backward(learningRate,trainingDataSet.get(j));
            }
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
}
