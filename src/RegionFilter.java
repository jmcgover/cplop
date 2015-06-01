import java.io.*;

public class RegionFilter implements Filter<Pyroprint>, Serializable{
   public RegionFilter(){
   }
   public boolean isComparable(Pyroprint a, Pyroprint b){
      return a.isSameAppliedRegion(b);
   }
}

