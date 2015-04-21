import java.util.*;

public class SortByPyroprintCount implements Comparator<Species>{
   public int compare(Species a, Species b){
      return -1*(a.getGoodPyroprintCount() - b.getGoodPyroprintCount());
   }
}
