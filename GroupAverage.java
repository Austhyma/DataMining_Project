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
  public double bss = 0.0;
  public double wss = 0.0;
  public double weightedEntropy = 0.0;
  public double infoGain = 0.0;
  protected double recall;
  protected double accuracy;
  protected double precision;
  protected double f1;
  //Stuff for output
  
  
  public GroupAverage (String filename, String[] attributeNames, int n) throws IOException {
    try {
      this.file = new BufferedReader(new FileReader(filename));
    }
    catch (FileNotFoundException e) {
      System.out.println("Can not find file: " + filename);
    }
    this.attributeNames = attributeNames;
    initData();
    groupAverage(n);
    output("groupAverage"+n+"Clusters");
  }
  
  public void groupAverage(int n) {
    this.masterDistances = new double[dataset.size()][dataset.size()];
    calculateDistances();
    System.out.println("Clustering...");
    while(this.clusters.size() != n) {
      mergeClusters();
    }
    for(int i = 0; i < this.clusters.size(); i++) {
      computeCentroid();
      this.clusters.get(i).calcEntropy();
      this.clusters.get(i).calculateWSS();
      System.out.println("Buzzing: "+this.clusters.get(i).buzzing);
      System.out.println("Entropy: "+this.clusters.get(i).getEntropy());
    }
    calculateWSS();
    calculateBSS();
    calculateInfoGain();
  }
  
  public double getRecall() {return this.recall;}
  public double getAccuracy() {return this.accuracy;}
  public double getPrecision() {return this.precision;}
  public double getF1() {return this.f1;}
  
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
  
  public static ArrayList<GATestingData> initTestingData(String filename, String[] attNames) throws IOException {
      ArrayList<GATestingData> retVals = new ArrayList<GATestingData>();
      BufferedReader file = null;
      try {
        file = new BufferedReader(new FileReader(filename));
      }
      catch (FileNotFoundException e) {
        System.out.println("Can not find file: " + filename);
      }
      System.out.println("Initializing Data");
      String line = file.readLine();
      int count = 0;
      while (line != null) {
        String[] lineVals = line.split(",");
        HashMap<String, Double> attributes = new HashMap<String, Double>();
        for (int i = 0; i < attNames.length; i++) {
          LinkedList<Double> values = new LinkedList<Double>();
          for (int j = i*7; j < (i*7)+7; j++) {
            values = insertValue(values, Double.parseDouble(lineVals[j]));
          }
          attributes.put(attNames[i], values.get(values.size()/2));
        }
        double buzzVal = Double.parseDouble(lineVals[lineVals.length - 1]);
        retVals.add(new GATestingData(attributes, (buzzVal == 1.0)));
        line = file.readLine();
        //System.out.println("Line: " + count++);
      }
      return retVals;
    }
  
  public static LinkedList<Double> insertValue(LinkedList<Double> values, double value) {
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
      output.println("---------------------------------------------");
      output.println("Cluster "+i+ " size: "+this.clusters.get(i).getPoints().size());
      output.println("Entropy, " + this.clusters.get(i).getEntropy());
      output.println("WSS , " + this.clusters.get(i).getWSS());
    }
    output.println("---------------------------------------------");
    output.println("BSS , " + bss);
    output.println("TotalWSS , " + wss);
    output.println("TotalInfoGain , " + this.infoGain);
    output.println("---------------------------------------------");
    output.println("Precision , " + this.precision);
    output.println("Recall , " + this.recall);
    output.println("F1 , " + this.f1);
    output.println("Accuracy , " + this.accuracy);
    output.close();
  } 
  
  public ArrayList<Data> computeCentroid() {    
    ArrayList<Data> returnValues = new ArrayList<Data>();
    for (int i = 0; i < this.clusters.size(); i++) {
      HashMap<String, Double> dataAttributes = new HashMap<String, Double>();
      for (int j = 0; j < this.clusters.get(i).getPoints().size(); j++) {
        Data data = this.clusters.get(i).getPoints().get(j);
        for (Iterator<String> attribute = clusters.get(i).getPoints().get(j).getAttributes().keySet().iterator(); attribute.hasNext();) {
          String current = attribute.next();
          double total = (double) this.clusters.get(i).getPoints().size();
          double value = (j == 0) ? (data.getAttribute(current)/total) : (dataAttributes.get(current) + data.getAttribute(current)/total);
          dataAttributes.put(current, value);
        }
      }
      if (this.clusters.get(i).getPoints().size() > 0) {this.clusters.get(i).setCentroid(new Data(dataAttributes)); returnValues.add(new Data(dataAttributes));}
      else {returnValues.add(this.clusters.get(i).getCentroid()); this.clusters.get(i).setCentroid(this.clusters.get(i).getCentroid());}
    }
    return returnValues;
  }
  
  public void calculateBSS() {
    HashMap<String, Double> dataAttributes = new HashMap<String, Double>();
    for (int i = 0; i < clusters.size(); i++) {
      for (int j = 0; j < clusters.get(i).getPoints().size(); j++) {
        for (Iterator<String> stuff = clusters.get(i).getPoints().get(j).getAttributes().keySet().iterator(); stuff.hasNext();) {         
          String current = stuff.next();
          if (i == 0) {
            dataAttributes.put(current, (clusters.get(i).getCentroid().getAttributes().get(current))/clusters.size());
          }
          else {
            dataAttributes.put(current, dataAttributes.get(current) + (clusters.get(i).getCentroid().getAttributes().get(current))/clusters.size());
          }
        }
      }
    }
    for (int i = 0; i < clusters.size(); i++) {
      for (int j = 0; j < clusters.get(i).getPoints().size(); j++) {
        for (Iterator<String> stuff = clusters.get(i).getCentroid().getAttributes().keySet().iterator(); stuff.hasNext();) {
          String current = stuff.next();
          double value = Math.pow((clusters.get(i).getCentroid().getAttribute(current) - dataAttributes.get(current) + (clusters.get(i).getCentroid().getAttributes().get(current))/clusters.size()), 2);
          double manValue = value * clusters.get(i).getPoints().size();
          bss += manValue;
        }
      }
    }
  }
  
  public void calculateWSS() {
    for (int i = 0; i < clusters.size(); i++) {
      clusters.get(i).calculateWSS();
      wss += clusters.get(i).getWSS();
    }  
  }
  
  public void calculateInfoGain() {
    for (int i = 0; i < clusters.size(); i++) {
      Cluster2 current = clusters.get(i);
      double numClassLabel = current.getPoints().size();
      double value = (numClassLabel/this.dataset.size()) * current.getEntropy();
      this.weightedEntropy += value;
    }
    this.infoGain = 1.0 - this.weightedEntropy;
  } 
  
  public GATestingData predict(GATestingData point) {
    double smallestDistance = Double.POSITIVE_INFINITY;
    Cluster2 smallest = null;
    for (int i = 0; i < this.clusters.size(); i++) {
      double distance = this.clusters.get(i).distance(point);
      if (distance < smallestDistance) {
        smallestDistance = distance;
        smallest = this.clusters.get(i);
      }
    }
    point.setPrediction(smallest.classCount(true) >= smallest.classCount(false));
    return point;
  }
  
  public void predictionAnalysis(ArrayList<GATestingData> initTestingData) {
    ArrayList<GATestingData> testingData = new ArrayList<GATestingData>();
    for (GATestingData initPoint : initTestingData) {
      testingData.add(predict(initPoint));
    }
    confusionMatrix(testingData);
  }
  
  public void confusionMatrix(ArrayList<GATestingData> testingData) {
    double truePositives = 0;
    double trueNegatives = 0;
    double falsePositives = 0; 
    double falseNegatives = 0;
    for (GATestingData point : testingData) {
      if (point.getBuzz() && point.getPrediction()) truePositives += 1;
      if (!point.getBuzz() && !point.getPrediction()) trueNegatives += 1;
      if (!point.getBuzz() && point.getPrediction()) falsePositives += 1;
      if (point.getBuzz() && !point.getPrediction()) falseNegatives += 1;
    }
    if(truePositives == 0) {
      this.precision = 0;
      this.recall = 0;
      this.f1 = 0;
    }
    else {
      this.precision = truePositives/(truePositives + falsePositives);
      this.recall = truePositives/(truePositives + falseNegatives);
      this.f1 = (2 * this.recall * this.precision)/(this.recall + this.precision);
    }    
    this.accuracy = (truePositives + trueNegatives)/(double) testingData.size();
    System.out.println("truePositives: " + truePositives);
    System.out.println("trueNegatives: " + trueNegatives);
    System.out.println("falsePositives: " + falsePositives);
    System.out.println("falseNegatives: " + falseNegatives);
  }
  
  
  //java GroupAverage <filename>
  public static void main(String[] args) throws IOException {
    String[] initAttNames = {"NCD", "AI", "AS(NA)", "BL", "NAC", "AS(NAC)", "CS", "AT", "NA", "ADL", "NAD"};
    ArrayList<GATestingData> testingData = initTestingData("Twitter/1000samples.test.data", initAttNames);
    
    GroupAverage init = new GroupAverage("GroupAverage/2000samples.data", initAttNames, 2);    
    init.predictionAnalysis(testingData);
    GroupAverage init2 = new GroupAverage("GroupAverage/2000samples.data", initAttNames, 4);
    init2.predictionAnalysis(testingData);
    GroupAverage init3 = new GroupAverage("GroupAverage/2000samples.data", initAttNames, 8);
    init3.predictionAnalysis(testingData);
  }
}