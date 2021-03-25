package com.zostio.gthai.training;

import com.zostio.gthai.networkcomponents.NeuronLayer;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;

public class VisualRepresentationManager {

    private NeuralNetworkManager manager;
    private NeuralNetworkStrategy strategy;
    JFrame frame;
    JPanel panel;

    int playgroundWidth = 1000;
    int playgroundHeight = 1000;

    DecimalFormat numberFormat = new DecimalFormat("0.00");

    boolean readyToPack = false;
    boolean doneBigPack = false;

    public void startRepresenting(NeuralNetworkStrategy strategy) {
        this.strategy = strategy;
        createVisuals();
    }

    @Deprecated
    public void startRepresenting(NeuralNetworkManager neuralNetworkManager) {
        this.manager = neuralNetworkManager;
        createVisuals();
    }

    private void createVisuals() {
        //1. Create the frame.
        frame = new JFrame("Neural network representation");
        panel = new JPanel();

        JScrollPane scrollPane = new JScrollPane(panel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setWheelScrollingEnabled(true);


        //2. Optional: What happens when the frame closes?
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //3. Create components and put them in the frame.
        //...create emptyLabel...
        frame.getContentPane().add(scrollPane);

        //4. Size the frame.
        frame.pack();

        //5. Show it.
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setSize(playgroundWidth,playgroundHeight);
        frame.setMaximumSize(new Dimension(playgroundWidth,playgroundHeight));

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                refreshView();
            }
        });
        thread.start();
    }

    private void refreshView() {
        //frame.repaint();
        clearElementsFromPanel();
        addStatsPanel();
        addNeurons();
        //frame.invalidate();
        //frame.validate();
        panel.invalidate();
        panel.validate();

        if (readyToPack) {
            doneBigPack = true;
            readyToPack = false;
            fullRepack();
        }

        try {
            if (manager != null) {
                Thread.sleep(manager.visualRefreshFreq);
            }else {
                Thread.sleep(strategy.visualRefreshFreq);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        refreshView();
    }

    private void clearElementsFromPanel() {
        for (int i = 0; i < panel.getComponentCount(); i++) {
            if (panel.getComponent(i) instanceof JLabel || panel.getComponent(i) instanceof JPanel) {
                panel.remove(i);
                i = i-1;
            }
        }
    }

    private void addStatsPanel() {
        JPanel statsPanel = new JPanel();
        JLabel statusText = null;
        if (manager != null) {
            statusText = new JLabel(manager.getStatus());
        }else {
            statusText = new JLabel(strategy.getStatus());
        }

        statusText.setFont(new Font("Helvetica", Font.PLAIN, 20));
        statsPanel.add(statusText, BorderLayout.NORTH);

        JLabel eta = null;

        if (manager != null) {
            eta = new JLabel(manager.getEstimatedTimeLeft());
        }else {
            eta = new JLabel(strategy.getEstimatedTimeLeft());
        }

        eta.setFont(new Font("Helvetica", Font.PLAIN, 15));
        statsPanel.add(eta, BorderLayout.NORTH);

        statsPanel.setPreferredSize(new Dimension(playgroundWidth, playgroundHeight/10));
        panel.add(statsPanel);
    }

    public void addNeurons() {
        JPanel networkPanel = new JPanel();
        networkPanel.setPreferredSize(new Dimension(10000000,1000000));
        NeuronLayer[] neuronLayers = null;
        if (manager != null) {
            neuronLayers = manager.neuralNetwork.getLastLayers();
        }else {
            neuronLayers = strategy.neuralNetwork.getLastLayers();
        }

        if (neuronLayers == null) {
            return;
        }

        int mostNeurons = 0;
        for (int i = 0; i < neuronLayers.length; i++) {
            JPanel layerPanel = new JPanel();
            layerPanel.setPreferredSize(new Dimension(playgroundWidth/6,10000000));
            layerPanel.setBorder(BorderFactory.createEmptyBorder(0,800,0,800));
            if (neuronLayers[i] == null || neuronLayers[i].neurons == null) {
                continue;
            }

            String layerName = "";
            if (i == 0) {
                layerName = "Input layer";
            }else if (i == neuronLayers.length-1) {
                layerName = "Output layer";
            }else {
                layerName = "Hidden layer";
            }
            JLabel neuronCount = new JLabel(layerName +" (" + neuronLayers[i].neurons.length + ")");
            layerPanel.add(neuronCount, BorderLayout.SOUTH);

            if (mostNeurons < neuronLayers[i].neurons.length) {
                mostNeurons = neuronLayers[i].neurons.length;
            }

            for (int a = 0; a < neuronLayers[i].neurons.length; a++) {
                if (a == 1000) {
                    break;
                }

                double activation = neuronLayers[i].neurons[a].getActivation();
                if (activation > 1) {
                    activation = 1;
                }else if (activation < -1) {
                    activation = -1;
                }
                JLabel label = new JLabel(String.valueOf(numberFormat.format(activation)));

                if (i == 0) {
                    //label.setText("Input");
                }

                layerPanel.add(label, BorderLayout.SOUTH);

                if (!doneBigPack) {
                    readyToPack = true;
                }
            }
            networkPanel.add(layerPanel, BorderLayout.CENTER);
        }
        if (mostNeurons > 1000) {
            mostNeurons = 1000;
        }
        panel.add(networkPanel, BorderLayout.SOUTH);
        panel.setPreferredSize(new Dimension((playgroundWidth/6)*neuronLayers.length,mostNeurons*22));
    }

    private void fullRepack() {
        frame.pack();
        frame.setSize(playgroundWidth,playgroundHeight);
    }
}
