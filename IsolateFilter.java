public class IsolateFilter extends RegionFilter{
   public IsolateFilter(){
      super();
   }
   public boolean isComparable(Pyroprint a, Pyroprint b){
      return super.isComparable(a,b) && !a.getIsoId().equals(b.getIsoId());
   }
}
