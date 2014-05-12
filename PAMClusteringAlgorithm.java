import java.util.*;

public class PAMClusteringAlgorithm {
  protected double infoGain;
  protected double weightedEntropy;
  protected ArrayList<PAMCluster> clusters = new ArrayList<PAMCluster>();
  protected ArrayList<PAMData> dataset = new ArrayList<PAMData>();
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
  
  /*public void calculateEntropy() {
    System.out.println("Calculating Entropy");
    for (PAMCluster cluster : this.clusters) {
      cluster.calculateEntropy();
      double numClassLabel = cluster.getPoints().size();
      double value = (numClassLabel/(double) this.dataset.size()) * cluster.getEntropy();
      cluster.setWeightedEntropy(value);
      this.weightedEntropy += value;
    }
    PAMCluster parents = new PAMCluster(this.dataset);
    parents.calculateEntropy();
    this.parentEntropy = parents.getEntropy();
    this.infoGain = 1 - this.weightedEntropy;
  } */
}