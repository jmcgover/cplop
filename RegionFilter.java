public class RegionFilter implements Filter<Pyroprint>{
   public RegionFilter(){
   }
   public boolean isComparable(Pyroprint a, Pyroprint b){
      return a.isSameAppliedRegion(b);
   }
}

