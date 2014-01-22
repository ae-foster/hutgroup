package uk.ac.cam.queens.w3.predictors;

import uk.ac.cam.queens.w3.*;
import uk.ac.cam.queens.w3.util.Customer;
import uk.ac.cam.queens.w3.util.Order;
import uk.ac.cam.queens.w3.util.Product;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * User: jh
 * Date: 12/5/13
 * Time: 11:00 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class Predictor implements PredictionMaker {
    DataLoader mDataLoader;
    private Double baselineExpDecay = 5 * 10E-11;

    public Predictor (DataLoader dataLoader) {
        mDataLoader = dataLoader;

        preTrain();
    }

    public void preTrain (){

    }

    public void train(HashMap<String,Double> params){
        if (params.get("baselineExpDecay") != null)
            baselineExpDecay = params.get("baselineExpDecay");
    }

    private static ArrayList<Product> cachedBaseline;

    public ArrayList<Product> initialiseBaseline(){

        // calculate cache
        if (cachedBaseline == null){
            ArrayList<Product> products = mDataLoader.getProducts();

            for (Customer customer : mDataLoader.getCustomers())
                for (Order order : customer.getOrders()){
                    long t = mDataLoader.getLatestTimeInTrainingSet().getTime() - order.getTransactionTime().getTime();
                    products.get(order.getProductId()).incrementWeightedCount(Math.exp(-1*t*baselineExpDecay));
                }

            // rescale so all weightedCounts are between 0 and 1
            // find maximum weighted count
            double maxWeightedCount = 0;
            for (int i = 0; i<products.size(); i++)
                maxWeightedCount = Math.max(maxWeightedCount,products.get(i).getWeightedCount());
            // normalize
            for (int i = 0; i<products.size(); i++)
                products.get(i).setWeightedCount(products.get(i).getWeightedCount()/maxWeightedCount);
            cachedBaseline = products;
        }

        // copy cache
        ArrayList<Product> products = new ArrayList<Product>();
        for (Product product : cachedBaseline)
            products.add(product.getProductId(),product.copy());

        return products;
    }

    public abstract ArrayList<Integer> getRecommendations (int customerId);

}
