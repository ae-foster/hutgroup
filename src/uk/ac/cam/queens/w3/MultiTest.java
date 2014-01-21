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

public class MultiTest {
    static NewDataLoader mDataLoader;
    static DataWriter mDataWriter;

    public static void main (String[] args){
        ArrayList<TestCase> testCustomers;

        // run test on part of training set or actual test file (test.csv)
        final boolean runOnRealTestSet = false;

        // load data
        try {

            //SortedDataLoader newDataLoader = new SortedDataLoader(2000000);

            mDataWriter = new DataWriter();
            mDataLoader = new NewDataLoader(500000);  // load data (NumberOfTrainingRows)
            if (!runOnRealTestSet)
                testCustomers = mDataLoader.getTestCustomers();
            else
                testCustomers = mDataLoader.getRealTestCustomersFromFile();
        } catch (IOException e){
            System.err.println("Could not load datafile");
            e.printStackTrace();
            return;
        }

        // create instance of predictor
        FlexiPredictor predictor = new FlexiPredictor(mDataLoader);


        for (int x = 10; x <= 15 ; x+=1)
        {
            double param1 = Math.pow(2, x);
            System.out.println("Parameter values " + param1) ;
            predictor.resetParameters(param1);
            predictor.train();

            System.out.println("Running over test cases, of which there are " + testCustomers.size());
            double totalScore = 0;

            for (TestCase testCase : testCustomers){
                // System.out.println("Calculating recommendation for user: " + testCase.getCustomerId());
                ArrayList<Integer> recommendations = predictor.getRecommendations(testCase.getCustomerId());

                if (runOnRealTestSet){
                    String outputLine = "";
                    for (Integer rec : recommendations){
                        outputLine += rec.toString() + ",";
                    }

                    // remove trailing comma
                    outputLine = outputLine.substring(0,outputLine.length()-1);

                    outputLine += "\n";
                    mDataWriter.write(outputLine);
                }

                double score = Evaluator.rateRecommendations(recommendations,testCase.getProductId());
                totalScore += score;

                // System.out.println("The answer was" + testCase.getProductId());
                // System.out.println("Result: " + score);
                // System.out.println();

            }
            totalScore = totalScore / testCustomers.size();
            System.out.println("Average score: " + totalScore);
        }

        mDataWriter.close();
    }

}
