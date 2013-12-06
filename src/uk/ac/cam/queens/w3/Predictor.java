package uk.ac.cam.queens.w3;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created with IntelliJ IDEA.
 * User: jh
 * Date: 12/5/13
 * Time: 11:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class Predictor {
    DataLoader mDataLoader;

    public Predictor (DataLoader dataLoader) {
        mDataLoader = dataLoader;
    }

    public ArrayList<Integer> getRecommendations (int customerId) {

        ArrayList<Product> products = mDataLoader.getProducts();
        Collections.sort(products,new Product.WeightedCountComparator());

        ArrayList<Integer> recommendations = new ArrayList<Integer>(6);
        for (int i = 0; i<6; i++){
            recommendations.add(products.get(i).getProductId());
        }
        return recommendations;
    }
}
