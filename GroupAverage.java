/*
*Framework file
*/

import java.util.*;
import java.io.*;

public class GroupAverage {
  //Input fields
  private BufferedReader file;
  private String[] attributeNames;
  private double[][] masterDistances;
  //Computed/resultant fields
  private ArrayList<Data> dataset = new ArrayList<Data>();
  private ArrayList<Cluster2> clusters = new ArrayList<Cluster2>();
  //Stuff for output
  
  
  public GroupAverage (String filename, String[] attributeNames) throws IOException {
    try {
      this.file = new BufferedReader(new FileReader(filename));
    }
    catch (FileNotFoundException e) {
      System.out.println("Can not find file: " + filename);
    }
    this.attributeNames = attributeNames;
    initData();
    groupAverage();    
    output("groupAverage2Clusters");
  }
  
  public void groupAverage() {
    this.masterDistances = new double[dataset.size()][dataset.size()];
    calculateDistances();
    System.out.println("Clustering...");
    while(this.clusters.size() != 2) {      
      mergeClusters();
    }
    System.out.println("Final Clusters: "+this.clusters.size());
    for(int i = 0; i < this.clusters.size(); i++) {
      System.out.println("Cluster Size: "+this.clusters.get(i).getPoints().size());
      this.clusters.get(i).calculateEntropy();
      System.out.println("Entropy: "+this.clusters.get(i).weightedEntropy);
    }
    for(int x = 0; x < masterDistances.length; x++) {
      for(int y = 0; y < masterDistances.length; y++) {
        System.out.println(masterDistances[x][y]);
      }
    }    
  }
  
  
  //initialize the data
  public void initData() throws IOException {    
    String line = this.file.readLine();
    int count = 0;
    while (line != null) {
      String[] lineVals = line.split(",");
      HashMap<String, Double> attributes = new HashMap<String, Double>();
      for (int i = 0; i < this.attributeNames.length; i++) {
        LinkedList<Double> values = new LinkedList<Double>();
        for (int j = i*7; j < (i*7)+7; j++) {
          values = insertValue(values, Double.parseDouble(lineVals[j]));
        }
        attributes.put(this.attributeNames[i], values.get(values.size()/2));
      }
      double buzzVal = Double.parseDouble(lineVals[lineVals.length - 1]);
      this.dataset.add(new Data(attributes, (buzzVal == 1.0)));
      line = file.readLine();
    }
    for(int j = 0; j < dataset.size(); j++) {
      ArrayList<Data> d = new ArrayList<Data>();
      d.add(dataset.get(j));
      this.clusters.add(new Cluster2(d));
    }
  }
  
  public LinkedList<Double> insertValue(LinkedList<Double> values, double value) {
    LinkedList<Double> a = values;
    if (a.size() == 0) {
      a.add(value);
    }
    else {
      int j = 0;
      int x = 0;
      while ((j < a.size()) && (a.get(j) <= value)) {
        j++;
        x = j;
      }
      a.add(x, value);
    }
    return a;
  }
  
  //take the data and find the lowest distance using the average
  public void calculateDistances() {
    double[][] distances = new double[this.clusters.size()][this.clusters.size()];
    for(int i = 0; i < this.clusters.size(); i++) {
      for(int j = 0; j < this.clusters.size(); j++) {
        if(i == j) {
          distances[i][j] = 0.0;
        }
        else {
        distances[i][j] = this.clusters.get(i).distance(this.clusters.get(j));
        }
      }
    }
    this.masterDistances = distances;
  }
  
//find the shortest distance 
//replace the old clusters with the new ones
  public void mergeClusters() {    
    double newSD = Double.POSITIVE_INFINITY;
    int shortI = 0;
    int shortJ = 0;
    for(int i = 0; i < this.masterDistances.length; i++) {
      for(int j = 0; j < this.masterDistances[i].length; j++) {
        if(i == j) {
          continue;
        }
        else if(this.masterDistances[i][j] < newSD) {
          newSD = this.masterDistances[i][j];
          shortI = i;
          shortJ = j;
        }
      }
    }
    Cluster2 c = this.clusters.get(shortI);
    c.addAllPoints(this.clusters.get(shortJ).getPoints());
    if(shortI > shortJ) {
      this.clusters.remove(shortI);
      this.clusters.remove(shortJ);
    }
    else if(shortJ > shortI) {
      this.clusters.remove(shortJ);
      this.clusters.remove(shortI);
    }
    this.clusters.add(c);
    calculateDistances();
  }
  
  public void output(String filename) throws IOException {
    PrintWriter output = new PrintWriter(new FileWriter(filename + "-results.csv"));
    output.println("Size of Dataset, " + this.dataset.size());
    output.println("Number of Clusters, " + this.clusters.size());
    double totalBuzz = 0;
    double totalNonBuzz = 0;
    for (Data point : this.dataset) {
      if (point.getBuzz()) totalBuzz+=1;
      else totalNonBuzz+=1;
    }
    output.println("Number of Buzz, " + totalBuzz);
    output.println("Number of NonBuzz, " + totalNonBuzz);
    for(int i = 0; i < this.clusters.size(); i++) {
      output.println("Entropy, " + this.clusters.get(i).weightedEntropy);
      output.println("Info Gain , " + this.clusters.get(i).getInfoGain());
    }    
    output.close();
    // wss/bss?
  } 
  
  /*public void updateMatrix() {
    double[][] newMatrix = new double[this.clusters.size()][this.clusters.size()];
    for(int i = 0; i < newMatrix.length; i++) {
      for(int j = 0; j < newMatrix[i].length; j++) {        
        if(i == j) {
          newMatrix[i][j] = 0.0;
        }
        else {
          newMatrix[i][j] = this.clusters.get(i).distance(this.clusters.get(j));
        }
      }
    }
    this.masterDistances = newMatrix;
  }*/
  
  //java GroupAverage <filename>
  public static void main(String[] args) throws IOException {
    String[] initAttNames = {"NCD", "AI", "AS(NA)", "BL", "NAC", "AS(NAC)", "CS", "AT", "NA", "ADL", "NAD"};
    GroupAverage init = new GroupAverage(args[0], initAttNames);
  }
}