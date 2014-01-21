package uk.ac.cam.queens.w3.predictors;

import java.util.ArrayList;

/**
 * Created by Adam on 17/12/13.
 */
public interface PredictionMaker {
    public void train();
    public ArrayList<Integer> getRecommendations (int customerId);
}
