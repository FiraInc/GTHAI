package com.zostio.gthai.training;

import java.io.Serializable;

public class DifferentResolutionsException extends Exception implements Serializable {

    @Override
    public void printStackTrace() {
        System.err.println("Error: the images you provided in this dataset had different resolutions!");
        super.printStackTrace();
    }
}
