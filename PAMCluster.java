import java.util.*;


public class PAMCluster extends Cluster {
  protected PAMData medoid;
  
  public PAMCluster(PAMData medoid) {
    super();
    this.medoid = medoid;
  }
  
  public PAMCluster() {
    super();
  }
  
  public PAMData getMedoid() {return this.medoid;}
  public void setMedoid(PAMData medoid) { this.medoid = medoid;}
  //public ArrayList<Data> getPoints() {return this.points;}
  
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
  
  public double distance(PAMTestingData point) {
    double distance = 0;
    for (Data clusterPoint : this.points) {
      for (String attribute : clusterPoint.getAttributes().keySet()) {
        double manValue = Math.abs(clusterPoint.getAttribute(attribute) - point.getAttribute(attribute));
        distance += manValue;
      }
    }
    return distance;
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
  
  







