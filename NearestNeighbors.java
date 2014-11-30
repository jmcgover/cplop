import java.io.*;
import java.util.*;

public class NearestNeighbors{
   private Pyroprint pyroprint;
   List<Pyroprint> library;
   ArrayList<PearsonCorrelation> neighbors;

   public NearestNeighbors(Pyroprint pyroprint, List<Pyroprint> library){
      this.pyroprint = pyroprint;
      this.library = library;
      this.neighbors = null;
   }
   public List<PearsonCorrelation> sort(){
      for (Pyroprint other : library) {
         neighbors.add(new PearsonCorrelation(this.pyroprint, other));
      }
      Collections.sort(neighbors);
      return neighbors;
   }
   public Species classifySpecies(int k, double alpha, Filter<Pyroprint> filter){
      if (neighbors == null) {
         sort();
      }
      HashMap<Species, Thing<Species>> resultsTable = new HashMap<Species, Thing<Species>>();
      Species result = null;
      return null;
   }
   public void printTopKAlpha(int k, double alpha, 
         Filter<Pyroprint> filter, PrintStream stream){
      PearsonCorrelation neighbor = null;
      int neighborNdx = 0;
      int i = 0;

      stream.printf("k: %d alpha: %.3f\n",k,alpha);
      while (i <= k && neighborNdx < neighbors.size()
            && (neighbor = neighbors.get(neighborNdx)).getSimilarity() >= alpha) {
         if (filter.isComparable(pyroprint, neighbor.getOther())) {
            stream.printf("%2d: %s\n",i,neighbors.get(neighborNdx));
            i++;
         }
         neighborNdx++;

      }
      stream.printf("%2d: alpha %.3f\n",i,alpha);
   }
}
