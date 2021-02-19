package com.zostio.myai.training;

import com.zostio.myai.StatUtils;
import com.zostio.myai.networkcomponents.NeuralNetwork;
import com.zostio.myai.networkcomponents.Neuron;

import java.util.ArrayList;

public class NeuralNetworkManager {

    public static final int RUN_FOREVER = 0;

    private int iterations = 10000;
    private double learningRate = 0.05;

    private int currentIteration = 0;

    private ArrayList<TrainingData> trainingDataSet;
    private NeuralNetwork neuralNetwork;

    private int[] hiddenLayersNeurons;
    private int outputLayerNeurons;

    private boolean progressPrintout = false;

    private OnTrainingFinishedListener onTrainingFinishedListener;
    private OnProgressListener onProgressListener;

    public NeuralNetworkManager(int[] hiddenLayersNeurons, int outputLayerNeurons) {
        configureNeuralNetwork(hiddenLayersNeurons, outputLayerNeurons, 0);
    }

    public NeuralNetworkManager(int[] hiddenLayersNeurons, int outputLayerNeurons, int iterations) {
        configureNeuralNetwork(hiddenLayersNeurons, outputLayerNeurons, iterations);
    }

    private void configureNeuralNetwork(int[] hiddenLayersNeurons, int outputLayerNeurons, int iterations) {
        Neuron.setRangeWeight(-1,1);
        this.hiddenLayersNeurons = hiddenLayersNeurons;
        this.outputLayerNeurons = outputLayerNeurons;
        this.iterations = iterations;
    }

    public void startTraining(ArrayList<TrainingData> trainingDataSet) {
        if (trainingDataSet == null) {
            StatUtils.printMessage("trainingDataSet cannot be null");
            return;
        }
        this.trainingDataSet = trainingDataSet;

        //start posting percentage of progress
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                postPercentage();
            }
        });
        thread.start();

        int[] layerNeurons = new int[hiddenLayersNeurons.length + 2];
        for (int i = 0; i < layerNeurons.length; i++) {
            if (i==0) {
                layerNeurons[i] = trainingDataSet.get(0).getData().length;
                continue;
            }else if (i==layerNeurons.length-1) {
                layerNeurons[i] = outputLayerNeurons;
                continue;
            }
            layerNeurons[i] = hiddenLayersNeurons[i-1];
        }

        neuralNetwork = new NeuralNetwork(layerNeurons);

        train();
    }

    // This function is used to train being forward and backward.
    private void train() {
        for(int i = 0; i < iterations || iterations == 0; i++) {
            currentIteration = i;
            for(int j = 0; j < trainingDataSet.size(); j++) {
                neuralNetwork.forward(trainingDataSet.get(j).getData());
                neuralNetwork.backward(learningRate,trainingDataSet.get(j));
            }
        }
        if (onTrainingFinishedListener != null) {
            onTrainingFinishedListener.trainingFinished();
        }
    }

    public void stopTrainingWhenPossible() {
        iterations = 1;
    }

    public void setRangeWeight(float start, float end) {
        Neuron.setRangeWeight(start,end);
    }

    public void setIterations(int iterations) {
        this.iterations = iterations;
    }

    public void setLearningRate(double learningRate) {
        this.learningRate = learningRate;
    }

    public void setProgressPrintout(boolean bool) {
        progressPrintout = bool;
    }

    public void setOnTrainingFinishedListener(OnTrainingFinishedListener onTrainingFinishedListener) {
        this.onTrainingFinishedListener = onTrainingFinishedListener;
    }

    public void setOnProgressListener(OnProgressListener onProgressListener) {
        this.onProgressListener = onProgressListener;
    }

    public double[] testNeuralNetwork(double[] input) {
        if (input.length == trainingDataSet.get(0).getData().length) {
            neuralNetwork.forward(input);
            return neuralNetwork.getResult();
        }else {
            StatUtils.printMessage("Error, input should have a length of " + trainingDataSet.get(0).getData().length + " according to the training data");
            return null;
        }
    }

    public double[] testNeuralNetwork(TrainingData input) {
        if (input.getData().length == trainingDataSet.get(0).getData().length) {
            neuralNetwork.forward(input.getData());
            return neuralNetwork.getResult();
        }else {
            StatUtils.printMessage("Error, input should have a length of " + trainingDataSet.get(0).getData().length + " according to the training data");
            return null;
        }

    }

    private void postPercentage() {
        if (currentIteration >= iterations-1 && iterations != 0) {
            return;
        }
        try {
            if (onProgressListener != null) {
                if (iterations == 0) {
                    onProgressListener.progressChanged(0,currentIteration,iterations);
                }else {
                    onProgressListener.progressChanged(currentIteration/iterations*100,currentIteration,iterations);
                }

            }
            if (progressPrintout) {
                if (iterations == 0) {
                    StatUtils.printMessage("Progress: Iteration: " + currentIteration);
                }else {
                    StatUtils.printMessage("Progress: " + (int)((double)currentIteration/(double)iterations*100) + "% (Iteration: " + currentIteration + "/" + iterations +  ")");
                }
            }
            Thread.sleep(1000);
            postPercentage();
        }catch (Exception e) {
            e.printStackTrace();
            try {
                Thread.sleep(1000);
                postPercentage();
            }catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
        }
    }
}
