import java.io.*;
import java.util.*;

public class NearestIsolates implements Serializable{
   private static final int FILTER_START = 31;
   private Isolate isolate;
   private ArrayList<IsolateCorrelation> neighbors;

   public NearestIsolates(Isolate isolate, Collection<Isolate> library){
      if (isolate == null) {
         throw new NullPointerException();
      }
      this.isolate = isolate;
      this.neighbors = sortNeighbors(library);
   }
   public Isolate getIsolate(){
      return this.isolate;
   }
   public List<IsolateCorrelation> getNeighbors(){
      return this.neighbors;
   }
   public String toString(){
      return this.isolate.key();
   }
   private ArrayList<IsolateCorrelation> sortNeighbors(Collection<Isolate> library){
      ArrayList<IsolateCorrelation> neighbors = new ArrayList<IsolateCorrelation>();

      for (Isolate other : library) {
         if (!other.isEnvironmental()) {
            IsolateCorrelation ic = new IsolateCorrelation(this.isolate, other);
            if (ic.get1623() != null && ic.get235() != null) {
               neighbors.add(ic);
            }
         }
      }
      Collections.sort(neighbors);
      return neighbors;
   }
   //Classification
   public Species classifySpecies(int k, double alpha){
      HashMap<String, Thing<Species>> resultsTable = new HashMap<String, Thing<Species>>();
      Species result = null;
      List<IsolateCorrelation> topKAlphaList = getTopKAlpha(k,alpha);
      for (int i = 1; i < topKAlphaList.size(); i++) {
         IsolateCorrelation p = topKAlphaList.get(i);
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

   public static Species classifySpecies(int k, double alpha, List<IsolateCorrelation> topKAlphaList){
      HashMap<String, Thing<Species>> resultsTable = new HashMap<String, Thing<Species>>();
      Species result = null;
//      List<IsolateCorrelation> topKAlphaList = getTopKAlpha(k,alpha);
      for (int i = 1; i < topKAlphaList.size(); i++) {
         IsolateCorrelation p = topKAlphaList.get(i);
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
   public List<IsolateCorrelation> getTopKAlpha(int k, double alpha){
      ArrayList<IsolateCorrelation> topKAlphaList = new ArrayList<IsolateCorrelation>();
      int i = 0;
      while (i <= k && i < neighbors.size() && neighbors.get(i).getSimilarity() >= alpha) {
         topKAlphaList.add(neighbors.get(i));
         i++;
      }

//      if (alpha == 1.0) {
//         if (topKAlphaList.size() > 1) {
//         System.err.println("Some Bullshit: ");
//         for (IsolateCorrelation p : topKAlphaList) {
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
      for (IsolateCorrelation p : getTopKAlpha(k,alpha)) {
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
