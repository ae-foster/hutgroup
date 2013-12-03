package uk.ac.cam.queens.w3;

import java.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * User: jh
 * Date: 12/3/13
 * Time: 7:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class Customer {
    private int customerId;
    Vector<Order> ordersPlaced;

    public Customer(int customerId, Vector<Order> ordersPlaced) {
        this.customerId = customerId;
        this.ordersPlaced = ordersPlaced;
    }

    public int getCustomerId (){
        return customerId;
    }

    public Vector<Order> getOrders (){
        return ordersPlaced;
    }
}
