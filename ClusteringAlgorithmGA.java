import java.util.*;

public class ClusteringAlgorithmGA {
  protected double infoGain;
  protected double weightedEntropy;
  protected ArrayList<Cluster2> clusters = new ArrayList<Cluster2>();
  protected ArrayList<Data> dataset = new ArrayList<Data>();
  protected double parentEntropy;
  
  public double getInfoGain() {return this.infoGain;}
  
  public void calculateEntropy() {
    for (int i = 0; i < clusters.size(); i++) {
      Cluster2 current = clusters.get(i);
      current.calcEntropy();
      double numClassLabel = current.getPoints().size();
      double value = (numClassLabel/(double) this.dataset.size()) * current.getEntropy();
      current.setWeightedEntropy(value);
      this.weightedEntropy += value;
    }
//    Cluster2 parents = new Cluster2(this.dataset);
//    parents.calculateEntropy();
//    this.parentEntropy = parents.getEntropy();
    this.infoGain = 1 - this.weightedEntropy;
  } 
}