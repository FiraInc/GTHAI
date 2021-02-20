package com.zostio.myai.training;

import com.zostio.myai.StatUtils;
import com.zostio.myai.networkcomponents.NeuralNetwork;
import com.zostio.myai.networkcomponents.Neuron;

import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class NeuralNetworkManager {

    public static final int RUN_FOREVER = 0;

    private int iterations = 10000;
    private double learningRate = 0.05;

    private int currentIteration = 0;

    private int savingIteration = 0;
    private String networkSaveDir;

    private ArrayList<TrainingData> trainingDataSet;
    protected NeuralNetwork neuralNetwork;

    private int[] hiddenLayersNeurons;
    private int outputLayerNeurons;

    private boolean progressPrintout = false;

    private OnTrainingFinishedListener onTrainingFinishedListener;
    private OnProgressListener onProgressListener;

    private boolean enableVisuals = false;

    private String status = "";
    protected int visualRefreshFreq = 1000;

    private long iterationStartTime = 0;
    private long iterationEndTime = 0;
    private int iterationTime = 0;

    public NeuralNetworkManager(int[] hiddenLayersNeurons, int outputLayerNeurons) {
        configureNeuralNetwork(hiddenLayersNeurons, outputLayerNeurons, 0);
    }

    public NeuralNetworkManager(int[] hiddenLayersNeurons, int outputLayerNeurons, int iterations) {
        configureNeuralNetwork(hiddenLayersNeurons, outputLayerNeurons, iterations);
    }

    private void configureNeuralNetwork(int[] hiddenLayersNeurons, int outputLayerNeurons, int iterations) {
        StatUtils.printMessage("-------- Good To Have AI ---------");
        StatUtils.printMessage("Library version " + StatUtils.VERSION_CODE + " build " + StatUtils.BUILD_NUMBER);
        StatUtils.printMessage("----------------------------------");
        status = "Idling...";
        Neuron.setRangeWeight(-1,1);
        this.hiddenLayersNeurons = hiddenLayersNeurons;
        this.outputLayerNeurons = outputLayerNeurons;
        this.iterations = iterations;
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

    // This function is used to train being forward and backward.
    DecimalFormat numberFormat = new DecimalFormat("0.00");

    private void train() {
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

    public void setSavingIteration(String networkSaveDir, int iteration) {
        this.networkSaveDir = networkSaveDir;
        this.savingIteration = iteration;
    }

    public void saveNeuralNetwork(String directory) {
        if (directory == null) {
            StatUtils.printMessage("Could not save to folder specified. Folder cannot be null");
            return;
        }
        File directoryOfSave = new File(directory);
        if (!directoryOfSave.exists()) {
            directoryOfSave.mkdirs();
        }

        SerializationTool.serializeObject(directory, "neuralNetwork.gthnet", neuralNetwork);
    }

    public void loadNeuralNetwork(String directory) {
        neuralNetwork = (NeuralNetwork) SerializationTool.deSerializeObject(directory + File.separator + "neuralNetwork.gthnet");
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

    public void setVisualRepresentation(boolean enableVisuals) {
        this.enableVisuals = enableVisuals;
    }

    public void setVisualUpdateFreq(int timesASecond) {
        if (timesASecond > 0) {
            if (timesASecond > 120) {
                timesASecond = 120;
                StatUtils.printMessage("120 refreshes a second is the limit");
            }
            this.visualRefreshFreq = 1000/timesASecond;
        }else {
            StatUtils.printMessage("timesASecond cannot be negative");
        }
    }

    public String getEstimatedTimeLeft() {
        if (iterations == 0) {
            return "Unlimited...";
        }
        String estimatedTimeLeft = "Calculating...";
        if (iterationTime != 0) {
            int totalMillis = (int)((double)(iterations-currentIteration)*(double)iterationTime);
            int totalSeconds = totalMillis/1000;
            if (totalSeconds > 60) {
                int totalMinutes = totalSeconds/60;
                if (totalMinutes > 60) {
                    int totalHours = totalMinutes/60;
                    return totalHours + " hour(s)";
                }
                return totalMinutes + " minute(s)";
            }
            return totalSeconds + " second(s)";
        }
        return estimatedTimeLeft;
    }

    public String getStatus() {
        return status;
    }
}
