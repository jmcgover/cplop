public class IsolateFilter extends EnvironmentalFilter{
   public IsolateFilter(){
      super();
   }
   public boolean isComparable(Pyroprint a, Pyroprint b){
      return super.isComparable(a,b) && !(a.getIsoId().equals(b.getIsoId()));
   }
}
