package com.zostio.gthai.training;

import com.zostio.gthai.StatUtils;
import com.zostio.gthai.networkcomponents.NeuralNetwork;
import com.zostio.gthai.networkcomponents.Neuron;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class NeuralNetworkStrategy {

    public static final int RUN_FOREVER = 0;

    protected ArrayList<TrainingData> trainingDataSet;

    protected double learningRate = 0.05;
    protected String networkSaveDir;

    protected String status = "";

    protected NeuralNetwork neuralNetwork;

    protected int[] hiddenLayersNeurons;
    protected int outputLayerNeurons;

    protected boolean progressPrintout = false;

    protected OnTrainingFinishedListener onTrainingFinishedListener;
    protected OnProgressListener onProgressListener;

    protected boolean enableVisuals = false;

    protected int visualRefreshFreq = 1000;

    protected int iterationTime = 0;

    protected int iterations = 10000;
    protected int currentIteration = 0;

    protected long iterationStartTime = 0;
    protected long iterationEndTime = 0;

    protected int savingIteration = 0;

    DecimalFormat numberFormat = new DecimalFormat("0.00");

    public NeuralNetworkStrategy(int[] hiddenLayersNeurons, int outputLayerNeurons) {
        configureNeuralNetwork(hiddenLayersNeurons, outputLayerNeurons, 0);
    }

    public NeuralNetworkStrategy(int[] hiddenLayersNeurons, int outputLayerNeurons, int iterations) {
        configureNeuralNetwork(hiddenLayersNeurons, outputLayerNeurons, iterations);
    }

    protected void configureNeuralNetwork(int[] hiddenLayersNeurons, int outputLayerNeurons, int iterations) {
        StatUtils.printMessage("-------- Good To Have AI ---------");
        StatUtils.printMessage("Library version " + StatUtils.VERSION_CODE + " build " + StatUtils.BUILD_NUMBER);
        StatUtils.printMessage("----------------------------------");
        status = "Idling...";
        Neuron.setRangeWeight(-1,1);
        this.hiddenLayersNeurons = hiddenLayersNeurons;
        this.outputLayerNeurons = outputLayerNeurons;
        this.iterations = iterations;
    }

    // This function is used to train being forward and backward.


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

    protected void postPercentage() {
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
