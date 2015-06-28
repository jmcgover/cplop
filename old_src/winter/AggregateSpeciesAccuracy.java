import java.io.*;
import java.util.*;

public class AggregateSpeciesAccuracy implements Serializable{
   int k;
   double alpha;
   HashMap<String, SpeciesAccuracy> accuracies;
   public AggregateSpeciesAccuracy(int k, double alpha, Collection<Species> species){
      this.k = k;
      this.alpha = alpha;
      this.accuracies = new HashMap<String, SpeciesAccuracy>();
      for (Species s : species) {
         this.accuracies.put(s.getCommonName(), new SpeciesAccuracy(s));
      }
   }
   public Accuracy getProperAccuracy(Species s, String appliedRegion){
      return this.accuracies.get(s.getCommonName()).getProperAccuracy(s, appliedRegion);
   }
   public Accuracy getProperAccuracy(Pyroprint p){
      return this.accuracies.get(p.getCommonName()).getProperAccuracy(p);
   }
   public Collection<SpeciesAccuracy> getAccuracies(){
      return accuracies.values();
   }
   public SpeciesAccuracy getAccuracy(Species s){
      return accuracies.get(s.getCommonName());
   }
}
