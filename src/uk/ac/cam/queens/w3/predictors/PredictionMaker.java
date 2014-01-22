package uk.ac.cam.queens.w3.predictors;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Adam on 17/12/13.
 */
public interface PredictionMaker {
    public void train(HashMap<String,Double> params);
    public ArrayList<Integer> getRecommendations (int customerId);
}
