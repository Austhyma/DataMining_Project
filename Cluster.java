import java.util.*;

public class Cluster {
  private Data centroid;
  private ArrayList<Data> cluster;
  private double eucWSSmeasure= 0;
  private double manWSSmeasure = 0;
  private double entropy = 0;
  private double weightedEntropy = 0;
  
  public Cluster(Data centroid) {
    this.centroid = centroid;
    this.cluster = new ArrayList<Data>();
  }
  
  //Getters
  public double getEucWSS() {return this.eucWSSmeasure;}
  public Data getCentroid() {return this.centroid;}
  public ArrayList<Data> getCluster() {return this.cluster;}
  public double getManWSS() {return this.manWSSmeasure;}
  public double getWeightedEntropy() {return this.weightedEntropy;}
  
  public double getEntropy() {return this.entropy;}
  
  public void calculateWSS() {
    for (int i = 0; i < cluster.size(); i++) {
      for (Iterator<String> stuff = cluster.get(i).getAttributes().keySet().iterator() ; stuff.hasNext();) {
        String current = stuff.next();
        double manValue = Math.pow(centroid.getAttribute(current) - cluster.get(i).getAttribute(current), 2);
        eucWSSmeasure += Math.pow(manValue, 2);
        manWSSmeasure += manValue;
      }
    }
  }
  
  public double classCount(String classLabel) {
    double counter = 0;
    for (int i = 0; i < cluster.size(); i++) {
      if (classLabel.equals(cluster.get(i).getClassLabel())) { counter++;}
    }
    return counter;
  }
  
  public double classCount2(String classLabel, ArrayList<Data> data) {
    double counter = 0; 
    for (int i = 0; i < data.size(); i++) {
      counter = (classLabel.equals(data.get(i).getClassLabel())) ? counter+1 : counter;
    }
    return counter;
  }
 
  //TODO
  public void calculateSplitInfo(ArrayList<String> classLabels, ArrayList<Data> data) {
    for (int i = 0; i < classLabels.size(); i++) {     
      double probability = classCount2(classLabels.get(i), data)/(double) data.size();
      this.weightedEntropy += -(probability * log(probability, classLabels.size()));
    }
  }
    
  public void addPoint(Data point) {
    this.cluster.add(point);
  }
  
  public double log(double value, int base) {
    return Math.log(value)/Math.log(base);
  }
  
  
  
  //TODO
  public void calculateEntropy(ArrayList<String> classLabels) {
    double entropySum = 0;
    for (int i = 0; i < classLabels.size(); i++) {
      double probability = classCount(classLabels.get(i))/(double) cluster.size();
      entropySum += -(probability * log(probability, classLabels.size()));
    }
  }
}





