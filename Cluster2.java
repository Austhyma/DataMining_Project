/*
 * File: Cluster2.java
 */

import java.util.*;

public class Cluster2 extends ClusteringAlgorithmGA {
  
  protected ArrayList<Data> points;
  protected Data centroid;
  protected boolean buzz;
  protected double entropy = 0.0;
  protected double weightedEntropy = 0;
  protected double wss = 0.0;
  protected double buzzing = 0.0;
  
  
  //Do I need/want boolean buzz?
  public Cluster2(ArrayList<Data> data) {
    this.points = data;
  }
  
  public Cluster2(Data data) {
    this.points.add(data);
  }
  
  public void setCentroid(Data point) {this.centroid = point;}
  
  public Data getCentroid() {return this.centroid;}
  
  public void removePoint(Data point) {this.points.remove(point);}
  
  public void setWeightedEntropy(double value) {this.weightedEntropy = value;}
  
  public double getWeightedEntropy() {return this.weightedEntropy;}
  
  public boolean getBuzz() {return this.buzz;}
  
  public ArrayList<Data> getPoints() {return this.points;}
  
  public void addAllPoints(ArrayList<Data> p) {this.points.addAll(p);}
  
  public double getEntropy() {return this.entropy;}
  
  public double getWSS() {return this.wss;}
  
  public void calculateWSS() {
    for (int i = 0; i < points.size(); i++) {
      for (Iterator<String> stuff = points.get(i).getAttributes().keySet().iterator() ; stuff.hasNext();) {
        String current = stuff.next();
        double manValue = Math.pow(centroid.getAttribute(current) - points.get(i).getAttribute(current), 2);
        wss += manValue;
      }
    }
  }
  
  public double log(double value, double base) {
    return Math.log(value)/Math.log(base);
  }
  
  public double log2(double value) {
    return Math.log(value)/Math.log(2);
  }
  
   public double distance(Cluster2 other) {
    double dist = 0.0;
    for(int i = 0; i < this.points.size(); i++) {
      for(int j = 0; j < other.getPoints().size(); j++) {
        for (Iterator<String> stuff = this.points.get(i).getAttributes().keySet().iterator() ; stuff.hasNext();) {
          String current = stuff.next();
          double manValue = Math.pow(this.points.get(i).getAttribute(current) -
                                     other.getPoints().get(j).getAttribute(current), 2);
          dist += manValue;
        }             
      }
    }
    return (dist/(double)(this.points.size() * other.getPoints().size()));
  }
   
   public double classCount(boolean buzz) {
    double counter = 0;
    for (int i = 0; i < points.size(); i++) {
      if (points.get(i).getBuzz() == buzz) { counter++;}
    }
    return counter;
  }
   
   public double classCount(boolean buzz, ArrayList<OpticsData> data) {
    double counter = 0; 
    for (int i = 0; i < data.size(); i++) {
      counter += (data.get(i).getBuzz() == buzz) ? 1 : 0;
    }
    return counter;
  }
   
   public void calcEntropy() {
     double buzzProb = classCount(true)/(double) points.size();
     this.buzzing = buzzProb;
     double nonBuzzProb = classCount(false)/(double) points.size();
     System.out.println("nonBuzzProb: "+nonBuzzProb);
     this.entropy -= (buzzProb == 0) ? 0 : entropy(buzzProb);
     this.entropy -= (nonBuzzProb == 0) ? 0 : entropy(nonBuzzProb);
   }
   
   public double entropy(double probability) {
    return probability * log2(probability);
  }
   
   
}
