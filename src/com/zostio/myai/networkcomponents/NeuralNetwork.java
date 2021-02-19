package com.zostio.myai.networkcomponents;

import com.zostio.myai.StatUtils;
import com.zostio.myai.training.TrainingData;

public class NeuralNetwork{

    private NeuronLayer[] neuronLayers;

    public NeuralNetwork(int[] amountOfNeuronsInLayers) {
        createLayers(amountOfNeuronsInLayers);
    }

    private void createLayers(int[] amountOfNeuronsInLayers) {
        neuronLayers = new NeuronLayer[amountOfNeuronsInLayers.length];
        for (int i = 0; i < amountOfNeuronsInLayers.length; i++) {
            if (i == 0) {
                neuronLayers[i] = null;
                continue;
            }
            int amountOfNeuronsBefore = amountOfNeuronsInLayers[i-1];
            neuronLayers[i] = new NeuronLayer(amountOfNeuronsBefore,amountOfNeuronsInLayers[i]);
        }
    }

    public void forward(double[] trainingData) {
        neuronLayers[0] = new NeuronLayer(trainingData);

        for(int i = 1; i < neuronLayers.length; i++) {
            for(int j = 0; j < neuronLayers[i].neurons.length; j++) {
                double sum = 0;
                for(int k = 0; k < neuronLayers[i-1].neurons.length; k++) {
                    sum += neuronLayers[i-1].neurons[k].getActivation()*neuronLayers[i].neurons[j].weights[k];
                }
                //sum += neuronLayers[i].neurons[j].getBias(); // TODO add in the bias
                neuronLayers[i].neurons[j].setActivation(StatUtils.getSigmoid(sum));
            }
        }
    }

    public void backward(double learning_rate, TrainingData tData) {
        int number_layers = neuronLayers.length;
        int out_index = number_layers-1;

        // Update the output layers
        // For each output
        for(int i = 0; i < neuronLayers[out_index].neurons.length; i++) {
            // and for each of their weights
            double output = neuronLayers[out_index].neurons[i].getActivation();
            double target = tData.getCorrectAnswer()[i];
            double derivative = output-target;
            double delta = derivative*(output*(1-output));
            neuronLayers[out_index].neurons[i].gradient = delta;
            for(int j = 0; j < neuronLayers[out_index].neurons[i].weights.length;j++) {
                double previous_output = neuronLayers[out_index-1].neurons[j].getActivation();
                double error = delta*previous_output;
                neuronLayers[out_index].neurons[i].cache_weights[j] = neuronLayers[out_index].neurons[i].weights[j] - learning_rate*error;
            }
        }

        //Update all the subsequent hidden layers
        for(int i = out_index-1; i > 0; i--) {
            // For all neurons in that layers
            for(int j = 0; j < neuronLayers[i].neurons.length; j++) {
                double output = neuronLayers[i].neurons[j].getActivation();
                double gradient_sum = sumGradient(j,i+1);
                double delta = (gradient_sum)*(output*(1-output));
                neuronLayers[i].neurons[j].gradient = delta;
                // And for all their weights
                for(int k = 0; k < neuronLayers[i].neurons[j].weights.length; k++) {
                    double previous_output = neuronLayers[i-1].neurons[k].getActivation();
                    double error = delta*previous_output;
                    neuronLayers[i].neurons[j].cache_weights[k] = neuronLayers[i].neurons[j].weights[k] - learning_rate*error;
                }
            }
        }

        // Here we do another pass where we update all the weights
        for(int i = 0; i< neuronLayers.length;i++) {
            for(int j = 0; j < neuronLayers[i].neurons.length;j++) {
                neuronLayers[i].neurons[j].update_weight();
            }
        }
    }

    // This function sums up all the gradient connecting a given neuron in a given layer
    public double sumGradient(int n_index,int l_index) {
        float gradient_sum = 0;
        NeuronLayer current_layer = neuronLayers[l_index];
        for(int i = 0; i < current_layer.neurons.length; i++) {
            Neuron current_neuron = current_layer.neurons[i];
            gradient_sum += current_neuron.weights[n_index]*current_neuron.gradient;
        }
        return gradient_sum;
    }

    public double[] getResult() {
        return new double[]{neuronLayers[neuronLayers.length-1].neurons[0].getActivation(), neuronLayers[neuronLayers.length-1].neurons[1].getActivation()};
    }
}
