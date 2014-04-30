import java.util.*;
import java.math.*;

public class OpticsData extends Data implements Comparable<OpticsData> {
  
  private Double reachabilityDistance;
  private Double coreDistance;
  private boolean processed = false;
  
  //Default Constructor
  public OpticsData() {
    super();
  }
  
  //Constructor
  public OpticsData(HashMap<String, Double> attributes, boolean buzz) {
    super(attributes, buzz);
  }
  
  //Getters and Setters
  public Double getCoreDistance() {return this.coreDistance;}
  public Double getReachabilityDistance() {return this.reachabilityDistance;}
  public void setReachabilityDistance(double reachabilityDistance) {this.reachabilityDistance = reachabilityDistance;}
  public void process() {this.processed = true;}
  public boolean processed() {return this.processed;}
  
  //Calculates distance between two points
  //TODO: Needs to weight the distances
  public double distance(OpticsData other, boolean euclidean) {
    double distance = 0;
    for (Iterator<String> attribute = this.attributes.keySet().iterator(); attribute.hasNext();) {
      String current = attribute.next();
      double manValue = Math.abs(other.getAttribute(current) - this.attributes.get(current));
      distance += (euclidean) ? Math.pow(manValue, 2) : manValue;
    }
    return (euclidean) ? Math.sqrt(distance) : distance;
  }
  
  //Calculates the core-distance for each point from it's minPointsith neighbor
  public void coreDistance(ArrayList<OpticsData> neighbors, int minPoints, boolean euclidean) {
    this.coreDistance = distance(neighbors.get(minPoints-1), euclidean);
  }
  
  public int compareTo(OpticsData other) {
    return this.reachabilityDistance.compareTo(other.getReachabilityDistance());
  }
}