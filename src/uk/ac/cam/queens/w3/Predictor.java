package uk.ac.cam.queens.w3;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: jh
 * Date: 12/3/13
 * Time: 7:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class Predictor {
    static DataLoader mDataLoader;

    public static void main (String[] args){

        // load data
        try {
            mDataLoader = new DataLoader(1000,100);  // load data (NumberOfTrainingRows,NumberOfTestRows)
        } catch (IOException e){
            System.err.println("Could not load datafile");
            e.printStackTrace();
            return;
        }

        // create instance of predictor
        Predictor predictor = new Predictor();

        ArrayList<TestCase> testCustomers = mDataLoader.getTestCustomers();
        double totalScore = 0;
        for (TestCase testCase : testCustomers){
            System.out.println("Calculating recommendation for user: " + testCase.getCustomerId());
            ArrayList<Integer> recommendations = predictor.getRecommendations(testCase.getCustomerId());
            double score = predictor.rateRecommendations(recommendations,testCase.getProductId());
            totalScore += score;
            System.out.println("Result: " + score);

        }
        totalScore = totalScore / testCustomers.size();
        System.out.println("Avarage score: " + totalScore);
    }

    public ArrayList<Integer> getRecommendations (int customerId) {
        ArrayList<Integer> recommendations = new ArrayList<Integer>(6);
        recommendations.add(10);
        recommendations.add(11);
        recommendations.add(12);
        recommendations.add(13);
        recommendations.add(14);
        recommendations.add(15);
        return recommendations;
    }

    public double rateRecommendations (ArrayList<Integer> recommendations, int productId){

        if (recommendations.size() != 6){
            System.out.println("Size of recommendations were not 6!");
            return -1;
        }

        // map@6 calculation
        double score = 0;
        for (int i = 0; i<recommendations.size(); i++){
            double tmpScore = 0;
            for (int j = 0; j<=i; j++){
                if (recommendations.get(j) == productId)
                    tmpScore++;
            }
            tmpScore = tmpScore / (i+1);
            score += tmpScore;
        }
        score = score / recommendations.size();

        return score;
    }
}
