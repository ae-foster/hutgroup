package uk.ac.cam.queens.w3.predictors;

import uk.ac.cam.queens.w3.*;
import uk.ac.cam.queens.w3.util.Customer;
import uk.ac.cam.queens.w3.util.Order;
import uk.ac.cam.queens.w3.util.Product;

import java.util.*;

/**
 * Created by Adam on 02/01/14.
 */
public class FlexiPredictor implements PredictionMaker {

private NewDataLoader dataLoader;
private double A = 5.4E-7;
private double B = 0;
private double C = 0;
private Date lastestTime = new Date();
double[] baselineScores;
List<Order> training;
Customer[] customers;
double[][] productIntersection;
double[] columnTotals;

    public FlexiPredictor(NewDataLoader dataLoader)
    {
        this.dataLoader = dataLoader;
        training = dataLoader.getTraining();
        customers = dataLoader.getCustomers();
        pretrain();
    }

    public void pretrain(){

        lastestTime = training.get(dataLoader.getTrainRows()-1).getTransactionTime();

        // Compute intersection matrix
        productIntersection = new double[dataLoader.getMaxProductId()+1][dataLoader.getMaxProductId()+1];
        columnTotals = new double[dataLoader.getMaxProductId()+1];
       for (Customer jim : customers)
        {
            if (jim == null) continue;
            List<Order> jimsOrders = jim.getOrders();
            for (int i = 0; i < jimsOrders.size(); ++i)
                for (int j = i+1; j < jimsOrders.size(); ++j)
                {
                    // Buy i and then j
                    long t = (jimsOrders.get(j).getTransactionTime().getTime() - jimsOrders.get(i).getTransactionTime().getTime())/1000;
                    productIntersection[jimsOrders.get(i).getProductId()][jimsOrders.get(j).getProductId()] += Math.exp(-C * t);

                    columnTotals[jimsOrders.get(j).getProductId()] += Math.exp(-C * t);
                }
        }
    }

    public void train() {

        // Generate a baseline score
        baselineScores = new double[dataLoader.getMaxProductId()+1];
        for (int i =0; i<dataLoader.getTrainRows(); ++i)
        {
            long t = (lastestTime.getTime() - training.get(i).getTransactionTime().getTime())/1000;
            baselineScores[training.get(i).getProductId()] += Math.exp(-A*t); // Might not be appropriate
        }

    }

    @Override
    public ArrayList<Integer> getRecommendations(int customerId) {

        List<Product> products = dataLoader.getProducts();
        for (int i=0; i<products.size(); ++i)
        {
            products.get(i).setWeightedCount(baselineScores[products.get(i).getProductId()]);
        }


        if (customers[customerId] != null){
            Vector<Order> orders = customers[customerId].getOrders();
            for (Order order : orders){
                // for each order, sum intersection
                int productId = order.getProductId();
                long t = (lastestTime.getTime() - order.getTransactionTime().getTime())/1000;
                for (int i = 0; i<productIntersection[productId].length; i++){
                    if (columnTotals[i] != 0 )
                        products.get(i).incrementWeightedCount(Math.exp(-A * t)*B * ((productIntersection[productId][i]/columnTotals[i]) - 1/505));
                }
            }
        }


        Collections.sort(products, new Product.WeightedCountComparator());

        ArrayList<Integer> recommendations = new ArrayList<Integer>(6);
        for (int i = 0; i<6; i++){
            recommendations.add(products.get(i).getProductId());
//            System.out.println("Recommending product " + products.get(i).getProductId()
//                    + " with score " + products.get(i).getWeightedCount());
        }
        return recommendations;
    }
      
    public void resetParameters(double B)
    {
        this.B = B;
    }
}
