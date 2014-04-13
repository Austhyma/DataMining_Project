/*
*Framework file
*/

import java.util.*;
import java.io.*;


public class PAM {
  
  //Input fields
  private BufferedReader file;
  private String[] attributeNames;
  //Computed/resultant fields
  private ArrayList<Data> data;
  private ArrayList<Cluster> clusters = new ArrayList<Cluster>();
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
  public ArrayList<Cluster> getClusters() {return this.clusters;}
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
  
  
  //java PAM <filename>
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
    PAM init = new PAM(args[0], initAttNames);
  }
}