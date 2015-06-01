import java.io.*;
import java.util.*;

public class NearestNeighbors implements Serializable{
   private static final int FILTER_START = 31;
   private Pyroprint pyroprint;
   private ArrayList<PearsonCorrelation> neighbors;

   public NearestNeighbors(Pyroprint pyroprint, Collection<Pyroprint> library, Filter<Pyroprint> filter){
      if (pyroprint == null) {
         throw new NullPointerException();
      }
      this.pyroprint = pyroprint;
      this.neighbors = sortNeighbors(library, filter);
   }
   public Pyroprint getPyroprint(){
      return this.pyroprint;
   }
   public List<PearsonCorrelation> getNeighbors(){
      return this.neighbors;
   }
   public String toString(){
      return this.pyroprint.key();
   }
   private ArrayList<PearsonCorrelation> sortNeighbors(Collection<Pyroprint> library, Filter<Pyroprint> filter){
      ArrayList<PearsonCorrelation> neighbors = new ArrayList<PearsonCorrelation>();

      for (Pyroprint other : library) {
         if (filter.isComparable(this.pyroprint, other) || other.equals(this.pyroprint)) {
            neighbors.add(new PearsonCorrelation(this.pyroprint, other));
         }
      }
      Collections.sort(neighbors);
//      neighbors.subList(FILTER_START, neighbors.size() - 1).clear();
      return neighbors;
   }
   //Classification
   public Species classifySpecies(int k, double alpha){
      HashMap<String, Thing<Species>> resultsTable = new HashMap<String, Thing<Species>>();
      Species result = null;
      List<PearsonCorrelation> topKAlphaList = getTopKAlpha(k,alpha);
      for (int i = 1; i < topKAlphaList.size(); i++) {
         PearsonCorrelation p = topKAlphaList.get(i);
         result = new Species(p.getOther().getCommonName());
         if (resultsTable.containsKey(result.getCommonName())) {
            resultsTable.get(result.getCommonName()).increment();
         }
         else {
            resultsTable.put(result.getCommonName(), new Thing<Species>(result));
         }
      }
      if (resultsTable.size() > 0) {
         ArrayList<Thing<Species>> sortedResults = new ArrayList<Thing<Species>>(resultsTable.values());
         Collections.sort(sortedResults);
         return sortedResults.get(0).getThing();
      }
      return null;
   }

   public static Species classifySpecies(int k, double alpha, List<PearsonCorrelation> topKAlphaList){
      HashMap<String, Thing<Species>> resultsTable = new HashMap<String, Thing<Species>>();
      Species result = null;
//      List<PearsonCorrelation> topKAlphaList = getTopKAlpha(k,alpha);
      for (int i = 1; i < topKAlphaList.size(); i++) {
         PearsonCorrelation p = topKAlphaList.get(i);
         result = new Species(p.getOther().getCommonName());
         if (resultsTable.containsKey(result.getCommonName())) {
            resultsTable.get(result.getCommonName()).increment();
         }
         else {
            resultsTable.put(result.getCommonName(), new Thing<Species>(result));
         }
      }
      if (resultsTable.size() > 0) {
         ArrayList<Thing<Species>> sortedResults = new ArrayList<Thing<Species>>(resultsTable.values());
         Collections.sort(sortedResults);
         return sortedResults.get(0).getThing();
      }
      return null;
   }
   //Top List
   public List<PearsonCorrelation> getTopKAlpha(int k, double alpha){
      ArrayList<PearsonCorrelation> topKAlphaList = new ArrayList<PearsonCorrelation>();
      int i = 0;
      while (i <= k && i < neighbors.size() && neighbors.get(i).getSimilarity() >= alpha) {
         topKAlphaList.add(neighbors.get(i));
         i++;
      }

//      if (alpha == 1.0) {
//         if (topKAlphaList.size() > 1) {
//         System.err.println("Some Bullshit: ");
//         for (PearsonCorrelation p : topKAlphaList) {
//            System.err.println(p);
//         }
//         }
//      }

      return topKAlphaList;
   }

   //Printing
   public void printTopKAlpha(int k, double alpha, PrintStream stream){
      //Top List
      //Has identical logic but could be gotten rid of.
      int i = 0;
      stream.printf("k: %d alpha: %.3f\n",k,alpha);
      while (i <= k && i < neighbors.size() && neighbors.get(i).getSimilarity() >= alpha) {
         stream.printf("%2d: %s\n",i,neighbors.get(i++));
      }
      stream.printf("Last Checked Neighbor:\n");
      stream.printf("%2d: %s\n",i,neighbors.get(i));

      //Species Classification
      HashMap<String, Thing<Species>> resultsTable = new HashMap<String, Thing<Species>>();
      Species result = null;
      for (PearsonCorrelation p : getTopKAlpha(k,alpha)) {
         result = new Species(p.getOther().getCommonName());
         if (resultsTable.containsKey(result.getCommonName())) {
            resultsTable.get(result.getCommonName()).increment();
         }
         else {
            resultsTable.put(result.getCommonName(), new Thing<Species>(result));
         }
      }
      ArrayList<Thing<Species>> sortedResults = new ArrayList<Thing<Species>>(resultsTable.values());
      Collections.sort(sortedResults);
      i = 0;
      stream.printf("Resulting Species\n");
      for (Thing<Species> t : sortedResults) {
         stream.printf("%2d: %s\n", ++i, t);
      }
   }
}
