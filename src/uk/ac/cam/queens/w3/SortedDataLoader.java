package uk.ac.cam.queens.w3;

import uk.ac.cam.queens.w3.util.Product;
import uk.ac.cam.queens.w3.util.TestCase;

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
public class SortedDataLoader {
    private final static String trainFile = "train.csv";
    private final static String testFile = "test.csv";
    private final static String dataPath = "data/";
    private final static int noRecords = 2224535;
    private final static int maxCustomerId = 337405;
    private final static int maxProductId = 505;
    private int[][] records = new int[noRecords][4];
    private ArrayList<Product> products = new ArrayList<Product>();


    private int[] customerCount = new int[maxCustomerId+1];
    private ArrayList<TestCase> testCustomers = new ArrayList<TestCase>();
    private int trainRows;
    private static int noCols = 3;

    // Indices for the record array
    private static int CUSTOMER = 0;
    private static int PRODUCT = 1;
    private static int DATE = 2;
    private int latestTime = 0;

    public SortedDataLoader(int trainRows) throws  IOException
    {
        this.trainRows = trainRows;
        for (int i = 0; i <= maxProductId; ++i)
        {
            products.add(new Product(i));
        }
        System.out.println("Loading data");
        LoadDataFile();
        System.out.println("Sorting the data by date");
        sortBy(records, DATE);
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
        int countryCode;

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
            } catch (ParseException e){
                err.println("Failed to parse date");
                e.printStackTrace();
                return;
            }

            customer = Integer.parseInt(splitLine.get(0));
            product = Integer.parseInt(splitLine.get(1));

            records[linesRead][CUSTOMER]= customer;
            records[linesRead][PRODUCT] = product;
            products.get(product).incrementCount();
            ++customerCount[product];

            // Record time in seconds and a slightly dangerous cast to int
            if (date.getTime()/1000 > Integer.MAX_VALUE)
            {
                out.println("Bad cast - Adam you shouldn't be doing that");
                return;
            }
            records[linesRead][DATE] = (int) (date.getTime()/1000);
            latestTime = records[linesRead][DATE] > latestTime ? records[linesRead][DATE] : latestTime;

            ++linesRead;
            if (linesRead % 500000 == 0)
                out.println("Read " + linesRead  + " lines");
        }

        }

        // Implement some SQL-like features
        public void sortBy(int[][] array, final int column, int start, int end)
        {
            // Valid col index?
            assert (0<= column && column < noCols);

            // Sort records
            Arrays.sort(array, start, end, new Comparator<int[]>() {
                @Override
                public int compare(int[] o1, int[] o2) {
                    return o1[column] - o2[column];
                }
            });

        }

        public void sortBy(int[][] array, final int column)
        {
            sortBy(array, column, 0, array.length);
        }

        public void sortBy(int[][] array, final int column1, final int column2, int start, int end)
        /*
        Sort columns by column1, then sub-sort by column2
         */

        {
            // Valid col indices?
            assert (0<= column1 && column1 < noCols && 0 <= column2 && column2 < noCols);

            // Sort records
            Arrays.sort(array, start, end, new Comparator<int[]>() {
                @Override
                public int compare(int[] o1, int[] o2) {
                    // Compare by column 1 first, and only then by column 2
                    return o1[column1] - o2[column1] == 0? o1[column2] - o2[column2] : o1[column1] - o1[column1];
                }
            });
        }

        public void sortBy(int[][] array, final int column1, final int column2)
        {
            sortBy(array, column1, column2, 0, array.length);
        }

        public int count(int[][] array, int column, int value, int start, int end)

        {
            // Valid col indices?
            assert (0<= column && column < noCols);

            int count = 0;
            for (int c = start; c < end; ++c)
            {
                if (array[c][column] == value)
                    ++count;
            }


            return count;

        }

        public int count(int[][] array, int column1, int value1, int column2, int value2, int start, int end)
        {
            // Valid col indices?
            assert (0<= column1 && column1 < noCols && 0 <= column2 && column2 < noCols);

            int count = 0;
            for (int c = start; c < end; ++c)
            {
                if (array[c][column1] == value1 && array[c][column2] == value2)
                    ++count;
            }


            return count;
        }

        public int[][] select(int[][] array, int column, int value, int start, int end)
    {
        int n = count(array, column, value, start, end);
        int[][] toReturn = new int[n][noCols];
        int insertion = 0;
        for (int i = start; i < end; ++i)
        {
            if (array[i][column] == value)
            {
                System.arraycopy(array[i], 0, toReturn[insertion], 0, noCols);
                insertion++;
            }
        }

        return toReturn;
    }

    public int[][] select(int[][] array, int column1, int value1, int column2, int value2, int start, int end)
    {
        int n = count(array, column1, value1, column2, value2, start, end);
        int[][] toReturn = new int[n][noCols];
        int insertion = 0;
        for (int i = start; i < end; ++i)
        {
            if (array[i][column1] == value1 && array[i][column2] == value2)
            {
                System.arraycopy(array[i], 0, toReturn[insertion], 0, noCols);
                insertion++;
            }
        }

        return toReturn;
    }


        public int[][] getRecords()
        {
            return records;
        }

        public int[][] getRecords(int start, int end)
        {
            return Arrays.copyOfRange(records, start, end);
        }

        public int[][] getTraining()
        {
            sortBy(records, DATE);
            return Arrays.copyOfRange(records, 0, trainRows);
        }


    public static int getNoRecords() {
        return noRecords;
    }


    public static int getMaxCustomerId() {
        return maxCustomerId;
    }

    public static int getMaxProductId() {
        return maxProductId;
    }

    public int getTrainRows() {
        return trainRows;
    }

    public static int getCUSTOMER() {
        return CUSTOMER;
    }

    public static int getPRODUCT() {
        return PRODUCT;
    }

    public static int getDATE() {
        return DATE;
    }

    public int getLatestTime()
    {
        return latestTime;
    }

    public void initialiseTestCustomers()
    {
        for (int j = trainRows; j<noRecords; ++j)
            testCustomers.add(new TestCase(records[j][CUSTOMER], records[j][PRODUCT]));
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


    public ArrayList<Product> getProducts() {
        return products;
    }
}

