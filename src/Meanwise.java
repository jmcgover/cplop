import java.util.*;

public class Meanwise extends Classifier<Isolate, Phylogeny, Species> {
   ArrayList<ListEntry<Isolate, Double>> neighbors;
   public Meanwise(Isolate unknown, Phylogeny library) {
      super(unknown, library);
      neighbors = compare(unknown, library);
   }

   public ArrayList<ListEntry<Isolate, Double>> compare(Isolate unknown, Phylogeny library) {
      Collection<Isolate>                    allIsolates;
      ArrayList<ListEntry<Isolate, Double>>  neighbors;
      Similarities<Isolate, Double>          similarities;
      ArrayList<Double>                      results;
      Double                                 result;

      allIsolates    = library.getAllIsolates().values();
      neighbors      = new ArrayList<ListEntry<Isolate, Double>>(allIsolates.size());
      similarities   = new PearsonIsolate();

      for (Isolate i : library.getAllIsolates().values()) {

         /*Calculate All Similarities*/
         results = new ArrayList<Double>(similarities.getSimilarities().size());
         for (SimilarityMetric<Isolate, Double> sim : similarities.getSimilarities()) {

            result = sim.similarity(unknown, i);
            if (result == null) {
               result = 0.0;
            }
            results.add(result);
         }

         /*Add to Neighbors*/
         neighbors.add(new ListEntry<Isolate, Double>(i, mean(results), -1));
      }

      /* Sort */
      Collections.sort(neighbors);

         if (!neighbors.get(0).getData().equals(unknown)) {
             neighbors.add(0, new ListEntry<Isolate, Double>(
                         new Isolate("IGNORE SPECIES", "IGNORE HOST", "IGNORE ISOLATE"), 1.0, 0));
//            throw new IllegalStateException(
//                  String.format("The unknown (%s) is not the zeroth element (%s)", unknown, neighbors.get(0)));
         }

      /*Mark Position*/
      int i = 0;
      for (ListEntry<Isolate, Double> n : neighbors) {
         n.setPosition(i++);
      }
      /*We shouldn't ever have a state where the first in the list isn't the
       * unknown, since we're comparing against the whole databse.*/
//      if (!neighbors.get(0).getData().equals(unknown)) {
//         throw new IllegalStateException(
//               String.format("The unknown (%s) is not the zeroth element (%s)", 
//                  unknown, this.neighbors.get(0))
//               );
//      }

      return neighbors;
   }

   public Species classify(Integer k, Double alpha){
      ArrayList<ListEntry<Isolate, Double>> nearest;
      Count<Species> result;
      ListCounter<Species, Isolate, Double> counter;
      double root2 = Math.sqrt(2);

      counter = new ListCounter<Species, Isolate, Double>();

      nearest = new ArrayList<ListEntry<Isolate, Double>>(k + 1);
      result  = null;
      for (int i = 1; i <= k && neighbors.get(i) != null && neighbors.get(i).getValue() > root2 * alpha; i++) {
         nearest.add(neighbors.get(i));
      }

      result = null;
      result = counter.findMostPlural(nearest);
      if (result != null) {
         return result.getData();
      }
      return null;
   }

   public Double mean(List<Double> vals) {
      return arithmeticMean(vals);
   }

   public Double euclideanNorm(List<Double> vals) {
      Double sum = 0.0;
      for (Double val : vals) {
         sum += val*val;
      }
      return Math.sqrt(sum);
   }

   public Double arithmeticMean(List<Double> vals) {
      Double sum = 0.0;
      for (Double val : vals) {
         sum += val;
      }
      return sum / vals.size();
   }
}
