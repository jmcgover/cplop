import java.util.*;
import java.io.*;

public class SpeciesResult implements Serializable, Comparable<SpeciesResult>{
   private int k;
   private double alpha;
   private String commonName;
   private int totalClassifications;
   private HashMap<String,Classification> classifications;

   public SpeciesResult(int k, double alpha, Species species, Phylogeny tree) {
      this.k = k;
      this.alpha = alpha;
      this.commonName = species.getCommonName();
      this.classifications = new HashMap<String,Classification>();
      this.totalClassifications = 0;
      for (Species s : tree.getAllSpecies().values()) {
         this.classifications.put(s.getCommonName(), new Classification(s));
      }
   }
   public int addClassification(Species c){
      if (c == null) {
         return 0;
      }
      this.totalClassifications++;
      return this.classifications.get(c.getCommonName()).addClassification();
   }
   public HashMap<String, Classification> getClassifications() {
      return this.classifications;
   }
   public String getCommonName() {
      return this.commonName;
   }
   public int getTotalClassifications() {
      return this.totalClassifications;
   }
   public int getK() {
      return this.k;
   }
   public double getAlpha() {
      return this.alpha;
   }
   public int compareTo(SpeciesResult other) {
      return this.commonName.compareTo(other.commonName);
   }
}
