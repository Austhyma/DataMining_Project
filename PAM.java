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
  
  public PAM (String filename, String[] attributeNames, int k) throws IOException {
    try {
      this.file = new BufferedReader(new FileReader(filename));
    }
    catch (FileNotFoundException e) {
      System.out.println("Can not find file: " + filename);
    }
    this.attributeNames = attributeNames;
    this.k = k;
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
    System.out.println("Info Gain: " + this.infoGain);
  }           
  
  public void initData() throws IOException {
    String line = this.file.readLine();
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
      this.data.add(new PAMData(attributes, (buzzVal == 1.0)));
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
      boolean valid = false;
      Data medoid = new Data();
      while (!valid) {
      int randomVal = (int) Math.round(Math.random()*(this.data.size() - 1));
      medoid = this.data.get(randomVal);
      valid = !medoids.contains(medoid);
      }
      
      this.clusters.add(new PAMCluster(medoid));
      medoids.add(medoid);
      //data.remove(medoid);
    }
    

    ArrayList<PAMData> newMedoids = new ArrayList<PAMData>();
    System.out.println("=================================================================");
    cluster();
    bestMedoids();
    //setNewMedoids(newMedoids);

    computeGoodness();

    


  
      
  }
  
  public void setNewMedoids(ArrayList<PAMData> medoids) {
    for (int i = 0; i < this.clusters.size(); i++) {
      this.clusters.remove(i);
    }
    for (int j = 0; j < medoids.size(); j++) {
      this.clusters.add(new PAMCluster(medoids.get(j)));
    }
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
    
    public ArrayList<Data> getCluster(Data point) {
      for (int i = 0; i < this.clusters.size(); i++) {
        if (clusters.get(i).contains(point)) {
          return clusters.get(i).getPoints();
        }
      }
      
      return null;
    }

    public boolean closerMedoid(Data point, Data potentialMedoid) {
      for (int i = 0; i < this.medoids.size(); i++) {
        if ( distance(point, medoids.get(i)) < distance(point, potentialMedoid) ) {
          return true;
        }
      }
      return false;
    }
    
    public Data closestMedoid(Data point) {
      double distance = 0;
      double lowest = Double.POSITIVE_INFINITY;
      for (int i = 0; i < this.medoids.size(); i++) {
        distance = distance(point, medoids.get(i));
        if (distance < lowest) {
          lowest = distance;
        }
      }
      for (int i = 0; i < this.medoids.size(); i++) {
        if (distance(point,medoids.get(i)) == lowest) {

          return this.medoids.get(i); 
        }
      }
      return null;
    }
          
     
    
                                         
      
      
      
    
    //TODO: questions:
    // Go through data twice to take our the swap Point then go through all the other points?
    //
    public void bestMedoids() {
      Data swapPoint = new Data();
      Data currentMedoid = new Data();
      Data currentPoint = new Data();
      Data nextClosestMedoid = new Data();
      ArrayList<Data> currentCluster = new ArrayList<Data>();
      int counter = 0;
      for (int i = 0; i < this.medoids.size(); i++) {
        
        currentMedoid = medoids.get(i); //i
        medoids.remove(currentMedoid);
        if (counter == 5) {
          break;
        }
        for (int j = 0; j < this.data.size()-1; j++) {
          swapPoint = data.get(j); //h
          data.remove(swapPoint);
          counter++;
          double totalCost = 0;
          if (counter == 5) {
            break;
          }
          for (int k = 0; k < this.data.size()-1; k++) {
            currentPoint = data.get(k); //j
            currentCluster = getCluster(currentPoint);
            nextClosestMedoid = closestMedoid(currentPoint); //j2
            //case 1
            if (counter == 5) {
              break;
            }
            else {
              if  (currentCluster.contains(currentMedoid) && closerMedoid(currentPoint, swapPoint)) {
                totalCost += (distance(currentPoint, nextClosestMedoid) - distance(currentPoint, currentMedoid));
                //System.out.println("case 1");
              }
              //case 2
              else if (currentCluster.contains(currentMedoid) && !closerMedoid(currentPoint, swapPoint)) {
                totalCost += (distance(currentPoint, swapPoint) - distance(currentPoint, currentMedoid));
                //System.out.println("case 2");
              }
              //case 3
              else if (!currentCluster.contains(currentMedoid) && closerMedoid(currentPoint, swapPoint)) {
                totalCost += 0;
                //System.out.println("case 3");
              }
              //case 4
              else if (!currentCluster.contains(currentMedoid) && !closerMedoid(currentPoint, swapPoint)) {
                totalCost += ( distance(currentPoint, swapPoint) - distance(currentPoint, nextClosestMedoid ) );
                //System.out.println(
                //System.out.println("case 4");
              }
            }
          }
          
          //TODO: IT'S NOT REMOVING THE CLUSTER
          
          System.out.println(totalCost);
          if (totalCost < 0) {
            data.add(currentMedoid);
            data.remove(swapPoint);
            clusters.add(new PAMCluster(swapPoint));
            medoids.add(swapPoint);
            clusters.remove(currentCluster);      
            System.out.println(clusters.size());
            cluster();
          }
        }
      } 
    }
    
   
        
        /*double currentCost = computeCost(medoids.get(i));
        System.out.println("Current cost: " + currentCost);
        int counter = 0;
        PAMData bestMedoid = new PAMData();
        for (int j = 0; j < data.size()-k; j++) {
          double swapCost = 0;
          swapCost = computeCost(data.get(j));
          //System.out.println("Swap " + swapCost);
          //if the cost after swapping is less than current cost
          if (swapCost < currentCost && counter < 3) {
            //the current configuration stays
            currentCost = swapCost;
            counter++;
            bestMedoid = data.get(j);
            System.out.println("Iteration: " + counter + " for medoid " + i);
            System.out.println("Cost " + swapCost);
            System.out.println();
          }
          
          else if (swapCost > currentCost){
            continue;
          }
          else {
            newMedoids.add(bestMedoid);
            data.remove(data.get(j));
            data.add(medoids.get(i));
            double bestCost = computeCost(bestMedoid);
            System.out.println("Best Cost: " + bestCost);
            //System.out.println("Medoid " + i + " cost after = " + computeCost(medoids.get(i)));
            System.out.println("=================================================================");
            System.out.println();
            break;
          }
        }
      }
      System.out.println(data.size());
      return newMedoids;
      
    }*/
    
    
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
    String fileName1 = "Twitter/Absolute_labeling/Twitter-Absolute-Sigma-500.data";
    String fileName2 = "Twitter/Relative_labeling/sigma=500/Twitter-Relative-Sigma-500.data";
    String fileName3 = "Twitter/Relative_labeling/sigma=1000/Twitter-Relative-Sigma-1000.data";
    String fileName4 = "Twitter/Relative_labeling/sigma=1500/Twitter-Relative-Sigma-1500.data";
    String fileName5 = "Twitter/test1000samples.data.txt";
    
    PAM clusterPam = new PAM(fileName4, initAttNames, 3);
    //PAM clusterPam4 = new PAM(fileName4, initAttNames, 4);
    //PAM clusterPam6 = new PAM(fileName4, initAttNames, 6);
    //ArrayList<PAM> stuff = new ArrayList<PAM>(Arrays.asList(clusterPam));
    //for (int i = 0; i < stuff.size(); i ++) {
      //stuff.get(i).computeGoodness();
    //}
    
    //output(stuff, fileName4);
  }

}