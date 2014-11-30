import java.util.*;

public class KAlphaNearest{
   private Pyroprint pyroprint;
   private int k;
   private double alpha;
   public KAlphaNearest(Pyroprint pyroprint, int k, double alpha){
      this.pyroprint = pyroprint;
      this.k = k;
      this.alpha = alpha;
   }
   public Species classifySpecies(List<Pyroprint> otherPyroprints, Filter<Pyroprint> filter){
      ArrayList<PearsonCorrelation> nearest = new ArrayList<PearsonCorrelation>();
      for (Pyroprint other : otherPyroprints) {
         if (filter.isComparable(pyroprint,other)) {
            nearest.add(new PearsonCorrelation(this.pyroprint, other));
         }
      }
      Collections.sort(nearest);
      HashMap<Species,Thing<Species>> resultsTable = new HashMap<Species,Thing<Species>>();
      Species result = null;
      for (int i = 0; i < nearest.size() && i <= this.k; i++){
         result = new Species(nearest.get(i).getOther().getCommonName());

         if (nearest.get(i).getOther().getIsoId().equals(this.pyroprint.getIsoId())) {
//            System.err.printf("*%d: %s\n",i,nearest.get(i));
         }
         else {
//            System.err.printf("%d: %s\n",i,nearest.get(i));
         }
         if (resultsTable.containsKey(result)) {
            resultsTable.get(result).increment();
         }
         else {
            resultsTable.put(result, new Thing<Species>(result));
         }
      }
      ArrayList<Thing<Species>> sortedResults = new ArrayList<Thing<Species>>(resultsTable.values());
      Collections.sort(sortedResults);
//      for (Thing<Species> t : sortedResults) {
//         System.err.println(t);
//      }
      return sortedResults.get(0).getThing();
   }
}
