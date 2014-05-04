import java.util.*;


public class PAMCluster extends Cluster {
  protected Data medoid;
  
  public PAMCluster(Data medoid) {
    super();
    this.medoid = medoid;
  }
  
  public PAMCluster() {
    super();
  }
  
  public Data getMedoid() {return this.medoid;}
  public void setMedoid(Data medoid) { this.medoid = medoid;}
  public ArrayList<Data> getPoints() {return this.points;}
  
  public void calculateWSS() {
    for (int i = 0; i < points.size(); i++) {
      for (Iterator<String> stuff = points.get(i).getAttributes().keySet().iterator() ; stuff.hasNext();) {
        String current = stuff.next();
        double manValue = Math.pow(medoid.getAttribute(current) - points.get(i).getAttribute(current), 2);
        eucWSSmeasure += Math.pow(manValue, 2);
        manWSSmeasure += manValue;
      }
    }
  }
  
  
  
  public boolean contains(Data point) {
    for (int i = 0; i < points.size(); i++) {
      if (point == points.get(i)) {
        return true;
      }
    }
    return false;
  }
          
  

      
  
}
  
  







