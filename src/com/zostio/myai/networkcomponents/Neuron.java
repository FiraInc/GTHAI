package com.zostio.myai.networkcomponents;

public class Neuron {
    // Static variables
    static double minWeightValue;
    static double maxWeightValue;

    // Non-Static Variables
    double[] weights;
    double[] cache_weights;
    private double bias;
    double gradient;
    private double activation;

    //Constructor for hidden/output neurons
    public Neuron(double weights[], double bias) {
        this.weights = weights;
        this.bias = bias;
        this.cache_weights = this.weights;
        this.gradient = 0;
    }

    // Constructor for the input neurons
    public Neuron(double activation){
        this.weights = null;
        this.bias = -1;
        this.cache_weights = this.weights;
        this.activation = activation;
        this.gradient = -1;
    }

    // Static function to set min and max weight for all variables
    public static void setRangeWeight(float min,float max) {
        minWeightValue = min;
        maxWeightValue = max;
    }

    // Function used at the end of the backprop to switch the calculated value in the
    // cache weight in the weights
    public void update_weight() {
        this.weights = this.cache_weights;
    }

    public double getActivation() {
        return activation;
    }

    public void setActivation(double activation) {
        this.activation = activation;
    }

    public double getBias() {
        return bias;
    }
}
