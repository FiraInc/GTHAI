package com.zostio.gthai.training;

import java.io.Serializable;

public interface OnProgressListener extends Serializable {
    void progressChanged(int progressPercent, int currentIteration, int totalIterations);
}
