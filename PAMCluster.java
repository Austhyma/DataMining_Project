import java.util.*;

public class PAMCluster extends Cluster {
  private Data medoid;
  private Double cost;
  
  public PAMCluster(Data medoid) {
    super();
    this.medoid = medoid;
  }
  
  public Data getMedoid() {return this.medoid;}

 
  public double getCost() {return this.cost;}
  public void setCost(double cost) {this.cost = cost;}

  public void computeCost() {
    for (int i = 0; i < points.size(); i++) {
      for (Iterator<String> stuff = points.get(i).getAttributes().keySet().iterator() ; stuff.hasNext();) {
        String current = stuff.next();
        cost += Math.abs(medoid.getAttribute(current) - points.get(i).getAttribute(current));
      }
    }
  }
  
}
  
  







