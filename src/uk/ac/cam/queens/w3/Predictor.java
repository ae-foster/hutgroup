package uk.ac.cam.queens.w3;

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
public class Predictor implements PredictionMaker{
    DataLoader mDataLoader;
    private double productIntersection [][]; // productIntersection [a][b] is the intersection a -> b, could be asymmetric!

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

    public ArrayList<Integer> getRecommendations (int customerId) {

        ArrayList<Product> products = mDataLoader.getProducts();
        if (mDataLoader.getCustomers()[customerId] != null){
            Vector<Order> orders = mDataLoader.getCustomers()[customerId].getOrders();
            for (Order order : orders){
                // for each order, sum intersection
                int productId = order.getProductId();
                for (int i = 0; i<productIntersection[productId].length; i++){
                    products.get(i).incrementWeightedCount(productIntersection[productId][i]);
                }
            }
        }
        Collections.sort(products,new Product.WeightedCountComparator());

        ArrayList<Integer> recommendations = new ArrayList<Integer>(6);
        for (int i = 0; i<6; i++){
            recommendations.add(products.get(i).getProductId());
        }
        return recommendations;
    }
}
