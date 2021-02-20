package com.zostio.myai.networkcomponents;

import com.zostio.myai.StatUtils;

import java.io.Serializable;
import java.util.*;

public class NeuronLayer implements Serializable {

    public Neuron[] neurons;

    // Constructor for the hidden and output layer
    public NeuronLayer(int inNeurons, int numberNeurons) {
        this.neurons = new Neuron[numberNeurons];

        for(int i = 0; i < numberNeurons; i++) {
            double[] weights = new double[inNeurons];
            for(int j = 0; j < inNeurons; j++) {
                weights[j] = StatUtils.randomDouble(Neuron.minWeightValue, Neuron.maxWeightValue);
            }
            neurons[i] = new Neuron(weights,StatUtils.randomDouble(0, 1));
        }
    }


    // Constructor for the input layer
    public NeuronLayer(double[] input) {
        this.neurons = new Neuron[input.length];
        for(int i = 0; i < input.length; i++) {
            this.neurons[i] = new Neuron(input[i]);
        }
    }
}
