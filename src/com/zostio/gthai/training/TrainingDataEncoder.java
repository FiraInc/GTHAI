package com.zostio.gthai.training;

import com.zostio.gthai.StatUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

public class TrainingDataEncoder implements Serializable {

    private ArrayList<File> filesInFolderClean = new ArrayList<>();

    private int resizeDataTo = 0;
    private boolean progressPrintOut;

    public TrainingDataEncoder(boolean progressPrintout) {
        this.progressPrintOut = progressPrintout;
    }

    public TrainingDataEncoder() {
        this.progressPrintOut = false;
    }

    public ArrayList<TrainingData> getTrainingDataSet(String folder, double[] correctAnswer) throws DifferentResolutionsException {
        ArrayList<TrainingData> trainingDataSet = new ArrayList<>();

        getFilesInFolder(folder);
        BufferedImage[] dataSet = getImages();
        for (int i = 0; i < dataSet.length; i++) {
            TrainingData trainingData = new TrainingData(correctAnswer);
            trainingData.setFileName(filesInFolderClean.get(i).getName());
            double[] pixelValues = getPixelValuesOfImage(dataSet[i]);
            trainingData.setData(pixelValues);
            trainingDataSet.add(trainingData);
        }

        return trainingDataSet;
    }

    private double[] getPixelValuesOfImage(BufferedImage bufferedImage) {
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();

        ArrayList<Integer> pixels = new ArrayList<>();

        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                pixels.add(bufferedImage.getRGB(w,h));
            }
        }

        double[] pixelArray = new double[pixels.size()];

        for (int i = 0; i < pixels.size(); i++) {
            pixelArray[i] = pixels.get(i);
        }

        return pixelArray;
    }

    private void getFilesInFolder(String folder) {
        StatUtils.printMessage("Trying to load images in folder: " + folder);
        File folderDir = new File(folder);
        File[] filesInFolderMess = folderDir.listFiles();
        if (filesInFolderMess == null) {
            StatUtils.printMessage("Could not find the folder specified");
            return;
        }
        StatUtils.printMessage("Found " + filesInFolderMess.length + " files in folder.");

        filesInFolderClean = new ArrayList<>();
        for (int i = 0; i < filesInFolderMess.length;i++) {
            if (progressPrintOut) {
                StatUtils.printMessage("Working on image " + i);
            }
            if (filesInFolderMess[i].getName().endsWith(".png") || filesInFolderMess[i].getName().endsWith(".jpg")) {
                filesInFolderClean.add(filesInFolderMess[i]);
            }else {
                StatUtils.printMessage("Image " + i + ": " + filesInFolderMess[i] + " is of wrong type");
            }
        }
    }

    private BufferedImage[] getImages() throws DifferentResolutionsException {
        BufferedImage[] images = new BufferedImage[filesInFolderClean.size()];
        for (int i = 0; i < filesInFolderClean.size(); i++) {
            File file = filesInFolderClean.get(i);
            images[i] = getImageFromFile(file);
            if (resizeDataTo != 0) {
                if (progressPrintOut) {
                    StatUtils.printMessage("Resizing image" + i + "...");
                }
                images[i] = resizeImage(images[i],resizeDataTo);
            }
            if (i != 0) {
                if (images[i].getWidth() != images[i-1].getWidth() || images[i].getHeight() != images[i-1].getHeight()) {
                    throw new DifferentResolutionsException();
                }
            }
        }

        return images;
    }

    private BufferedImage getImageFromFile(File file) {
        BufferedImage img = null;
        try {
            img = ImageIO.read(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return img;
    }

    private BufferedImage resizeImage(BufferedImage originalImage, int targetWidth) {
        int targetHeight = originalImage.getHeight()/originalImage.getWidth()*targetWidth;

        if (targetHeight < 1) {
            targetHeight = 1;
        }

        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = resizedImage.createGraphics();
        graphics2D.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        graphics2D.dispose();
        return resizedImage;
    }

    public void setImageResize(int newWidth) {
        if (resizeDataTo > -1) {
            resizeDataTo = newWidth;
        }else {
            StatUtils.printMessage("Cannot resize image to a width lower than 0");
        }
    }
}
