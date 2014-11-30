public class RegionFilter implements Filter<Pyroprint>{
   public RegionFilter(){
      super();
   }
   public boolean isComparable(Pyroprint a, Pyroprint b){
      return a.getAppliedRegion().equals(b.getAppliedRegion());
   }
}
