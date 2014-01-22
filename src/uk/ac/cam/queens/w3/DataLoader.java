package uk.ac.cam.queens.w3;

import uk.ac.cam.queens.w3.util.Customer;
import uk.ac.cam.queens.w3.util.Order;
import uk.ac.cam.queens.w3.util.Product;
import uk.ac.cam.queens.w3.util.TestCase;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

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
    private Customer[] customers = new Customer[400000]; // ~300k users
    private Product[] products = new Product[1000]; //505 products in dataset
    private int numberOfProducts;
    private int numberOfCustomers;
    private List<Order> records = new Vector<Order>();
    private Date latestTimeInTrainingSet = new Date();

    private ArrayList<TestCase> testCustomers = new ArrayList<TestCase>();

    public DataLoader (int trainRows, int testRows) throws IOException {
        loadDataFile(trainRows+testRows);
        sortByDate(records);
        loadTrainingAndTestData(trainRows);

        System.out.println("Data loaded successfully");
    }

    private void loadTrainingAndTestData(int trainDataLines) throws IOException {

        int maxProductId = 0;
        int maxCustomerId = 0;
        int recordsProcessed = 0;

        HashMap<Integer,TestCase> testCaseHashMap = new HashMap<Integer, TestCase>();

        for (Order order : records) {

            if (recordsProcessed > trainDataLines){

                if (testCaseHashMap.get(order.getCustomerId()) != null){
                    // test customer already loaded
                    testCaseHashMap.get(order.getCustomerId()).getProducts().add(order.getProductId());
                } else {
                    // test customer not loaded, load
                    ArrayList<Integer> list = new ArrayList<Integer>();
                    list.add(order.getProductId());
                    TestCase newTestCase = new TestCase(order.getCustomerId(),list);
                    testCustomers.add(newTestCase);
                    testCaseHashMap.put(newTestCase.getCustomerId(),newTestCase);
                }

            } else {
                maxProductId = Math.max(maxProductId,order.getProductId());
                maxCustomerId = Math.max(maxCustomerId,order.getCustomerId());

                if (customers[order.getCustomerId()] == null){
                    Customer customer = new Customer(order.getCustomerId(),new Vector<Order>());
                    customers[order.getCustomerId()] = customer;
                }

                if (products[order.getProductId()] == null){
                    products[order.getProductId()] = new Product(order.getProductId());
                }

                customers[order.getCustomerId()].getOrders().add(order);
                products[order.getProductId()].incrementCount();

                if (latestTimeInTrainingSet.before(order.getTransactionTime()))
                    latestTimeInTrainingSet = order.getTransactionTime();
            }

            recordsProcessed++;
        }

        numberOfProducts = maxProductId+1;
        numberOfCustomers = maxCustomerId+1;

        // initialise all products
        for (int i = 0; i<numberOfProducts; i++){
            if (products[i] == null){
                products[i] = new Product(i);
            }
        }

        // initialise all customers
        for (int i = 0; i<customers.length; i++){
            if (customers[i] == null){
                customers[i] = new Customer(i,new Vector<Order>());
            }
        }

    }

    public void loadDataFile(int lines) throws IOException
    {
        BufferedReader reader = new BufferedReader(new FileReader(dataPath+trainFile));
        SimpleDateFormat dataParser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String line;
        List<String> splitLine;
        int customer;
        int product;
        Date date;

        // skip first line:
        // Customer_Id,(No column name),Order_Created,Product_Id
        reader.readLine();

        int linesRead = 0;
        while ((line = reader.readLine()) != null && linesRead < lines)
        {
            splitLine = Arrays.asList(line.split("\\s*,\\s*"));

            try {
                date = dataParser.parse(splitLine.get(2));
                date.setTime(date.getTime()-dataParser.parse("2012-04-01 00:00:00.000").getTime());
            } catch (ParseException e){
                System.err.println("Failed to parse date");
                e.printStackTrace();
                return;
            }

            customer = Integer.parseInt(splitLine.get(0));
            product = Integer.parseInt(splitLine.get(1));

            records.add(new Order(customer, product, date, splitLine.get(3)));

            ++linesRead;
            if (linesRead % 250000 == 0)
                System.out.println("Read " + linesRead  + " lines");
        }

    }

    // Implement some SQL-like features
    public void sortByDate(List<Order> array) {
        Collections.sort(array, new Comparator<Order>() {
            @Override
            public int compare(Order o1, Order o2) {
                return (o1.getTransactionTime().getTime() - o2.getTransactionTime().getTime()) > 0 ? 1 : -1;
        }
        });
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
            realTestCustomers.add(new TestCase(customerId,null));
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

    public Date getLatestTimeInTrainingSet (){
        return latestTimeInTrainingSet;
    }
}
