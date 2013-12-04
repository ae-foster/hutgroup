package uk.ac.cam.queens.w3;


import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * User: jh
 * Date: 12/3/13
 * Time: 7:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class DataLoader {
    private final static String trainFile = "train.csv";
    private final static String dataPath = "data/";
    private Customer[] customers = new Customer[500000]; // ~300k users
    private Product[] products = new Product[1000]; //505 products in dataset
    private ArrayList<Pair<Integer,Integer>> testCustomers = new ArrayList<Pair<Integer,Integer>>();

    public DataLoader (int trainRows, int testRows) throws IOException {

        loadTrainingFile(trainRows,testRows);

        System.out.println("Data loaded successfully");
    }

    private void loadTrainingFile(int trainDataLines, int testDataLines) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(dataPath+trainFile));
        String line;
        int linesRead = 0;

        // skip first line:
        // Customer_Id,(No column name),Order_Created,Product_Id
        br.readLine();

        // read training data
        int maxProductId = 0;
        SimpleDateFormat dataParser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        while ((line = br.readLine()) != null && linesRead < trainDataLines) {
            // process the line.
            List<String> items = Arrays.asList(line.split("\\s*,\\s*"));
            int customerId = Integer.parseInt(items.get(0));
            Date date;

            try {
                date = dataParser.parse(items.get(2));
            } catch (ParseException e){
                System.err.println("Failed to parse date");
                e.printStackTrace();
                return;
            }

            Order order = new Order(Integer.parseInt(items.get(1)), date,items.get(3));
            maxProductId = Math.max(maxProductId,order.getProductId());

            if (customers[customerId] == null){
                Customer customer = new Customer(customerId,new Vector<Order>());
                customers[customerId] = customer;
            }

            if (products[order.getProductId()] == null){
                products[order.getProductId()] = new Product(order.getProductId());
            }

            customers[customerId].getOrders().add(order);
            products[order.getProductId()].incrementCount();
            products[order.getProductId()].incrementWeightedCount(date.getTime() / 2000000000);

            linesRead++;
            if (linesRead % 500000 == 0)
                System.out.println("Read " + linesRead  + " lines");
        }

        // read test data
        while ((line = br.readLine()) != null && linesRead < (trainDataLines+testDataLines)) {
            // process the line.
            List<String> items = Arrays.asList(line.split("\\s*,\\s*"));
            testCustomers.add(new Pair<Integer, Integer>(Integer.parseInt(items.get(0)),Integer.parseInt(items.get(1))));
            linesRead++;
        }

        br.close();
        System.out.println("Read " + linesRead + " lines");
    }

    public ArrayList<Pair<Integer, Integer>> getTestCustomers() {
        return testCustomers;
    }
}
