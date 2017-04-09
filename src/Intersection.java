import java.util.*;
import java.io.*;

public class Intersection extends Classifier<Isolate, Phylogeny, Species> {
   ArrayList<ArrayList<ListEntry<Isolate, Double>>> neighborsLists;
   public Intersection(Isolate unknown, Phylogeny library) {
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
//         if (!neighbors.get(0).getData().equals(unknown)) {
//            throw new IllegalStateException(
//                  String.format("The unknown (%s) is not the zeroth element (%s)", unknown, neighbors.get(0)));
//         }

         /*Add to all neighbors.*/
         neighborsLists.add(neighbors);
      }

      return neighborsLists;
   }

   public Species classify(Integer k, Double alpha){
      HashSet<ListEntry<Isolate, Double>>             nearest;
      ArrayList<HashSet<ListEntry<Isolate, Double>>>  nearestLists;
      ArrayList<Integer>                              prevListSizes;
      HashSet<ListEntry<Isolate, Double>>             intersection;
      SetCounter<Species, Isolate, Double>            counter;
      Count<Species>                                  result;
      boolean keepGoing = true;
      boolean isInAll;
      int delta = (int)Math.ceil(k / 2);
      int neededSize  = k;

      prevListSizes        = new ArrayList<Integer>(this.neighborsLists.size());
      intersection     = new HashSet<ListEntry<Isolate, Double>>();

      for (int i = 0; i < this.neighborsLists.size(); i++) {
         prevListSizes.add(0);
      }


      while (intersection.size() < neededSize && keepGoing) {
         nearestLists = new ArrayList<HashSet<ListEntry<Isolate, Double>>>();
         for (ArrayList<ListEntry<Isolate, Double>> neighbors : this.neighborsLists) {
            nearest = new HashSet<ListEntry<Isolate, Double>>();
            for (int i = 1; i <= k && neighbors.get(i) != null && neighbors.get(i).getValue() > alpha; i++) {
               nearest.add(neighbors.get(i));
            }
            nearestLists.add(nearest);
         }

         for (ListEntry<Isolate, Double> entry : nearestLists.get(0)) {
            isInAll = true;
            for (HashSet<ListEntry<Isolate, Double>> n : nearestLists) {
               isInAll &= n.contains(entry);
            }
            if (intersection.size() < neededSize && isInAll) {
               intersection.add(entry);
            }
         }
         keepGoing = false;
         for (int i = 0; i < nearestLists.size(); i++) {
            keepGoing |= nearestLists.get(i).size() > prevListSizes.get(i);
            prevListSizes.set(i, nearestLists.get(i).size());
         }
         k += delta;
      }

      result = null;
      if (intersection.size() == neededSize) {
//         System.out.printf(
//         "(%4d)[%1.3f]{%4d} ", 
//         k, alpha, this.neighborsLists.get(0).size());
         counter = new SetCounter<Species, Isolate, Double>();
         result = counter.findMostPlural(intersection);
      }
      else {
//         System.out.printf("
//         (%4d)[%1.3f]{%4d}|%4d| ", 
//         k, alpha, this.neighborsLists.get(0).size(), intersection.size());
      }

      // Print the relevant list
      PrintStream out = System.err;
      try {
          out = new PrintStream(String.format(
                      "results/intersection/avila_intersection_%d_%.3f_%s.csv", neededSize, alpha, unknown));
      } catch (Exception e) {
          throw new RuntimeException(e);
      }
      List<ListEntry<Isolate, Double>> sortedList =
          new ArrayList<ListEntry<Isolate, Double>>(intersection);
      Collections.sort(sortedList);
      out.printf("k,alpha,unknownId,classification,pearson,isoId,species\n");
      for (ListEntry<Isolate, Double> entry : sortedList) {
          out.printf("%d,%.3f,%s,%s,%.3f,%s,%s\n",
                  k, alpha, unknown, result, entry.getValue(), entry.getData(), entry.getData().getCommonName());
      }

      if (result != null) {
         return result.getData();
      }
      return null;
   }
   public Double getLowest(Set<ListEntry<Isolate, Double>> set){
      Double highest = Double.MAX_VALUE * -1;
      for (ListEntry<Isolate, Double> entry : set) {
         if (highest < entry.getValue()) {
            highest = entry.getValue();
         }
      }
      return highest;
   }
}
