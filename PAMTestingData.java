import java.util.*;

public class PAMTestingData extends Data {
  private boolean prediction;
  
  public PAMTestingData(HashMap<String, Double> attributes, boolean buzz) {
    super(attributes, buzz);
  }
  
  public void setPrediction(boolean prediction) {this.prediction = prediction;}
  public boolean getPrediction() {return this.prediction;}
}