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

        ArrayList<TestCase> testCustomers = mDataLoader.getTestCustomers();
        for (TestCase customer : testCustomers){
            System.out.println("Calculating recommendation for user: " + customer.getCustomerId());
        }
    }
}
