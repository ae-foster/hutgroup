package uk.ac.cam.queens.w3.predictors;

import uk.ac.cam.queens.w3.DataLoader;
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
 * Date: 22/01/14
 * Time: 17:03
 * To change this template use File | Settings | File Templates.
 */
public class ProductIntersectionPredictor extends Predictor{
    private double productIntersection [][]; // productIntersection [a][b] is the intersection a -> b, could be asymmetric!

    public ProductIntersectionPredictor (DataLoader dataLoader) {
        super(dataLoader);
    }

    public void train(HashMap<String,Double> params){

        // must train parent class
        super.train(params);

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

        ArrayList<Product> products = initialiseBaseline();
        ArrayList<Product> products2 = initialiseBaseline();


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

        Collections.sort(products, new Product.WeightedCountComparator());
        Collections.sort(products2,new Product.WeightedCountComparator());

        int slotsToUseForIntersection = 3;

        ArrayList<Integer> recommendations = new ArrayList<Integer>();
        for (int i = 0; i<slotsToUseForIntersection; i++){
            recommendations.add(products.get(i).getProductId());
        }
        for (int i = 0; i<6; i++){
            if (recommendations.size() == 6) return  recommendations;
            boolean coolFound = false;
            for (int j = 0; j<slotsToUseForIntersection; j++){
                if (products2.get(i).getProductId() == recommendations.get(j))
                    coolFound = true;
            }
            if (coolFound) continue;
            recommendations.add(products2.get(i).getProductId());
        }
        return recommendations;
    }
}
