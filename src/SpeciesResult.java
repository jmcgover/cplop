import java.util.*;
import java.io.*;

public class SpeciesResult implements Serializable, Comparable<SpeciesResult>{
   private int k;
   private double alpha;
   private String commonName;
   private int total;
   private int attempts;
   private int classifiedAs;
   private HashMap<String,Classification> classifications;

   public SpeciesResult(int k, double alpha, Species species, Phylogeny tree) {
      this.k = k;
      this.alpha = alpha;
      this.commonName = species.getCommonName();
      this.classifications = new HashMap<String,Classification>();
      this.total = 0;
      this.attempts = 0;
      this.classifiedAs = 0;
      for (Species s : tree.getAllSpecies().values()) {
         this.classifications.put(s.getCommonName(), new Classification(s));
      }
   }
   public int addClassifiedAs(){
      return ++this.classifiedAs;
   }

   public int addClassification(Species c){
      this.attempts++;
      if (c == null) {
         return 0;
      }
      this.total++;
      return this.classifications.get(c.getCommonName()).addClassification();
   }
   public HashMap<String, Classification> getClassifications() {
      return this.classifications;
   }
   public String getCommonName() {
      return this.commonName;
   }
   public int getNumClassifiedAs() {
      return this.classifiedAs;
   }
   public int getTotal() {
      return this.total;
   }
   public int getAttempts() {
      return this.attempts;
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
   public String toString() {
      return this.commonName;
   }

}
