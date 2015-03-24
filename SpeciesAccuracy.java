import java.io.*;

public class SpeciesAccuracy implements Serializable{
   private Species species;
   private RegionAccuracy<Species> acc1623;
   private RegionAccuracy<Species> acc235;
   public SpeciesAccuracy(Species species){
      this.species = species;
      this.acc1623 = new RegionAccuracy<Species>(this.species, "16-23");
      this.acc235 = new RegionAccuracy<Species>(this.species, "23-5");
   }
   public Accuracy getProperAccuracy(Pyroprint p){
      if (p.getAppliedRegion().equals("16-23")) {
         return this.acc1623;
      }
      else if (p.getAppliedRegion().equals("23-5")) {
         return this.acc235;
      }
      throw new IllegalStateException("WTF applied region is this? " + p.getAppliedRegion());
   }
   public Species getSpecies(){
      return this.species;
   }
   public Accuracy get1623Accuracy(){
      return this.acc1623;
   }
   public Accuracy get235Accuracy(){
      return this.acc235;
   }
}
