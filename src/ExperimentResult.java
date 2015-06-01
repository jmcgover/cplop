import java.io.*;
import java.util.*;

public class ExperimentResult implements Serializable{
   private int k;
   private double alpha;
   HashMap<String, SpeciesResult> speciesResults;

   public ExperimentResult(int k, double alpha, Phylogeny tree) {
      this.k = k;
      this.alpha = alpha;
      this.speciesResults = new HashMap<String, SpeciesResult>();
      for (Species s : tree.getAllSpecies().values()) {
         this.speciesResults.put(s.getCommonName(), new SpeciesResult(k, alpha, s, tree));
      }
   }
   public int addClassification(Species s, Species c) {
      if (s == null) {
         return 0;
      }
      return this.speciesResults.get(s.getCommonName()).addClassification(c);
   }

   public int getK() {
      return this.k;
   }
   public double getAlpha() {
      return this.alpha;
   }
   public HashMap<String, SpeciesResult> getSpeciesResults() {
      return this.speciesResults;
   }
}
