import java.util.*;
import java.math.*;

public class OpticsData extends Data {
  
  private Double reachabilityDistance;
  private Double coreDistance;
  private boolean processed = false;
  private Double neighborDistance;
  
  //Default Constructor
  public OpticsData() {
    super();
  }
  
  //Constructor
  public OpticsData(HashMap<String, Double> attributes, boolean buzz) {
    super(attributes, buzz);
  }
  
  //Getters and Setters
  public void setNeighborDistance(Double neighborDistance) {this.neighborDistance = neighborDistance;}
  public Double getNeighborDistance() {return this.neighborDistance;}
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
    return distance;
  }
  
  //Calculates the core-distance for each point from it's minPointsith neighbor
  public Double coreDistance(ArrayList<OpticsData> neighbors, int minPoints, boolean euclidean) {
    Double coreDistance;
    if (neighbors.size() < minPoints) {
      return null;
    }
    else {
      coreDistance = distance(neighbors.get(minPoints-1), euclidean);
      this.coreDistance = coreDistance;
    }
    return coreDistance;
  }
}