import java.util.*;

public class PAMCluster extends Cluster {
  private Data medoid;
  
  public PAMCluster(Data medoid) {
    super();
    this.medoid = medoid;
  }
  
  public Data getMedoid() {return this.medoid;}
}





