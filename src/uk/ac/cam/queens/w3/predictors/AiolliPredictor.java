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
 * Created by Adam on 17/12/13.
 */
public class AiolliPredictor implements PredictionMaker {

    DataLoader mDataLoader;
    private double productWeights [][]; // stores weights between products, w_{ij} as in Aiolli
    private int productIntersections[][]; // stores size of intersections of products i and j
                                             // the intersection matrix will now be symmetric, not so the weights
    private double weightSquareSums[]; // stores the norm squares of the weights
    private static double ALPHA = 0;
    private static double BETA = 0.2;
    private static final double THRESHOLD = 2E-5;

    public AiolliPredictor (DataLoader dataLoader) {
        mDataLoader = dataLoader;

        train(null);
    }

    public void train(HashMap<String,Double> params){

        // Build the intersection matrix WHICH I WILL ONLY HALF FILL!
        productIntersections = new int[mDataLoader.getNumberOfProducts()][mDataLoader.getNumberOfProducts()];
        Customer[] customers = mDataLoader.getCustomers();

        for (int i = 0; i<mDataLoader.getNumberOfCustomers(); i++){
            Customer customer = customers[i];
            if (customer == null) continue;
            Vector<Order> orders = customer.getOrders();
            for (int j = 0; j<orders.size(); j++){
                for (int k = j+1; k<orders.size(); k++){
                    // Customer i bought products j and k
                    // Shouldn't worry about ordering I think
                    // Perhaps add a function of the time between orders
                    // I have left half of the intersection matrix blank
                    int p_id1 = orders.get(j).getProductId();
                    int p_id2 = orders.get(k).getProductId();
                    productIntersections[p_id1 <= p_id2 ? p_id1 : p_id2][p_id1 > p_id2 ? p_id1 : p_id2] +=1;
                }
            }
        }

        // Build the weights matrix
        productWeights = new double[mDataLoader.getNumberOfProducts()][mDataLoader.getNumberOfProducts()];
        weightSquareSums = new double[mDataLoader.getNumberOfProducts()];
        ArrayList<Product> products = mDataLoader.getProducts(); // why is this one an array list?
        for (int i=0; i<mDataLoader.getNumberOfProducts(); ++i)
        {
            for (int j = i; j<mDataLoader.getNumberOfProducts(); ++j)
            {

                if (products.get(i).getCount() <= 0 || products.get(j).getCount() <= 0)
                {
                    productWeights[i][j] = 0;
                    productWeights[j][i] = 0;
                }
                else
                {
                    // NB: j >= i
                    productWeights[i][j] = productIntersections[i][j]/
                            (Math.pow(products.get(i).getCount(), ALPHA)*Math.pow(products.get(j).getCount(), 1 - ALPHA));
                    weightSquareSums[i] += productWeights[i][j]*productWeights[i][j];
                    productWeights[j][i] = productIntersections[i][j]/
                            (Math.pow(products.get(j).getCount(), ALPHA)*Math.pow(products.get(i).getCount(), 1 - ALPHA));
                            // Just swap i and j in denominator
                    weightSquareSums[j] += productWeights[j][i]*productWeights[j][i];

                    if (productIntersections[i][j] != 0)
                    System.out.println("Counts " + productIntersections[i][j] + " " + products.get(i).getCount() + " " +
                            products.get(j).getCount() + "Weights: " + productWeights[i][j] + " " + productWeights[j][i]);
                }
            }
        }

    }
    public ArrayList<Integer> getRecommendations (int customerId) {

        double[] tempWeightSum = new double[mDataLoader.getNumberOfProducts()];
        ArrayList<Product> products = mDataLoader.getProducts();
        int informationTotal = 0;
        if (mDataLoader.getCustomers()[customerId] != null){
            Vector<Order> orders = mDataLoader.getCustomers()[customerId].getOrders();
            System.out.println("This user has " + orders.size() + " historical transactions");
            for (Order order : orders){
                informationTotal += 1;
                // for each order, look at other products
                int productId = order.getProductId();
                for (int i = 0; i<productWeights[productId].length; i++){
                    // Sum up the weights associated if they are larger than threshold value
                    if (productWeights[i][productId] > THRESHOLD)
                        tempWeightSum[i] += productWeights[i][productId];
                }
            }
            // Rescale each value by norm squared and add baseline
            for (int i=0; i<mDataLoader.getNumberOfProducts(); ++i)
            {
                // Just watch to divide by zero, and add an "information factor"
                products.get(i).incrementWeightedCount(weightSquareSums[i] > 1E12 ?
                        Math.exp(-1/informationTotal)*tempWeightSum[i]/ Math.pow(weightSquareSums[i], BETA) : 0);
            }
        }

        else System.out.println("This user has not yet been encountered");
        Collections.sort(products, new Product.WeightedCountComparator());

        ArrayList<Integer> recommendations = new ArrayList<Integer>(6);
        for (int i = 0; i<6; i++){
            recommendations.add(products.get(i).getProductId());
            System.out.println("Recommending product " + products.get(i).getProductId() +
                    " Score: " + products.get(i).getWeightedCount());
        }
        return recommendations;

    }

}
