package uk.ac.cam.queens.w3.util;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: jh
 * Date: 12/3/13
 * Time: 7:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class Order {
    private int customerId;
    private int productId;
    private Date transactionTime;
    private String countryCode;

    public Order(int customerId, int productId, Date transactionTime, String countryCode) {
        this.customerId = customerId;
        this.productId = productId;
        this.transactionTime = transactionTime;
        this.countryCode = countryCode;
    }

    public int getCustomerId() {return customerId;}

    public int getProductId() {
        return productId;
    }

    public Date getTransactionTime() {
        return transactionTime;
    }

    public String getCountryCode() {
        return countryCode;
    }
}
