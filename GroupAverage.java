/*
*Framework file
*/

import java.util.*;
import java.io.*;

public class GroupAverage {
  //Input fields
  private BufferedReader file;
  private String[] attributeNames;
  private double[][] distances;
  //Computed/resultant fields
  private ArrayList<Data> dataset = new ArrayList<Data>();
  private ArrayList<Cluster2> clusters = new ArrayList<Cluster2>();
  double manMeasure = 0.0;
  
  public GroupAverage (String filename, String[] attributeNames) throws IOException {
    try {
      this.file = new BufferedReader(new FileReader(filename));
    }
    catch (FileNotFoundException e) {
      System.out.println("Can not find file: " + filename);
    }
    this.attributeNames = attributeNames;
    initData();
  }
  
  
  //add items to HashMap<int, Cluster2>()
  public void initData() throws IOException {
    String line = this.file.readLine();
    int count = 0;
    while (line != null) {
      String[] lineVals = line.split(",");
      HashMap<String, Double> attributes = new HashMap<String, Double>();
      for (int i = 0; i < lineVals.length - 1; i++) {
        attributes.put(this.attributeNames[i], Double.parseDouble(lineVals[i]));
      }
      double buzzVal = Double.parseDouble(lineVals[lineVals.length - 1]);
      this.dataset.add(new Data(attributes, buzzVal == 1.0));
      System.out.println("DataSet Size: "+dataset.size());
      line = file.readLine();
      System.out.println("Line: " + count++);
    }
      for(int j = 0; j<=dataset.size(); j++) {
        Cluster2 c =  new Cluster2(dataset.get(j));
        clusters.add(c);
      }
  }
  
  //take the data and find the lowest distance using the average
  //must normalize distances!!!!!!
  public void calculateDistances(ArrayList<Cluster2> clusters) {
    this.clusters = clusters;
    for(int i = 0; i < clusters.size(); i++) {
      for(int j = 0; j < clusters.size(); j++) {
        distances[i][j] = clusters.get(i).distance(clusters.get(j));
      }
    }
    mergeClusters(clusters, distances);
    calculateDistances(clusters);
  }
  
//find the shortest distance  
//replace the old clusters with the new ones
  public ArrayList<Cluster2> mergeClusters(ArrayList<Cluster2> clist, double[][] distances) {
    double shortestDistance = 0.0;
    int shortI = 0;
    int shortJ = 0;
    for(int i = 0; i < distances.length; i++) {
      for(int j = 0; j <distances[i].length; j++) {
        if(distances[i][j] == 0.0) {
          j++;
        }
        else if(distances[i][j] < shortestDistance) {
          shortestDistance = distances[i][j];
          shortI = i;
          shortJ = j;
        }
        else {
          j++;
        }
      }
      Cluster2 c = clist.get(shortI);
      for(int x = 0; x < clist.get(shortJ).getPoints().size(); x++) {
        c.addPoint(clist.get(shortJ).getPoints().get(x));
      }      
    }
    clist.remove(clist.get(shortJ));
    return clist;
  }
  
  //java GroupAverage <filename>
  public static void main(String[] args) throws IOException {
    String[] initAttNames = {"NCD_0", "NCD_1", "NCD_2", "NCD_3", "NCD_4", "NCD_5", "NCD_6",
                            "AI_0", "AI_1", "AI_2", "AI_3", "AI_4", "AI_5", "AI_6",
                            "AS(NA)_0", "AS(NA)_1", "AS(NA)_2", "AS(NA)_3", "AS(NA)_4", "AS(NA)_5", "AS(NA)_6",
                            "BL_0", "BL_1", "BL_2", "BL_3", "BL_4", "BL_5", "BL_6",
                            "NAC_0", "NAC_1", "NAC_2", "NAC_3", "NAC_4", "NAC_5", "NAC_6",
                            "AS(NAC)_0", "AS(NAC)_1", "AS(NAC)_2", "AS(NAC)_3", "AS(NAC)_4", "AS(NAC)_5", "AS(NAC)_6",
                            "CS_0", "CS_1", "CS_2", "CS_3", "CS_4", "CS_5", "CS_6",
                            "AT_0", "AT_1", "AT_2", "AT_3", "AT_4", "AT_5", "AT_6",
                            "NA_0", "NA_1", "NA_2", "NA_3", "NA_4", "NA_5", "NA_6",
                            "ADL_0", "ADL_1", "ADL_2", "ADL_3", "ADL_4", "ADL_5", "ADL_6",
                            "NAD_0", "NAD_1", "NAD_2", "NAD_3", "NAD_4", "NAD_5", "NAD_6"
                            };
    GroupAverage init = new GroupAverage(args[0], initAttNames);
  }
}