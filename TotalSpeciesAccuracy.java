import java.io.*;
import java.util.*;

public class TotalSpeciesAccuracy{
   private Species species;
   private Collection<SpeciesAccuracy> accuracies;
   private RegionAccuracy<Species> acc1623;
   private RegionAccuracy<Species> acc235;
   public TotalSpeciesAccuracy(Collection<SpeciesAccuracy> accuracies){
      this.species = new Species("OVERALL");
      this.accuracies = accuracies;
      this.acc1623 = new RegionAccuracy<Species>(this.species, "16-23");
      this.acc235 = new RegionAccuracy<Species>(this.species, "23-5");
      for (SpeciesAccuracy sa : this.accuracies) {
          this.acc1623.addAccuracy(sa.get1623Accuracy());
          this.acc235.addAccuracy(sa.get235Accuracy());
      }
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
