/*
 * File: Cluster2.java
 */

import java.util.*;

public class Cluster2 extends Cluster {
  
  protected ArrayList<Data> points;
  protected boolean buzz;
  
  //Do I need/want boolean buzz?
  public Cluster2(ArrayList<Data> data /*, boolean buzz*/) {
    this.points = data;
    //this.buzz = buzz;
  }
  
  public Cluster2(Data data /*, boolean buzz*/) {
    this.points.add(data);
    //this.buzz = buzz;
  }
  
  public void removePoint(Data point) {
    this.points.remove(point);
  }
  
  public boolean getBuzz() {return this.buzz;}
  public ArrayList<Data> getPoints() {return this.points;}
  
  public double distance(Cluster2 other) {
    double dist = 0.0;
    for(int i = 0; i <this.points.size(); i++) {
      for(int j = 0; j < other.getPoints().size(); j++) {
        for (Iterator<String> stuff = this.points.get(i).getAttributes().keySet().iterator() ; stuff.hasNext();) {
          String current = stuff.next();
          double manValue = Math.pow(this.points.get(i).getAttributes().get(current) - 
                                     other.getPoints().get(j).getAttributes().get(current), 2);
          dist += manValue;
        }             
      }
    }
    dist = (dist/this.points.size());
    return dist;
  }
}
