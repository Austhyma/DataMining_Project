package common;
import java.util.*;

public class Cluster {
  //initial fields
  protected ArrayList<Data> points;
  //computed fields
  protected double eucWSSmeasure= 0;
  protected double manWSSmeasure = 0;
  protected double entropy = 0;
  protected double weightedEntropy = 0;
  
  public Cluster(ArrayList<Data> points) {
    this.points = points;
  }
  
  public Cluster() {
    this.points = new ArrayList<Data>();
  }
  
  //Getters
  public double getEucWSS() {return this.eucWSSmeasure;}
  public ArrayList<Data> getPoints() {return this.points;}
  public double getManWSS() {return this.manWSSmeasure;}
  public double getWeightedEntropy() {return this.weightedEntropy;}
  //Setters
  public void setPoints(ArrayList<Data> cluster) {this.points = cluster;}
  public void setWeightedEntropy(double value) {this.weightedEntropy = value;}
  
  public double getEntropy() {return this.entropy;}
  
  //This will need to be defined per each cluster child-class you can use the former code to figure it out.
  public abstract void calculateWSS() {
//    for (int i = 0; i < points.size(); i++) {
//      for (Iterator<String> stuff = points.get(i).getAttributes().keySet().iterator() ; stuff.hasNext();) {
//        String current = stuff.next();
//        double manValue = Math.pow(centroid.getAttribute(current) - points.get(i).getAttribute(current), 2);
//        eucWSSmeasure += Math.pow(manValue, 2);
//        manWSSmeasure += manValue;
//      }
//    }
  }
  
  public double classCount(boolean buzz) {
    double counter = 0;
    for (int i = 0; i < points.size(); i++) {
      if (points.get(i).getBuzz() == buzz) { counter++;}
    }
    return counter;
  }
  
  public double classCount(boolean buzz, ArrayList<Data> data) {
    double counter = 0; 
    for (int i = 0; i < data.size(); i++) {
      counter += (data.get(i).getBuzz() == buzz) ? 1 : 0;
    }
    return counter;
  }
    
  public void addPoint(Data point) {
    this.points.add(point);
  }
  
  public double log(double value, double base) {
    return Math.log(value)/Math.log(base);
  }
  
  public double log2(double value) {
    return Math.log(value)/Math.log(2);
  }
    
  public void calculateEntropy() {
    double buzzProb = classCount(true)/(double) points.size();
    double nonBuzzProb = classCount(false)/(double) points.size();
    this.entropy += (buzzProb == 0) ? 0 : entropy(buzzProb);
    this.entropy += (nonBuzzProb == 0) ? 0 : entropy(nonBuzzProb);
  }
  
  public double entropy(double probability) {
    return probability * log2(probability);
  }
}





