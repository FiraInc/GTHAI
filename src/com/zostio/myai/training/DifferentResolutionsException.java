package com.zostio.myai.training;

public class DifferentResolutionsException extends Exception {

    @Override
    public void printStackTrace() {
        System.err.println("Error: the images you provided in this dataset had different resolutions!");
        super.printStackTrace();
    }
}
