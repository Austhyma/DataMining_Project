import java.util.*;

public class PAMData extends Data implements Comparable<PAMData>{
  
  protected int closestMedoid;
  protected Double cost;
  
  
  //Default Constructor
  public PAMData() {
    super();
  }
  
  //Constructor
  public PAMData(HashMap<String, Double> attributes, boolean buzz) {
    super(attributes, buzz);
  }
  
  public int compareTo(PAMData otherPoint) {
    return this.cost.compareTo(otherPoint.getCost());
  }
  
  public void setCost(double cost) {this.cost = cost;}
  public double getCost() {return this.cost;}
  
  public void setClosestMedoid(int closestMedoid) {this.closestMedoid = closestMedoid;}
  public int getClosestMedoid() {return this.closestMedoid;}
  
  public double distance(PAMTestingData point) {
    double distance = 0;
      for (String attribute : point.getAttributes().keySet()) {
        double manValue = Math.abs(this.getAttribute(attribute) - point.getAttribute(attribute));
        distance += manValue;
      }
    
    return distance;
  }
  
  
}
