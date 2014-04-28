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
  public int getK() {return this.k;}
  
  
  public void computeGoodness() {
    calculateWSS();
    calculateBSS();
    calculateEntropy();
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
  
  public void calculateBSS() {
    //Figure out centroid for entire data set
    HashMap<String, Double> dataAttributes = new HashMap<String, Double>();
    for (int i = 0; i < clusters.size(); i++) {
      for (int j = 0; j < clusters.get(i).getPoints().size(); j++) {
        for (Iterator<String> stuff = clusters.get(i).getPoints().get(j).getAttributes().keySet().iterator(); stuff.hasNext();) {         
          String current = stuff.next();
          if (i == 0) {
            dataAttributes.put(current, (clusters.get(i).getMedoid().getAttributes().get(current))/clusters.size());
          }
          else {
            dataAttributes.put(current, dataAttributes.get(current) + (clusters.get(i).getMedoid().getAttributes().get(current))/clusters.size());
          }
        }
      }
    }
    //Calculate distances from dataset centroid
    for (int i = 0; i < clusters.size(); i++) {
      for (int j = 0; j < clusters.get(i).getPoints().size(); j++) {
        for (Iterator<String> stuff = clusters.get(i).getMedoid().getAttributes().keySet().iterator(); stuff.hasNext();) {
          String current = stuff.next();
          double value = Math.pow((clusters.get(i).getMedoid().getAttribute(current) - dataAttributes.get(current) + (clusters.get(i).getMedoid().getAttributes().get(current))/clusters.size()), 2);
          double manValue = value * clusters.get(i).getPoints().size();
          manBSS += manValue;
          eucBSS += Math.pow(manValue, 2);
        }
      }
    }
  }
  
  public void calculateEntropy() {
    for (int i = 0; i < clusters.size(); i++) {
      Cluster current = clusters.get(i);
      current.calculateEntropy();
      double numClassLabel = current.getPoints().size();
      double value = (numClassLabel/this.data.size()) * current.getEntropy();
      current.setWeightedEntropy(value);
      this.weightedEntropy += value;
    }
    this.infoGain = 1 - this.weightedEntropy;
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
    bestMedoid();
    cluster();
  }

    public void cluster() {
    System.out.println("Commence clustering");  
    //Associates points with nearest medoid
    for (int i = 0; i < this.data.size(); i++) {
      int closestMedoid = 0;
      double smallest = Double.POSITIVE_INFINITY;
      for (int j = 0; j < this.clusters.size(); j++) {
        double distance = distance(this.data.get(i), this.clusters.get(j).getMedoid());
        if (distance < smallest) {
          closestMedoid = j; 
          smallest = distance;
        }
      }
      this.data.get(i).setClosestMedoid(closestMedoid);
    }
    System.out.println("Associated points to each cluster, now forming the clusters");
    //Adds all points to their nearest cluster
    for (int i = 0; i < this.clusters.size(); i++) {
      
      ArrayList<Data> newStuff = new ArrayList<Data>();
      for (int j = 0; j < this.data.size(); j++) {
        if (this.data.get(j).getClosestMedoid() == i) newStuff.add(this.data.get(j));
      }
      this.clusters.get(i).setPoints(newStuff);
    }
    System.out.println("Clustering complete");
  }
  
  public double distance(Data current, Data medoid) {
    double distance = 0;
    for (Iterator<String> attribute = current.getAttributes().keySet().iterator(); attribute.hasNext();) {
      String currentAttribute = attribute.next();
      double manValue = Math.abs(current.getAttribute(currentAttribute) - medoid.getAttribute(currentAttribute));
      distance += manValue;
    }
    return distance;
  }
    
    //TODO
    public void swap(Data point, Data otherPoint) {
      
      //Swap the two points
      //Data temp = point;
      //point = otherPoint;
      //otherPoint = temp;
      //Make sure the points are removed and added to their respective ArrayLists
      medoids.remove(point);
      data.add(point);
      medoids.add(otherPoint);
      data.remove(otherPoint);
    }
    
    public double computeCost(Data medoid) {
      double totalCost = 0;
      for (int i = 0; i < data.size() - k; i++) {
        for (Iterator<String> stuff = data.get(i).getAttributes().keySet().iterator() ; stuff.hasNext();) {
          String current = stuff.next();
          totalCost += Math.abs(medoid.getAttribute(current) - data.get(i).getAttribute(current));
        }
      }
      
        return totalCost;
    }
    
    //TODO: something with this method is not working. it is not finding the smallest key value  
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
            
    
    public void bestMedoid() {
      HashMap<Double, Data> potentialMedoids = new HashMap<Double, Data>();
      double swapCost = 0;
      double currentCost;
      System.out.println(swapCost);
      int counter = 0;
      int otherCounter = 0;
      Data bestMedoid = new Data();
      for (int i = 0; i < medoids.size(); i++) {
        currentCost = computeCost(medoids.get(i));
        otherCounter++;
        
        for (int j = 0; j < data.size() - k; j++) {         
          //swap(medoids.get(i), data.get(j));
          swapCost = computeCost(data.get(j));
          if (swapCost < currentCost && counter < 3) {
            currentCost = swapCost;
            counter++;
            System.out.println(counter);
            System.out.println(swapCost);
            //put the list of better costs into a hashmap to be sorted through for the single best cost
            potentialMedoids.put(swapCost, data.get(j));
            System.out.println("PotentialMedoids size: " + potentialMedoids.size());
          }
          else if (swapCost >= currentCost && counter < 3) { continue; }
          else {break;}
        }
        
        //Add and remove the data points from their respective ArrayLists
        bestMedoid = lowestCost(potentialMedoids);
        double bestCost = computeCost(bestMedoid);
        System.out.println("Best Cost: " + bestCost);
        if (medoids.size() < k) {
          medoids.add(bestMedoid);
          data.remove(bestMedoid);
          medoids.remove(medoids.get(i));
          data.add(medoids.get(i));
        }
        
      }
      System.out.println("bestMedoid() complete");
      System.out.println("Size of list of medoids after finding the best: " + medoids.size());
      
    }
    
    public static void output(ArrayList<PAM> current, String filename) throws IOException {
      PrintWriter outFile = new PrintWriter(new FileWriter(filename +  "_results.csv"));
      //Report cohesion and separation using WSS and BSS
      for(int i = 0; i < current.size(); i++) {
        String tableColumns = (current.get(i).getEuclidean()) ? "Euclidean," : "Manhattan,";
        tableColumns += "WSS,BSS,BSS/WSS,InformationGain,WeightedEntropy";
        outFile.println(tableColumns);
        if(current.get(i).getEuclidean()){     
          String line = "k = " + current.get(i).getK() + ", " + current.get(i).getEucWSS() + ", " + current.get(i).getEucBSS() + ", " + current.get(i).getEucBSS()/current.get(i).getEucWSS() + ", " 
            + current.get(i).getEntropy() + ", " + current.get(i).getWeightedEntropy() + ", ";
          outFile.println(line);
        }else {     
          String line = "k = " + current.get(i).getK() + ", " + current.get(i).getManWSS() + ", " + current.get(i).getManBSS() + ", " + current.get(i).getManBSS()/current.get(i).getManWSS() + ", " 
            + current.get(i).getEntropy() + ", " + current.get(i).getWeightedEntropy() + ", ";
          outFile.println(line);
        } 
      }   
      outFile.println(" ");
      
      //Report Entropy per cluster with total weighted entropy
      for(int i = 0; i < current.size(); i++) {
        String clusterColumns = (current.get(i).getEuclidean()) ? "Euclidean," : "Manhattan,";
        clusterColumns += "Entropy, Weighted Entropy";
        outFile.println(clusterColumns);
        if(current.get(i).getEuclidean()){  
          for(int j = 0; j< current.get(i).clusters.size(); j++) {
            String nLine = j + ", " + current.get(i).clusters.get(j).getEntropy() + ", " 
              + current.get(i).clusters.get(j).getWeightedEntropy();
            outFile.println(nLine);
          }
        }else {
          for(int j = 0; j< current.get(i).clusters.size(); j++) {
            String nLine = j + ", " + current.get(i).clusters.get(j).getEntropy() + ", " 
              + current.get(i).clusters.get(j).getWeightedEntropy();
            outFile.println(nLine);
          }
        }
      }
      outFile.close();
    }
  
  //java PAM <filename>
  public static void main(String[] args) throws IOException {
    String[] initAttNames = {"NCD", "AI", "AS(NA)", "BL", "NAC", "AS(NAC)", "CS", "AT", "NA", "ADL", "NAD"};
    String fileName = "Twitter/Absolute_labeling/Twitter-Absolute-Sigma-500.data";
    PAM init = new PAM(fileName, initAttNames);
    
  }

}