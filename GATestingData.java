import java.util.*;

public class GATestingData extends Data {
  private boolean prediction;
  
  public GATestingData(HashMap<String, Double> attributes, boolean buzz) {
    super(attributes, buzz);
  }
  
  public void setPrediction(boolean prediction) {this.prediction = prediction;}
  
  public boolean getPrediction() {return this.prediction;}
}