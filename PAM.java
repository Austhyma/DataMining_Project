/*
*Framework file
*/

import java.util.*;
import java.io.*;


public class PAM {
  
  
  public int k;
  //Input fields
  private BufferedReader file;
  private String[] attributeNames;
  //Computed/resultant fields
  private ArrayList<Data> data = new ArrayList<Data>();
  ArrayList<Data> medoids = new ArrayList<Data>();
  
  private ArrayList<PAMCluster> clusters = new ArrayList<PAMCluster>();
  private double manWSS = 0;
  private double eucWSS = 0;
  private double infoGain;
  private double manBSS = 0;
  private double eucBSS = 0;
  private boolean euclidean;
  private double weightedEntropy;
  
  //Getters
  public double getEucWSS() {return this.eucWSS;}
  public double getManWSS() {return this.manWSS;}
  public double getEucBSS() {return this.eucBSS;}
  public double getManBSS() {return this.manBSS;}
  public double getEntropy() {return this.infoGain;}
  public ArrayList<PAMCluster> getClusters() {return this.clusters;}
  public boolean getEuclidean() {return this.euclidean;}
  public double getWeightedEntropy() {return this.weightedEntropy;}
  
  public PAM (String filename, String[] attributeNames) throws IOException {
    try {
      this.file = new BufferedReader(new FileReader(filename));
    }
    catch (FileNotFoundException e) {
      System.out.println("Can not find file: " + filename);
    }
    this.attributeNames = attributeNames;
    initData();
  }
  
  
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
      this.data.add(new Data(attributes, (buzzVal == 1.0)));
      line = file.readLine();
      //System.out.println("Line: " + count++);
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
  
  public void kMedoids() {
    
    //select k points as initial medoids
    for (int i = 0; i < k; i++) {
      int randomVal = (int) Math.round(Math.random()*(this.data.size() -1));
      Data medoid = this.data.get(randomVal);
      this.clusters.add(new PAMCluster(medoid));
      medoids.add(medoid);
    }
  }
    
    public void cluster() {
      for (int i = 0; i < this.data.size(); i++) {
        int closestMedoid = 0;
        double smallest = -1;
        for (int j = 0; j < this.clusters.size(); j++) {
          double distance = 0;
           for (Iterator<String> attribute = this.data.get(i).getAttributes().keySet().iterator(); attribute.hasNext();) {
          String current = attribute.next();
          double manValue = Math.abs(this.data.get(i).getAttribute(current) - this.clusters.get(j).getMedoid().getAttributes().get(current));
          distance += (this.euclidean) ? Math.pow(manValue, 2) : manValue;
        }
        if (distance > smallest) {closestMedoid = j; smallest = distance;}
      }
      this.clusters.get(closestMedoid).addPoint(this.data.get(i));
    }
  }
    
    public void swap() {
      for (int i = 0; i < this.medoids.size(); i++) {
        data.remove(i);
      }
    }
      
      
      
        
        
  
  //java PAM <filename>
  public static void main(String[] args) throws IOException {
    String[] initAttNames = {"NCD", "AI", "AS(NA)", "BL", "NAC", "AS(NAC)", "CS", "AT", "NA", "ADL", "NAD"};
    PAM init = new PAM(args[0], initAttNames);
  }
}