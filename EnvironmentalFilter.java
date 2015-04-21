public class EnvironmentalFilter extends FlaggedFilter{
   public EnvironmentalFilter(){
      super();
   }
   public boolean isComparable(Pyroprint a, Pyroprint b){
      return super.isComparable(a,b) && !b.isEnvironmental();
   }
}
