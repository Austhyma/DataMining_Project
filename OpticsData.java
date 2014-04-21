import java.util.*;
import java.math.*;

public class OpticsData extends Data {
  
  private double reachabilityDistance = Double.POSITIVE_INFINITY;;
  
  public OpticsData() {
    super();
  }
  
  public OpticsData(HashMap<String, Double> attributes, boolean buzz) {
    super(attributes, buzz);
  }
}