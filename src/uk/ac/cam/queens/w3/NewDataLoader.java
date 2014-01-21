package uk.ac.cam.queens.w3;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.lang.System.*;

/**
 * Created by Adam on 02/01/14.
 */
public class NewDataLoader {
    private final static String trainFile = "train.csv";
    private final static String testFile = "test.csv";
    private final static String dataPath = "data/";
    // private final static int noRecords = 2224535;
     private final static int noRecords = 31000;
    private final static int maxCustomerId = 337405;
    private final static int maxProductId = 505;
    private List<Order> records = new Vector<Order>();
    private List<Product> products = new ArrayList<Product>();
    private Customer[] customers = null;


    private int[] customerCount = new int[maxCustomerId+1];
    private ArrayList<TestCase> testCustomers = new ArrayList<TestCase>();
    private int trainRows;
    private Date latestTime = new Date();

    public NewDataLoader(int trainRows) throws  IOException
    {
        this.trainRows = trainRows;
        for (int i = 0; i <= maxProductId; ++i)
        {
            products.add(new Product(i));
        }
        System.out.println("Loading data");
        LoadDataFile();
        System.out.println("Sorting the data by date");
        sortByDate(records);
        System.out.println("Initialising test customers");
        initialiseTestCustomers();
        System.out.println("Data loaded");
    }

    public void LoadDataFile() throws IOException
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
        while ((line = reader.readLine()) != null && linesRead < noRecords)
        {
            splitLine = Arrays.asList(line.split("\\s*,\\s*"));

            try {
                date = dataParser.parse(splitLine.get(2));
                date.setTime(date.getTime()-dataParser.parse("2012-04-01 00:00:00.000").getTime());
                if (date.after(latestTime)) latestTime = date;
            } catch (ParseException e){
                err.println("Failed to parse date");
                e.printStackTrace();
                return;
            }

            customer = Integer.parseInt(splitLine.get(0));
            product = Integer.parseInt(splitLine.get(1));

            products.get(product).incrementCount();
            ++customerCount[product]; // pourquoi?

            records.add(new Order(customer, product, date, splitLine.get(3)));

            ++linesRead;
            if (linesRead % 250000 == 0)
                out.println("Read " + linesRead  + " lines");
        }

    }

    // Implement some SQL-like features
    public void sortByDate(List<Order> array) {Collections.sort(array, new Comparator<Order>() {
        @Override
        public int compare(Order o1, Order o2) {
            return (o1.getTransactionTime().getTime() - o2.getTransactionTime().getTime()) > 0 ? 1 : -1;
        }
    });}


    public Customer[] getCustomers()
    {
        if (customers == null)
        {
            customers = new Customer[maxCustomerId+1];

            for (Order order: records.subList(0, trainRows))
            {
                int customer = order.getCustomerId();
                if (customers[customer] == null)
                    customers[customer] = new Customer(customer, new Vector<Order>());

                customers[customer].getOrders().add(order);
                // customers[customer].getOrders().add(new Order(customer,
                //        order.getProductId(), order.getTransactionTime(), order.getCountryCode()));
            }
        }
        return customers;
    }

    public List<Order> getRecords()  {return records;}

    public List<Order>  getTraining() { return records.subList(0, trainRows); }

    public List<Product> getProducts() {        return products;    }


    public static int getNoRecords() {        return noRecords;    }


    public static int getMaxCustomerId() {        return maxCustomerId;    }

    public static int getMaxProductId() {        return maxProductId;    }

    public int getTrainRows() {        return trainRows;    }


    public Date getLatestTime()    {        return latestTime;    }

    public void initialiseTestCustomers()
    {
        for (int j = trainRows; j<noRecords; ++j)
            testCustomers.add(new TestCase(records.get(j).getCustomerId(), records.get(j).getProductId()));
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



}

