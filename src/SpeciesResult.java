import java.util.*;

public class SpeciesResult {
   private int k;
   private double alpha;
   private String commonName;
   private HashMap<String,Classification> classifications;

   public SpeciesResult(int k, double alpha, Species species, Phylogeny tree) {
      this.k = k;
      this.alpha = alpha;
      this.commonName = species.getCommonName();
      this.classifications = new HashMap<String,Classification>();
      for (Species s : tree.getAllSpecies().values()) {
         this.classifications.put(s.getCommonName(), new Classification(s));
      }
   }
   private class Classification {
      private String commonName;
      private int numClassifications;
      public Classification(Species species) {
         this.commonName = species.getCommonName();
         this.numClassifications = 0;
      }
      public int getNumClassifications() {
         return this.numClassifications;
      }
      public int addClassification() {
         return ++this.numClassifications;
      }
      public int getClassifications() {
         return this.numClassifications;
      }
   }
   public int addClassification(Species c){
      if (c == null) {
         return 0;
      }
      return this.classifications.get(c.getCommonName()).addClassification();
   }
   public HashMap<String, Classification> getClassifications() {
      return this.classifications;
   }
   public int getK() {
      return this.k;
   }
   public double getAlpha() {
      return this.alpha;
   }
}
