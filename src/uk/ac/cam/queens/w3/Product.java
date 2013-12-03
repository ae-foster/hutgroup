package uk.ac.cam.queens.w3;

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
}
