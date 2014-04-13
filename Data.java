/*
 * File: Data.java
 */

import java.util.*;

public class Data {
  
  protected HashMap<String, Double> attributes;
  protected boolean buzz;
  
  public Data(HashMap<String, Double> dimensions, boolean buzz) {
    this.attributes = dimensions;
    this.buzz = buzz;
  }
  
  public boolean getBuzz() {return this.buzz;}
  public HashMap<String, Double> getAttributes() {return this.attributes;}
}