package uk.ac.cam.queens.w3;

import java.util.Comparator;

/**
 * Created with IntelliJ IDEA.
 * User: jh
 * Date: 12/3/13
 * Time: 8:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class Product {
    private int productId;
    private double count;
    private double weightedCount;

    public Product(int productId) {
        this.productId = productId;
        this.count = 0;
        this.weightedCount = 0;
    }

    // copy constructor
    public Product (Product product){
        this.productId = product.productId;
        this.count = product.count;
        this.weightedCount = product.weightedCount;
    }

    public int getProductId() {
        return productId;
    }

    public double getCount() {
        return count;
    }

    public double getWeightedCount() {
        return weightedCount;
    }

    public void incrementWeightedCount(double weightedCount) {
        this.weightedCount += weightedCount;
    }

    public void incrementCount() {
        this.count++;
    }

    public static class WeightedCountComparator implements Comparator<Product> {
        @Override
        public int compare(Product o1, Product o2) {
            return (o1.getWeightedCount()<o2.getWeightedCount() ? 1 : -1);
        }
    }
}
