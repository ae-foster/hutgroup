package uk.ac.cam.queens.w3.util;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: jh
 * Date: 12/4/13
 * Time: 5:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestCase {
    int customerId;
    ArrayList<Integer> products;

    public TestCase(int customerId, ArrayList<Integer> products) {
        this.customerId = customerId;
        this.products = products;
    }

    public int getCustomerId() {
        return customerId;
    }

    public ArrayList<Integer> getProducts() {
        return products;
    }
}
