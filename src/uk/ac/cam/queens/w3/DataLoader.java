package uk.ac.cam.queens.w3;

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
    private final static String testFile = "test.csv";
    private final static String dataPath = "data/";
    private static final double maxTimeWeightingInitialValue = 0.05;
    private Customer[] customers = new Customer[500000]; // ~300k users
    private Product[] products = new Product[1000]; //505 products in dataset
    private int numberOfProducts;
    private int numberOfCustomers;

    private ArrayList<TestCase> testCustomers = new ArrayList<TestCase>();

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
        int maxCustomerId = 0;
        SimpleDateFormat dataParser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        while ((line = br.readLine()) != null && linesRead < trainDataLines) {
            // process the line.
            List<String> items = Arrays.asList(line.split("\\s*,\\s*"));
            int customerId = Integer.parseInt(items.get(0));
            Date date;

            try {
                date = dataParser.parse(items.get(2));
                date.setTime(date.getTime()-dataParser.parse("2012-04-01 00:00:00.000").getTime());
            } catch (ParseException e){
                System.err.println("Failed to parse date");
                e.printStackTrace();
                return;
            }

            Order order = new Order(Integer.parseInt(items.get(2)), Integer.parseInt(items.get(1)), date,items.get(3));
            maxProductId = Math.max(maxProductId,order.getProductId());
            maxCustomerId = Math.max(maxCustomerId,customerId);

            if (customers[customerId] == null){
                Customer customer = new Customer(customerId,new Vector<Order>());
                customers[customerId] = customer;
            }

            if (products[order.getProductId()] == null){
                products[order.getProductId()] = new Product(order.getProductId());
            }

            customers[customerId].getOrders().add(order);
            products[order.getProductId()].incrementCount();
            // But surely we don't know how this will calibrate
            products[order.getProductId()].incrementWeightedCount(date.getTime());


            linesRead++;
            if (linesRead % 500000 == 0)
                System.out.println("Read " + linesRead  + " lines");
        }
        numberOfProducts = maxProductId+1;
        numberOfCustomers = maxCustomerId+1;

        // initialise all products
        for (int i = 0; i<numberOfProducts; i++){
            if (products[i] == null){
                products[i] = new Product(i);
            }
        }

        // rescale so all weightedCounts are between 0 and 0.02
        // find maximum weighted count
        double maxWeightedCount = 0;
        for (int i = 0; i<numberOfProducts; i++)
            maxWeightedCount = Math.max(maxWeightedCount,products[i].getWeightedCount());
        // normalize
        for (int i = 0; i<numberOfProducts; i++)
            products[i].setWeightedCount(maxTimeWeightingInitialValue*products[i].getWeightedCount()/maxWeightedCount);

        // read test data
        while ((line = br.readLine()) != null && linesRead < (trainDataLines+testDataLines)) {
            // process the line.
            List<String> items = Arrays.asList(line.split("\\s*,\\s*"));
            testCustomers.add(new TestCase(Integer.parseInt(items.get(0)),Integer.parseInt(items.get(1))));
            linesRead++;
        }

        br.close();
        System.out.println("Read " + linesRead + " lines");
    }

    public ArrayList<TestCase> getTestCustomers() {
        return testCustomers;
    }

    public ArrayList<TestCase> getRealTestCustomersFromFile () throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(dataPath+testFile));
        String line;
        int linesRead = 0;
        ArrayList<TestCase> realTestCustomers = new ArrayList<TestCase>();

        // read test data
        while ((line = br.readLine()) != null) {
            // process the line.
            int customerId = Integer.parseInt(line);
            realTestCustomers.add(new TestCase(customerId,-1));
            linesRead++;;
        }

        br.close();
        System.out.println("Read " + linesRead + " lines from test.csv");
        return realTestCustomers;
    }

    // returns copy of product list
    public ArrayList<Product> getProducts(){
        ArrayList<Product> productList = new ArrayList<Product>();
        for (int i = 0; i<numberOfProducts; i++){
            productList.add(i,new Product(products[i]));
        }
        return productList;
    }

    public int getNumberOfProducts (){
        return numberOfProducts;
    }

    public int getNumberOfCustomers(){
        return numberOfProducts;
    }

    public Customer[] getCustomers(){
        return customers;
    }
}
