import java.util.*;

public class Pyroprint extends Isolate{
   private static final int DISP_16_23 = 95;
   private static final int DISP_23_5 = 93;
   private String pyroId;
   private String appliedRegion;
   private boolean isErroneous;
   private int dispensations;
   private double[] pHeights;

   // CONSTRUCTORS
   public Pyroprint(
            String commonName, String hostId, String isoId, 
            String pyroId, boolean isErroneous, String appliedRegion, double[] pHeights
         ){
      super(commonName, hostId, isoId);
      this.pyroId = pyroId;
      this.isErroneous = isErroneous;

      this.appliedRegion = appliedRegion;
      if (appliedRegion.equals("23-5")) {
         this.dispensations = DISP_23_5;
      }
      else if (appliedRegion.equals("16-23")) {
         this.dispensations = DISP_16_23;
      }
      else {
         this.dispensations = -1;
      }
      this.pHeights = new double[pHeights.length];
      System.arraycopy(pHeights, 0, this.pHeights, 0, pHeights.length);
   }

   // GETS
   public String getPyroId(){
      return this.pyroId;
   }
   public boolean isErroneous(){
      return this.isErroneous;
   }
   public String getAppliedRegion(){
      return this.appliedRegion;
   }
   public double[] getPHeights(){
      return this.pHeights;
   }
   public int getDispensations(){
      return this.dispensations;
   }

   // Comparisons
   public boolean isSameAppliedRegion(Pyroprint other){
      return this.appliedRegion.equals(other.appliedRegion);
   }

   // Key
   public String isolateKey(){
      return super.key();
   }
   public String key(){
      return String.format("%s,%s",super.key(),pyroId);
   }

   // Object method overwriting
   public int hashCode(){
      return key().hashCode();
   }
   public String toString(){
      return this.pyroId + (this.isErroneous ? "(BAD)" : "");
   }
   public boolean equals(Object other){
      return super.equals(other) && this.pyroId.equals(((Pyroprint)other).pyroId);
   }
}
