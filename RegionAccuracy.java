import java.io.*;

public class RegionAccuracy<T> extends IndividualAccuracy<T> implements Serializable{
   private String appliedRegion;
   public RegionAccuracy(T category, String appliedRegion){
      super(category);
      this.appliedRegion = appliedRegion;
   }
   public String getApplieDRegion(){
      return appliedRegion;
   }
   public String toString(){
      return super.toString() + "(" + appliedRegion + ")";
   }
}
