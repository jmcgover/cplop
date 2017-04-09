import java.util.*;
import java.io.*;

public class Winner extends Classifier<Isolate, Phylogeny, Species> {
   ArrayList<ArrayList<ListEntry<Isolate, Double>>> neighborsLists;
   public Winner(Isolate unknown, Phylogeny library) {
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

         /*Add to all neighbors.*/
         neighborsLists.add(neighbors);
      }

      return neighborsLists;
   }

   public Species classify(Integer k, Double alpha){
      ArrayList<ListEntry<Isolate, Double>> nearest;
      ListCounter<Species, Isolate, Double> counter;
      ArrayList<Count<Species>> counts = new ArrayList<Count<Species>>();
      Count<Species> result;
      int meh = 0;

      counter = new ListCounter<Species, Isolate, Double>();

      for (ArrayList<ListEntry<Isolate, Double>> neighbors : this.neighborsLists) {
         nearest = new ArrayList<ListEntry<Isolate, Double>>();
         for (int i = 1; i <= k && neighbors.get(i) != null && neighbors.get(i).getValue() > alpha; i++) {
            nearest.add(neighbors.get(i));
         }
         result = counter.findMostPlural(nearest);
         if (result != null) {
            counts.add(result);
         }
      }

      result = null;
      if (counts.size() > 0) {
         Count<Species> highestCount = counts.get(0);
         for (Count<Species> count : counts) {
            if (count.compareTo(highestCount) > 0) {
               highestCount = count;
            }
         }
         result = highestCount;
      }

      if (result != null) {
          return result.getData();
      }


      return null;
   }
}
