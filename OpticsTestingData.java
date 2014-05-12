import java.util.*;

public class OpticsTestingData extends Data {
  private boolean prediction;
  
  public OpticsTestingData(HashMap<String, Double> attributes, boolean buzz) {
    super(attributes, buzz);
  }
  
  public void setPrediction(boolean prediction) {this.prediction = prediction;}
  public boolean getPrediction() {return this.prediction;}
}