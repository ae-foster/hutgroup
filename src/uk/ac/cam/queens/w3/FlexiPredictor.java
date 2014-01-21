package uk.ac.cam.queens.w3;

import java.util.*;

/**
 * Created by Adam on 02/01/14.
 */
public class FlexiPredictor implements PredictionMaker {

private NewDataLoader dataLoader;
private double A = 1E-7;
private double B = 0;
private double C = 0;
double[] baselineScores;
List<Order> training;
Customer[] customers;
int[][] productIntersection;

    FlexiPredictor(NewDataLoader dataLoader)
    {
        this.dataLoader = dataLoader;
        training = dataLoader.getTraining();
        customers = dataLoader.getCustomers();
        pretrain();
    }

    public void train(){


        // Compute intersection matrix
        productIntersection = new int[dataLoader.getMaxProductId()+1][dataLoader.getMaxProductId()+1];
       for (Customer jim : customers)
        {
            if (jim == null) continue;
            List<Order> jimsOrders = jim.getOrders();
            for (int i = 0; i < jimsOrders.size(); ++i)
                for (int j = i+1; j < jimsOrders.size(); ++j)
                {
                    // Buy i and then j
                    long t = (jimsOrders.get(j).getTransactionTime().getTime() - jimsOrders.get(i).getTransactionTime().getTime())/1000;
                    productIntersection[jimsOrders.get(i).getProductId()][jimsOrders.get(j).getProductId()] += 1; // Math.exp(-C * t);
                    //productIntersection[jimsOrders.get(j).getProductId()][jimsOrders.get(i).getProductId()] += C;
                }
        }
    }

    public void pretrain() {
        // Generate a baseline score
        baselineScores = new double[dataLoader.getMaxProductId()+1];
        for (int i =0; i<dataLoader.getTrainRows(); ++i)
        {
            long t = (dataLoader.getLatestTime().getTime() - training.get(i).getTransactionTime().getTime())/1000;
            baselineScores[training.get(i).getProductId()] += 1; // Math.exp(-A * t); // Might not be appropriate
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
                for (int i = 0; i<productIntersection[productId].length; i++){
                    products.get(i).incrementWeightedCount(B * productIntersection[productId][i]);
                }
            }
        }

        Collections.sort(products, new Product.WeightedCountComparator());

        ArrayList<Integer> recommendations = new ArrayList<Integer>(6);
        for (int i = 0; i<6; i++){
            recommendations.add(products.get(i).getProductId());
            // System.out.println("Recommending product " + products.get(i).getProductId());
        }
        return recommendations;
    }
      
    public void resetParameters(double B, double C)
    {
        this.B = B;
        this.C = C;
    }
}
