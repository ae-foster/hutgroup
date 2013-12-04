package uk.ac.cam.queens.w3;

/**
 * Created with IntelliJ IDEA.
 * User: jh
 * Date: 12/4/13
 * Time: 5:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestCase {
    int customerId;
    int productId;

    public TestCase(int customerId, int productId) {
        this.customerId = customerId;
        this.productId = productId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public int getProductId() {
        return productId;
    }
}
