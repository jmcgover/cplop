import java.util.*;

public class PearsonIsolate implements Similarities<Isolate, Double> {
   private LinkedList<SimilarityMetric<Isolate, Double>> similarities;
   public PearsonIsolate() {
      this.similarities = new LinkedList<SimilarityMetric<Isolate, Double>>();
      this.similarities.add(new PearsonRegion("16-23", 95));
      this.similarities.add(new PearsonRegion("23-5", 93));
   }

   public Collection<SimilarityMetric<Isolate, Double>> getSimilarities() {
      return similarities;
   }
   private class PearsonRegion implements SimilarityMetric<Isolate, Double> {
      private String region;
      private int dispensations;
      public PearsonRegion(String region, int dispensations) {
         this.region = region;
         this.dispensations = dispensations;
      }

      public Double similarity(Isolate a, Isolate b) {
         double repA[];
         double repB[];

         repA = a.getRepresentative(region, dispensations + 1);
         repB = b.getRepresentative(region, dispensations + 1);

         /*Check Null Returns*/
         if (repA == null || repB == null) {
            if (repA == null) {
//               System.out.printf("%s(a) did not have %s representative.\n", a, this.region);
            }
            if (repB == null) {
//               System.out.printf("%s(b) did not have %s representative.\n", b, this.region);
            }
            return null;
         }
         /*Calculate similarity*/
         return Pearson.correlation(repA, repB, dispensations + 1);
      }
   }
}
