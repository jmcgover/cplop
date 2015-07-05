import java.util.*;

public class Setwise extends Classifier<Isolate, Phylogeny, Species> {
   ArrayList<ArrayList<ListEntry<Isolate, Double>>> neighborsLists;
   public Setwise(Isolate unknown, Phylogeny library) {
      super(unknown, library);
      this.neighborsLists = compare(unknown, library);
   }

   public ArrayList<ArrayList<ListEntry<Isolate, Double>>> compare(Isolate unknown, Phylogeny library) {
      Collection<Isolate>                                allIsolates;
      Similarities<Isolate, Double>                      similarities;
      ArrayList<ArrayList<ListEntry<Isolate, Double>>>   neighborsLists;
      ArrayList<ListEntry<Isolate, Double>>              neighbors;
      Double                                             result;

      allIsolates    = library.getAllIsolates().values();
      similarities   = new PearsonIsolate();
      neighborsLists = new ArrayList<ArrayList<ListEntry<Isolate, Double>>>();

      
      for (SimilarityMetric<Isolate, Double> sim : similarities.getSimilarities()) {
         neighbors = new ArrayList<ListEntry<Isolate, Double>>(allIsolates.size());
         for (Isolate i : library.getAllIsolates().values()) {
            result = sim.similarity(unknown, i);
            if (result == null) {
               throw new IllegalStateException( String.format("%s or %s is missing %s", unknown, i, sim));
            }

            /*Add to Neighbors*/
            neighbors.add(new ListEntry<Isolate, Double>(i, result, -1));
         }
         /* Sort */
         Collections.sort(neighbors);

         /*Mark Position*/
         int i = 0;
         for (ListEntry<Isolate, Double> n : neighbors) {
            n.setPosition(i++);
         }
         if (!neighbors.get(0).getData().equals(unknown)) {
            throw new IllegalStateException(
                  String.format("The unknown (%s) is not the zeroth element (%s)", unknown, neighbors.get(0)));
         }

         /*Add to all neighbors.*/
         neighborsLists.add(neighbors);
      }

      return neighborsLists;
   }

   public Species classify(Integer k, Double alpha){
      ArrayList<ListEntry<Isolate, Double>> nearest;
      SetCounter<Species, Isolate, Double> counter;
      ArrayList<Count<Species>> counts = new ArrayList<Count<Species>>();
      Count<Species> result;
      Set<ListEntry<Isolate, Double>> allNearest = new HashSet<ListEntry<Isolate, Double>>(2*k+1);
      int meh = 0;

      for (ArrayList<ListEntry<Isolate, Double>> neighbors : this.neighborsLists) {
         nearest = new ArrayList<ListEntry<Isolate, Double>>();
         for (int i = 1; i <= k && neighbors.get(i) != null && neighbors.get(i).getValue() > alpha; i++) {
            nearest.add(neighbors.get(i));
         }
         allNearest.addAll(nearest);
      }
      counter = new SetCounter<Species, Isolate, Double>();
      result = counter.findMostPlural(allNearest);

      if (result != null) {
         return result.getData();
      }
      return null;
   }
}
