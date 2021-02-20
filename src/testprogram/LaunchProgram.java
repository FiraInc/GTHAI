package testprogram;

import com.zostio.myai.StatUtils;
import com.zostio.myai.networkcomponents.NeuralNetwork;
import com.zostio.myai.training.*;

import java.util.ArrayList;

class LaunchProgram {

    private ArrayList<TrainingData> trainingDataSet;
    private ArrayList<TrainingData> testData;

    boolean loadedOnce = false;
    public void startProgram() {
        prepareDataSet();
        NeuralNetworkManager neuralNetworkManager = new NeuralNetworkManager(new int[]{512,512,256,128,64,32}, 2,20000);
        neuralNetworkManager.setVisualRepresentation(true);
        neuralNetworkManager.setVisualUpdateFreq(120);
        neuralNetworkManager.setSavingIteration("C:\\Users\\Johan Svartdal\\Desktop\\GTHAI\\Trainingdata", 100);
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

                neuralNetworkManager.saveNeuralNetwork("C:\\Users\\Johan Svartdal\\Desktop\\GTHAI\\Trainingdata");
                System.out.println("DONE NUMBER ONE, TRYING TO LOAD AGAIN");
                if (!loadedOnce) {
                    testLoadingNetwork();
                }
            }
        });
    }

    private void testLoadingNetwork() {
        loadedOnce = true;
        NeuralNetworkManager manager = new NeuralNetworkManager(new int[]{64,16,16}, 2,10000);
        manager.loadNeuralNetwork("C:\\Users\\Johan Svartdal\\Desktop\\GTHAI\\Trainingdata");
        manager.setProgressPrintout(true);
        manager.startTraining(trainingDataSet);
    }

    private void prepareDataSet() {
        TrainingDataEncoder trainingDataEncoder = new TrainingDataEncoder();
        try {
            trainingDataSet = trainingDataEncoder.getTrainingDataSet("C:\\Users\\Johan Svartdal\\Desktop\\GTHAI\\Trainingdata\\five", new double[]{0,1});
            trainingDataSet.addAll(trainingDataEncoder.getTrainingDataSet("C:\\Users\\Johan Svartdal\\Desktop\\GTHAI\\Trainingdata\\two", new double[]{1,0}));
            testData = trainingDataEncoder.getTrainingDataSet("C:\\Users\\Johan Svartdal\\Desktop\\GTHAI\\Trainingdata\\test", new double[]{0,1});
        } catch (DifferentResolutionsException e) {
            e.printStackTrace();
        }
    }
}
