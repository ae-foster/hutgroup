package uk.ac.cam.queens.w3.predictors;

import uk.ac.cam.queens.w3.*;
import uk.ac.cam.queens.w3.util.Customer;
import uk.ac.cam.queens.w3.util.Order;
import uk.ac.cam.queens.w3.util.Product;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * User: jh
 * Date: 12/5/13
 * Time: 11:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class Predictor implements PredictionMaker {
    DataLoader mDataLoader;
    private double productIntersection [][]; // productIntersection [a][b] is the intersection a -> b, could be asymmetric!
    ArrayList<Product> mProducts;

    public Predictor (DataLoader dataLoader) {
        mDataLoader = dataLoader;

        train();
    }

    public void train(){
        productIntersection = new double[mDataLoader.getNumberOfProducts()][mDataLoader.getNumberOfProducts()];
        Customer[] customers = mDataLoader.getCustomers();

        for (int i = 0; i<mDataLoader.getNumberOfCustomers(); i++){
            Customer customer = customers[i];
            if (customer == null) continue;
            Vector<Order> orders = customer.getOrders();
            for (int j = 0; j<orders.size(); j++){
                for (int k = j+1; k<orders.size(); k++){
                    // customer i bought product k after product j
                    productIntersection[orders.get(j).getProductId()][orders.get(k).getProductId()] += 0.1;
                }
            }
        }
    }

    private static ArrayList<Product> cachedBaseline;

    public ArrayList<Product> initialiseBaseline(){

        // calculate cache
        if (cachedBaseline == null){
            ArrayList<Product> products = mDataLoader.getProducts();

            for (Customer customer : mDataLoader.getCustomers())
                for (Order order : customer.getOrders()){
                    long t = mDataLoader.getLatestTimeInTrainingSet().getTime() - order.getTransactionTime().getTime();
                    double A = 5 * 10E-11;
                    products.get(order.getProductId()).incrementWeightedCount(Math.exp(-1*t*A));
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


    public ArrayList<Integer> getRecommendations (int customerId) {

        mProducts = initialiseBaseline();

        /*
        if (mDataLoader.getCustomers()[customerId] != null){
            Vector<Order> orders = mDataLoader.getCustomers()[customerId].getOrders();
            for (Order order : orders){
                // for each order, sum intersection
                int productId = order.getProductId();
                for (int i = 0; i<productIntersection[productId].length; i++){
                    mProducts.get(i).incrementWeightedCount(productIntersection[productId][i]);
                }
            }
        }
        */
        Collections.sort(mProducts,new Product.WeightedCountComparator());

        ArrayList<Integer> recommendations = new ArrayList<Integer>(6);
        for (int i = 0; i<6; i++){
            recommendations.add(mProducts.get(i).getProductId());
        }
        return recommendations;
    }
}
