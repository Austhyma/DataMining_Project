import java.util.*;

public class PAMData extends Data {
  
  protected int closestMedoid;
  
  //Default Constructor
  public PAMData() {
    super();
  }
  
  //Constructor
  public PAMData(HashMap<String, Double> attributes, boolean buzz) {
    super(attributes, buzz);
  }
  
  public void setClosestMedoid(int closestMedoid) {this.closestMedoid = closestMedoid;}
  public int getClosestMedoid() {return this.closestMedoid;}
}
