/*
*Framework file
*/

import java.util.*;
import java.io.*;

public class Optics extends ClusteringAlgorithm {
  //Input fields
  private BufferedReader file;
  private String[] attributeNames;
  private double epsilon;
  private int minPoints;
  //Computed/resultant fields
  private boolean euclidean;
  private long time;
  
  public Optics (String filename, String[] attributeNames, String epsilon, String minPoints, String euclidean) throws IOException {
    try {
      this.file = new BufferedReader(new FileReader(filename));
    }
    catch (FileNotFoundException e) {
      System.out.println("Can not find file: " + filename);
    }
    this.attributeNames = attributeNames;
    this.epsilon = Double.parseDouble(epsilon);
    this.minPoints = Integer.parseInt(minPoints);
    this.euclidean = euclidean.equals("true");
    initData();
  }
  
  public void initData() throws IOException {
    System.out.println("Initializing Data");
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
      this.dataset.add(new OpticsData(attributes, (buzzVal == 1.0)));
      line = file.readLine();
      //System.out.println("Line: " + count++);
    }
    computeOptics();
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
  
  //Algorithm
  public void computeOptics() {
    System.out.println("Computing Optics");
    long start = System.currentTimeMillis();
    int count = 0;
    ArrayList<OpticsData> processedPoints = new ArrayList<OpticsData>();
    for (OpticsData point : dataset) {
      ArrayList<OpticsData> clusterPoints = new ArrayList<OpticsData>();
      if (processedPoints.contains(point)) {continue;}
      ArrayList<OpticsData> neighbors = getNeighbors(point);
      processedPoints.add(point);
      ArrayList<OpticsData> seeds = new ArrayList<OpticsData>();
      if (neighbors.size() >= this.minPoints) {
        point.coreDistance(neighbors, this.minPoints, this.euclidean);
        clusterPoints.add(point);
        seeds = update(neighbors, point, seeds, processedPoints);
        for (int i = 0; i < seeds.size(); i++) {
          OpticsData q = seeds.get(i);
          ArrayList<OpticsData> qNeighbors = getNeighbors(q);
          q.process();
          processedPoints.add(q);
          if (qNeighbors.size() >= this.minPoints) {
            q.coreDistance(qNeighbors, this.minPoints, this.euclidean);
            clusterPoints.add(q);
            seeds = update(qNeighbors, q, seeds, processedPoints);
          }
        }
        Collections.sort(clusterPoints);
        this.clusters.add(new OpticsCluster(clusterPoints));
      }
    }
    long elapsedTime = (System.currentTimeMillis() - start)/1000;
    this.time = elapsedTime;
    System.out.println("Time to compute Optics: " + elapsedTime + " seconds");
    calculateEntropy();
  }
  
  public ArrayList<OpticsData> getNeighbors(OpticsData point) {
    ArrayList<OpticsData> neighbors = new ArrayList<OpticsData>();
    for (OpticsData data : dataset) {
      double distance = point.distance(data, euclidean);
      if (distance <= this.epsilon) {
        neighbors.add(data);
        data.setReachabilityDistance(distance);
      }
    }
    Collections.sort(neighbors);
    return neighbors;
  }
  
  public ArrayList<OpticsData> update(ArrayList<OpticsData> neighbors, OpticsData point, ArrayList<OpticsData> seeds, ArrayList<OpticsData> processedPoints) {
    double coreDistance = point.getCoreDistance();
    for (OpticsData neighbor : neighbors) {
      if (!processedPoints.contains(neighbor)) {
        double reachabilityDistance = Math.max(coreDistance, neighbor.getReachabilityDistance());
        if (!seeds.contains(neighbor)) {
          neighbor.setReachabilityDistance(reachabilityDistance);
          seeds.add(neighbor);
        }
        else {
          if (reachabilityDistance < neighbor.getReachabilityDistance()) {
            neighbor.setReachabilityDistance(reachabilityDistance);
          }
        }
      }
    }
    Collections.sort(seeds);
    return seeds;
  }
  
  public void output(String filename) throws IOException {
    PrintWriter output = new PrintWriter(new FileWriter(filename + "-results.csv"));
    output.println("Size of Dataset, " + this.dataset.size());
    output.println("Epsilon, " + this.epsilon);
    output.println("MinPoints, " + this.minPoints);
    output.println("InfoGain, " + this.infoGain);
    output.println("Number of Clusters, " + this.clusters.size());
    String metric = (euclidean) ? "Euclidean" : "Manhattan";
    output.println("Distance Metric, " + metric);
    double totalBuzz = 0;
    double totalNonBuzz = 0;
    for (OpticsData point : this.dataset) {
      if (point.getBuzz()) totalBuzz+=1;
      else totalNonBuzz+=1;
    }
    output.println("Number of Buzz, " + totalBuzz);
    totalBuzz /= this.dataset.size();
    output.println("Percent Buzz, " + totalBuzz);
    output.println("Number of NonBuzz, " + totalNonBuzz);
    totalNonBuzz /= this.dataset.size();
    output.println("Percent NonBuzz, " + totalNonBuzz);
    output.println("Parent Entropy, " + this.parentEntropy);
    output.println("Weighted Entropy, " + this.weightedEntropy);
    output.println("Elapsed Time, " + this.time);
    output.println("List of Clusters, Entropy, Average Core Distance, Size of Cluster");
    int count = 0;
    for (OpticsCluster cluster : this.clusters) {
      String line = count + ", ";
      count++;
      line += cluster.getEntropy() + ", ";
      double total = 0;
      for (OpticsData point : cluster.getPoints()) {
        total += point.getCoreDistance();
      }
      total /= (double) cluster.size();
      line += total + ", ";
      line += cluster.size();
      output.println(line);
    }
    output.close();
    outputClusters(filename);
  }
  
  public void outputClusters(String filename) throws IOException {
    PrintWriter output = new PrintWriter(new FileWriter(filename + "-clusters.csv"));
    String reachabilityList = "";
    for (OpticsCluster cluster : this.clusters) {
      for (OpticsData point : cluster.getPoints()) {
        reachabilityList += point.getReachabilityDistance() + ", ";
      }
    }
    output.println("Reachability, All");
    output.println(reachabilityList);
    output.println("Reachability, Individual");
    for (OpticsCluster cluster : this.clusters) {
      String line = "";
      for (OpticsData point : cluster.getPoints()) {
        line += point.getReachabilityDistance() + ", ";
      }
      output.println(line);
    }
    output.println("Core, Individual");
    for (OpticsCluster cluster : this.clusters) {
      String line = "";
      for (OpticsData point : cluster.getPoints()) {
        line += point.getReachabilityDistance() + ", ";
      }
      output.println(line);
    }
  }
  
  //java Optics <filename> <epsilon> <minPoints> <euclidean>
  public static void main(String[] args) throws IOException {
    String[] initAttNames = {"NCD", "AI", "AS(NA)", "BL", "NAC", "AS(NAC)", "CS", "AT", "NA", "ADL", "NAD"};
    Optics optics = new Optics(args[0], initAttNames, args[1], args[2], args[3]);
    optics.output(args[0]);
  }
}