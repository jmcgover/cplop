import java.io.*;
import java.util.*;

public class RecallExperiment 
   implements Experiment<AggregateSpeciesAccuracy>, Serializable{
   int[] k;
   double[] alpha;
   private Phylogeny tree;
   private List<Species> species;
   private Collection<Pyroprint> library;
   AggregateSpeciesAccuracy[][] testAccuracies;

   public RecallExperiment(int k, double alpha, Phylogeny tree){
      this.k = new int[1];
      this.k[0] = k;
      this.alpha = new double[1];
      this.alpha[0] = alpha;

      this.tree = tree;
      this.species = new LinkedList<Species>(this.tree.getAllSpecies().values());
      Collections.sort(this.species);
      this.library  = new LinkedList<Pyroprint>(this.tree.getAllPyroprints().values());
      this.testAccuracies = new AggregateSpeciesAccuracy[this.k.length][this.alpha.length];
      for (int i = 0; i < this.k.length; i++) {
         for (int a = 0; a < this.alpha.length; a++) {
            testAccuracies[i][a] = new AggregateSpeciesAccuracy(
                  this.k[i],this.alpha[a], species);
         }
      }
   }
   public RecallExperiment(int k[], double alpha[], Phylogeny tree){
      this.k = k;
      this.alpha = alpha;

      this.tree = tree;
      this.species = new LinkedList<Species>(this.tree.getAllSpecies().values());
      Collections.sort(this.species);
      this.library  = new LinkedList<Pyroprint>(this.tree.getAllPyroprints().values());
      this.testAccuracies = new AggregateSpeciesAccuracy[this.k.length][this.alpha.length];
      for (int i = 0; i < this.k.length; i++) {
         for (int a = 0; a < this.alpha.length; a++) {
            testAccuracies[i][a] = new AggregateSpeciesAccuracy(
                  this.k[i],this.alpha[a], species);
         }
      }
      Collections.sort(this.species);
   }
   public int[] getKValues(){
      return k;
   }
   public double[] getAlphaValues(){
      return alpha;
   }
   public AggregateSpeciesAccuracy[][] getTestAccuracies(){
      return testAccuracies;
   }

   public void runExperiment(){
      Filter<Pyroprint> filter = new IsolateFilter();
      Species result = null;
      for (Species s : this.species) {
         System.err.print("Starting " + s + "... ");
         // Testing jsut Pyroprints from this species
         for (Pyroprint p : s.getPyroprints().values()) {
            // Calculate Nearest Neighbors
            if (p.isErroneous() == false) {
               NearestNeighbors nearestNeighbors = new NearestNeighbors(p, this.library, filter);
               for (int i = 0; i < k.length; i++) {
                  for (int a = 0; a < alpha.length; a++) {
                     // Grab proper accuracy object to update
                     Accuracy currAcc = testAccuracies[i][a].getProperAccuracy(p);
                     // Run Test
                     result = null;
                     result = nearestNeighbors.classifySpecies(k[i],alpha[a]);
                     if (result == null) {
                        currAcc.addNonDecision();
                     } // IF result Null
                     else {
   //                     System.err.print(s + " classified as " + result + ": ");
                        if (s.equals(result)) {
                           currAcc.addSuccess();
   //                        System.out.println("TRUE");
                        }
                        else {
                           currAcc.addFailure();
   //                        System.out.println("FALSE");
                        }
                     } // ELSE result not null
                  }
                  // END alpha
               }
               // END k
            }
            // END Pyroprint
         }
         System.err.println("finished.");
      }
      // END Species
   }

   public void save(String experimentFilename){
      FileOutputStream fileOut = null;
      ObjectOutputStream objectOut = null;
      try {
         fileOut = new FileOutputStream(experimentFilename);
         objectOut = new ObjectOutputStream(fileOut);
         objectOut.writeObject(this);
      }
      catch (FileNotFoundException e) {
         System.err.println("Could not find? '" + experimentFilename + "'.");
         System.exit(1);
      }
      catch (IOException e) {
         System.err.println("Could not save '" + experimentFilename + "'.");
         e.printStackTrace();
         System.exit(1);
      }
   }
   public static RecallExperiment load(String experimentFilename){
      RecallExperiment experiment = null;
      FileInputStream fileIn = null;
      ObjectInputStream objectIn = null;
      Object obj = null;
      try {
         fileIn = new FileInputStream(experimentFilename);
         objectIn = new ObjectInputStream(fileIn);
         obj = objectIn.readObject();
      }
      catch (FileNotFoundException e) {
         System.err.println("Could not open '" + experimentFilename + "'.");
         e.printStackTrace();
         System.exit(1);
      }
      catch (IOException e) {
         System.err.println("IOException for '" + experimentFilename + "'.");
         e.printStackTrace();
         System.exit(1);
      }
      catch (ClassNotFoundException e) {
         System.err.println("Could not find the Class of the object you are trying to load.");
         System.exit(1);
      }

      if (obj instanceof RecallExperiment) {
         experiment = (RecallExperiment)obj;
      }
      else {
         throw new ClassCastException( "Your file '" + experimentFilename + "' was not a RecallExperiment object.\n");
      }
      return experiment;
   }

}
