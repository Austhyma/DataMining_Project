/*
*Framework file
*/

import java.util.*;
import java.io.*;


public class PAM {
  
  
  public int k = 4;
  //Input fields
  private BufferedReader file;
  private String[] attributeNames;
  //Computed/resultant fields
  private ArrayList<Data> data = new ArrayList<Data>();
  private ArrayList<Data> medoids = new ArrayList<Data>();
  
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
  
  public void computeGoodness() {
    calculateWSS();
  }
  
  public PAM (String filename, String[] attributeNames) throws IOException {
    try {
      this.file = new BufferedReader(new FileReader(filename));
    }
    catch (FileNotFoundException e) {
      System.out.println("Can not find file: " + filename);
    }
    this.attributeNames = attributeNames;
    initData();
    kMedoids();
    
  }
  
  
  public void calculateWSS() {
    for (int i = 0; i < clusters.size(); i++) {
      clusters.get(i).calculateWSS();
      eucWSS += clusters.get(i).getEucWSS();
      manWSS += clusters.get(i).getManWSS();
    }  
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
    Data medoid = new Data();
    for (int i = 0; i < k; i++) {
      int randomVal = (int) Math.round(Math.random()*(this.data.size() -1));
      medoid = this.data.get(randomVal);
      this.clusters.add(new PAMCluster(medoid));
      medoids.add(medoid);
      data.remove(medoid);
    }
    boolean done = false;
    int iterations = 0;
    
    while (iterations < 50) {
      cluster();
      medoids = bestMedoid();
      iterations++;
    }
  }

    public void cluster() {
      for (int i = 0; i < this.data.size(); i++) {        
        int closestMedoid = 0;
        double smallest = Double.POSITIVE_INFINITY;
        for (int j = 0; j < this.clusters.size(); j++) {
          double distance = 0;
          
           for (Iterator<String> attribute = this.data.get(i).getAttributes().keySet().iterator(); attribute.hasNext();) {
          String current = attribute.next();
          distance = Math.abs(this.data.get(i).getAttribute(current) - this.clusters.get(j).getMedoid().getAttributes().get(current));
          
          //distance += (this.euclidean) ? Math.pow(manValue, 2) : manValue;          
        }
        if (distance > smallest) {closestMedoid = j; smallest = distance;}
      }
      this.data.get(i).setClosestMedoid(closestMedoid);
    }
    //Adds all points to their nearest cluster
    for (int i = 0; i < this.clusters.size(); i++) {
      ArrayList<Data> newStuff = new ArrayList<Data>();
      for (int j = 0; j < this.data.size(); j++) {
        if (this.data.get(j).getClosestMedoid() == i) newStuff.add(this.data.get(j));
      }
      this.clusters.get(i).setPoints(newStuff);
    }
  }
    
    //TODO
    public void swap(Data point, Data otherPoint) {
      
      //Swap the two points
      Data temp = point;
      point = otherPoint;
      otherPoint = temp;
      //Make sure the points are removed and added to their respective ArrayLists
      //medoids.remove(point);
      //data.add(point);
      //medoids.add(otherPoint);
      //data.remove(otherPoint);
    }
    
    public double computeCost(Data medoid) {
      double totalCost = 0;
      for (int i = 0; i < data.size(); i++) {
        for (Iterator<String> stuff = data.get(i).getAttributes().keySet().iterator() ; stuff.hasNext();) {
          String current = stuff.next();
          totalCost += Math.abs(medoid.getAttribute(current) - data.get(i).getAttribute(current));
          
        }
      }
        return totalCost;
    }
    
      
    public Data lowestCost(HashMap<Double, Data> list){
      double lowest = -9999;
      Data lowestPoint = new Data();
      for (Double key : list.keySet()) {
        if (lowest < key) { 
          lowest = key; 
          lowestPoint = list.get(key);
        }
      }
      return lowestPoint;
    }
            
    //TODO; might need to implement hashmap to have each cost associated with a data point
    public ArrayList<Data> bestMedoid() {
      HashMap<Double, Data> potentialMedoids = new HashMap<Double, Data>();
      double swapCost = 0;
      double currentCost;
      Data bestMedoid = new Data();
      for (int i = 0; i < medoids.size(); i++) {
        currentCost = computeCost(medoids.get(i));
        for (int j = 0; j < data.size(); j++) {    
          swap(medoids.get(i), data.get(j));
          swapCost = computeCost(medoids.get(i));
          if (swapCost < currentCost) {
            currentCost = swapCost;
            //put the list of better costs into a hashmap to be sorted through for the single best cost
            potentialMedoids.put(currentCost, data.get(j));
          }
          else { swap(medoids.get(i), data.get(j)); }
        }
        bestMedoid = lowestCost(potentialMedoids);
        //Add and remove the data points from their respective ArrayLists
        medoids.add(bestMedoid);
        data.remove(bestMedoid);
      }
      return medoids;
    }
  
  //java PAM <filename>
  public static void main(String[] args) throws IOException {
    String[] initAttNames = {"NCD", "AI", "AS(NA)", "BL", "NAC", "AS(NAC)", "CS", "AT", "NA", "ADL", "NAD"};
    String fileName = "Twitter/Absolute_labeling/Twitter-Absolute-Sigma-500.data";
    PAM init = new PAM(fileName, initAttNames);
  }

}