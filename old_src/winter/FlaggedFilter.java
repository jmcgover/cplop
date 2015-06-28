import java.io.*;

public class FlaggedFilter extends RegionFilter{
   public FlaggedFilter(){
   }
   public boolean isComparable(Pyroprint a, Pyroprint b){
      return super.isComparable(a, b) && (a.isErroneous() == false) && (b.isErroneous() == false);
   }
}

