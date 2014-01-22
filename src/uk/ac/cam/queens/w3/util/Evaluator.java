package uk.ac.cam.queens.w3.util;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: jh
 * Date: 12/5/13
 * Time: 11:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class Evaluator {
    public static double rateRecommendations (ArrayList<Integer> recommendations, ArrayList<Integer> orders){

        if (recommendations.size() != 6){
            System.out.println("Size of recommendations were not 6!");
            return -1;
        }

        // map@6 calculation
        double score = 0;
        for (int i = 0; i<recommendations.size(); i++){
            double tmpScore = 0;
            for (int j = 0; j<=i; j++){
                if (orderContains(recommendations.get(j),orders))
                    tmpScore++;
            }
            tmpScore = tmpScore / (i+1);
            score += tmpScore;
        }
        score = score / recommendations.size();

        return score;
    }

    private static boolean orderContains (int productId, ArrayList<Integer> orders){
        for (Integer product : orders)
            if (product == productId)
                return true;
        return false;
    }
}
