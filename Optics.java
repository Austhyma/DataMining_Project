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
    int count = 0;
    ArrayList<OpticsData> processedPoints = new ArrayList<OpticsData>();
    for (OpticsData point : dataset) {
      System.out.println("**************************************Point: " + count++);
      ArrayList<Data> clusterPoints = new ArrayList<Data>();
      if (processedPoints.contains(point)) {continue;}
      ArrayList<OpticsData> neighbors = getNeighbors(point);
      System.out.println("Neighbors: " + neighbors.size());
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
        System.out.println("Size of Cluster: " + clusterPoints.size());
        //Collections.sort(clusterPoints);
        this.clusters.add(new Cluster(clusterPoints));
      }
    }
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
  
  //java Optics <filename> <epsilon> <minPoints> <euclidean>
  public static void main(String[] args) throws IOException {
    String[] initAttNames = {"NCD", "AI", "AS(NA)", "BL", "NAC", "AS(NAC)", "CS", "AT", "NA", "ADL", "NAD"};
    Optics optics = new Optics(args[0], initAttNames, args[1], args[2], args[3]);
    System.out.println(optics.getInfoGain());
  }
}