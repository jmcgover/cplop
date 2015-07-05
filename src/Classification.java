import java.util.*;
import java.io.*;

public class Classification implements Serializable, Comparable<Classification>{
   private String commonName;
   private int numClassifications;
   public Classification(Species species) {
      this.commonName = species.getCommonName();
      this.numClassifications = 0;
   }
   public String getCommonName() {
      return this.commonName;
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
   public int compareTo(Classification other) {
      return this.commonName.compareTo(other.commonName);
   }
}
