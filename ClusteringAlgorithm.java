import java.util.*;

public class ClusteringAlgorithm {
  protected double infoGain;
  protected double weightedEntropy;
  protected ArrayList<OpticsCluster> clusters = new ArrayList<OpticsCluster>();
  protected ArrayList<OpticsData> dataset = new ArrayList<OpticsData>();
  protected double parentEntropy;
  //Prediction Analysis Variables
  protected double recall;
  protected double accuracy;
  protected double precision;
  protected double f1;
  
  public double getInfoGain() {return this.infoGain;}
  public double getRecall() {return this.recall;}
  public double getAccuracy() {return this.accuracy;}
  public double getPrecision() {return this.precision;}
  public double getF1() {return this.f1;}
  
  public void calculateEntropy() {
    System.out.println("Calculating Entropy");
    for (OpticsCluster cluster : this.clusters) {
      cluster.calculateEntropy();
      double numClassLabel = cluster.getPoints().size();
      double value = (numClassLabel/(double) this.dataset.size()) * cluster.getEntropy();
      cluster.setWeightedEntropy(value);
      this.weightedEntropy += value;
    }
    OpticsCluster parents = new OpticsCluster(this.dataset);
    parents.calculateEntropy();
    this.parentEntropy = parents.getEntropy();
    this.infoGain = 1 - this.weightedEntropy;
  } 
}