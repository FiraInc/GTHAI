package com.zostio.myai.training;

public interface OnProgressListener {
    void progressChanged(int progressPercent, int currentIteration, int totalIterations);
}
