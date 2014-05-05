import java.util.*;

public class ClusteringAlgorithm {
  protected double infoGain;
  protected double weightedEntropy;
  protected ArrayList<Cluster> clusters = new ArrayList<Cluster>();
  protected ArrayList<OpticsData> dataset = new ArrayList<OpticsData>();
  
  public double getInfoGain() {return this.infoGain;}
  
  public void calculateEntropy() {
    for (Cluster cluster : this.clusters) {
      cluster.calculateEntropy();
      double numClassLabel = cluster.getPoints().size();
      double value = (numClassLabel/(double) this.dataset.size()) * cluster.getEntropy();
      cluster.setWeightedEntropy(value);
      System.out.println("Size: " + cluster.getPoints().size());
      System.out.println("Amount NonBuzz: " + cluster.classCount(false));
      this.weightedEntropy += value;
    }
    this.infoGain = 1 - this.weightedEntropy;
  }     
  
  
}