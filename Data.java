/*
 * File: Data.java
 */

import java.util.*;

public class Data {
  
  protected HashMap<String, Double> attributes;
  protected boolean buzz;
 
  protected int closestMedoid;
  
  public void setClosestMedoid(int closestMedoid) {this.closestMedoid = closestMedoid;}
  public int getClosestMedoid() {return this.closestMedoid;}
  
  public Data(HashMap<String, Double> dimensions, boolean buzz) {
    this.attributes = dimensions;
    this.buzz = buzz;
  }
  
  public Data(HashMap<String, Double> dimensions) {
    this.attributes = dimensions;
  }
  
  public Data() {}
  
  public double getAttribute(String key) {
    return attributes.get(key);
  }
  
  public boolean getBuzz() {return this.buzz;}
  public HashMap<String, Double> getAttributes() {return this.attributes;}
  
  public boolean equals(OpticsData other) {
      for (String attribute : this.attributes.keySet()) {
        if (this.attributes.get(attribute) != other.getAttribute(attribute)) return false;
      }
      if (this.buzz != other.getBuzz()) return false;
      return true;
    }
}