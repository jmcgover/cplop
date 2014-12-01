import java.io.*;
import java.util.*;

public class NearestNeighbors{
   private Pyroprint pyroprint;
   private List<Pyroprint> library;
   private LinkedList<PearsonCorrelation> neighbors;

   public NearestNeighbors(Pyroprint pyroprint, List<Pyroprint> library){
      this.pyroprint = pyroprint;
      this.library = library;
      this.neighbors = sortNeighbors();
   }
   public Pyroprint getPyroprint(){
      return this.pyroprint;
   }
   public List<PearsonCorrelation> getNeighbors(){
      return this.neighbors;
   }
   public List<Pyroprint> getLibrary(){
      return this.library;
   }
   public String toString(){
      return this.pyroprint.key();
   }
   private LinkedList<PearsonCorrelation> sortNeighbors(){
      LinkedList<PearsonCorrelation> neighbors = new LinkedList<PearsonCorrelation>();

      // Hardcoding this filter because it's ALWAYS necessary.
      Filter<Pyroprint> regionFilter = new RegionFilter();
      for (Pyroprint other : library) {
         if (regionFilter.isComparable(this.pyroprint, other)) {
            neighbors.add(new PearsonCorrelation(this.pyroprint, other));
         }
      }
      Collections.sort(neighbors);
      return neighbors;
   }
   public Species classifySpecies(int k, double alpha, Filter<Pyroprint> filter){
      HashMap<Species, Thing<Species>> resultsTable = new HashMap<Species, Thing<Species>>();
      Species result = null;
      for (PearsonCorrelation p : getTopKAlpha(k,alpha,filter)) {
         result = new Species(p.getOther().getCommonName());
         if (resultsTable.containsKey(result)) {
            resultsTable.get(result).increment();
         }
         else {
            resultsTable.put(result, new Thing<Species>(result));
         }
      }
      ArrayList<Thing<Species>> sortedResults = new ArrayList<Thing<Species>>(resultsTable.values());
      Collections.sort(sortedResults);
      return sortedResults.get(0).getThing();
   }
   public List<PearsonCorrelation> getTopKAlpha(int k, double alpha, Filter<Pyroprint> filter){
      ArrayList<PearsonCorrelation> topKAlphaList = new ArrayList<PearsonCorrelation>();
      PearsonCorrelation neighbor = null;
      int neighborNdx = 0;
      int i = 0;
      while (i <= k && neighborNdx < neighbors.size()
            && (neighbor = neighbors.get(neighborNdx)).getSimilarity() >= alpha) {
         if (filter.isComparable(pyroprint, neighbor.getOther()) 
               || pyroprint.equals(neighbor.getOther())) {
            topKAlphaList.add(neighbors.get(neighborNdx));
            i++;
         }
         neighborNdx++;
      }

      return topKAlphaList;
   }

   public void printTopKAlpha(int k, double alpha, 
         Filter<Pyroprint> filter, PrintStream stream){

      PearsonCorrelation neighbor = null;
      int neighborNdx = 0;
      int i = 0;

      stream.printf("k: %d alpha: %.3f\n",k,alpha);
      while (i <= k && neighborNdx < neighbors.size()
            && (neighbor = neighbors.get(neighborNdx)).getSimilarity() >= alpha) {
         if (filter.isComparable(pyroprint, neighbor.getOther()) 
               || pyroprint.equals(neighbor.getOther())) {
            stream.printf("%2d: %s\n",i,neighbors.get(neighborNdx));
            i++;
         }
         else {
            stream.printf("%2d: *%s\n",i,neighbors.get(neighborNdx));
         }
         neighborNdx++;

      }
      stream.printf("Last Checked Neighbor:\n");
      stream.printf("%2d: %s\n",neighborNdx,neighbors.get(neighborNdx));
      HashMap<Species, Thing<Species>> resultsTable = new HashMap<Species, Thing<Species>>();
      Species result = null;
      for (PearsonCorrelation p : getTopKAlpha(k,alpha,filter)) {
         result = new Species(p.getOther().getCommonName());
         if (resultsTable.containsKey(result)) {
            resultsTable.get(result).increment();
         }
         else {
            resultsTable.put(result, new Thing<Species>(result));
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
