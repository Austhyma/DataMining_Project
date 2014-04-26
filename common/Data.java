/*
 * File: Data.java
 */

package common;
import java.util.*;

public class Data {
  
  protected HashMap<String, Double> attributes;
  protected boolean buzz;
  
  public Data(HashMap<String, Double> dimensions, boolean buzz) {
    this.attributes = dimensions;
    this.buzz = buzz;
  }
  
  public Data() {}
  
  public double getAttribute(String key) {
    return attributes.get(key);
  }
  
  public boolean getBuzz() {return this.buzz;}
  public HashMap<String, Double> getAttributes() {return this.attributes;}
}