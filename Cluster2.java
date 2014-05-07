/*
 * File: Cluster2.java
 */

import java.util.*;

public class Cluster2 extends ClusteringAlgorithmGA {
  
  protected ArrayList<Data> points;
  protected boolean buzz;
  protected double entropy = 0;
  protected double weightedEntropy = 0;
  
  //Do I need/want boolean buzz?
  public Cluster2(ArrayList<Data> data /*, boolean buzz*/) {
    this.points = data;
    //this.buzz = buzz;
  }
  
  public Cluster2(Data data /*, boolean buzz*/) {
    this.points.add(data);
    //this.buzz = buzz;
  }
  
  public void removePoint(Data point) {this.points.remove(point);}
  
  public void setWeightedEntropy(double value) {this.weightedEntropy = value;}
  
  public double getWeightedEntropy() {return this.weightedEntropy;}
  
  public boolean getBuzz() {return this.buzz;}
  
  public ArrayList<Data> getPoints() {return this.points;}
  
  public void addAllPoints(ArrayList<Data> p) {this.points.addAll(p);}
  
  public double getEntropy() {return this.entropy;}
  
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
}
