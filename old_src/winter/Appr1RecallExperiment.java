import java.io.*;
import java.util.*;

public class Appr1RecallExperiment 
   implements Experiment<AggregateSpeciesAccuracy>, Serializable{
   int[] k;
   double[] alpha;
   private Phylogeny tree;
   private List<Species> species;
   private Collection<Pyroprint> library;
   AggregateSpeciesAccuracy[][] testAccuracies;

   private String debugSpecies = null;

   public Appr1RecallExperiment(int k, double alpha, Phylogeny tree){
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
   public Appr1RecallExperiment(int k[], double alpha[], Phylogeny tree){
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
   public Appr1RecallExperiment(int k[], double alpha[], Phylogeny tree, String debugSpecies){
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
      System.err.println("Printing " + debugSpecies);
      this.debugSpecies = debugSpecies;
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
         for (Host h : s.getHosts().values()) {
            for (Isolate isol : h.getIsolates().values()) {
//               System.out.printf("Isolate: %s, %s, %s\n", s, h, isol);
               Pyroprint repPyro1623 = getRepresentative(isol, "16-23");
               Pyroprint repPyro235 = getRepresentative(isol, "23-5");

//               System.out.printf("Rep 16-23: %s.\n", repPyro1623);
//               System.out.printf("Rep 23-5: %s.\n", repPyro235);

               if (repPyro1623 != null && repPyro235 != null) {
                  NearestNeighbors nn1623 = new NearestNeighbors(repPyro1623, this.library, filter);
                  NearestNeighbors nn235 = new NearestNeighbors(repPyro235, this.library, filter);

                  for (int i = 0; i < k.length; i++) {
                     for (int a = 0; a < alpha.length; a++) {
                        // Grab proper accuracy object to update
                        Accuracy currAcc = testAccuracies[i][a].getProperAccuracy(repPyro235);

                        //Merge lists
                        List<PearsonCorrelation> list1623 = nn1623.getTopKAlpha(i, a);
                        List<PearsonCorrelation> list235 = nn235.getTopKAlpha(i, a);

                        ArrayList<PearsonCorrelation> merged = new ArrayList<PearsonCorrelation>();
                        merged.addAll(list1623);
                        merged.addAll(list235);

                        // Run Test
                        result = null;
                        result = nn1623.classifySpecies(k[i],alpha[a], merged);
                        if (isol.getCommonName().equals(debugSpecies)) {
                           nn1623.printTopKAlpha(k[i], alpha[a], System.err);
                           nn235.printTopKAlpha(k[i], alpha[a], System.err);
                        }
                        if (result == null) {
                           currAcc.addNonDecision();
                        } // IF result Null
                        else {
//                         System.err.print(s + " classified as " + result + ": ");
                           if (s.equals(result)) {
                              currAcc.addSuccess();
//                            System.out.println("TRUE");
                           }
                           else {
                              currAcc.addFailure();
//                            System.out.println("FALSE");
                           }
                        } // ELSE result not null
                     }
                     // END alpha
                  }

               }

            }
         }
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
                     if (p.getCommonName().equals(debugSpecies)) {
                        nearestNeighbors.printTopKAlpha(k[i], alpha[a], System.err);
                     }
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
   public static Appr1RecallExperiment load(String experimentFilename){
      Appr1RecallExperiment experiment = null;
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

      if (obj instanceof Appr1RecallExperiment) {
         experiment = (Appr1RecallExperiment)obj;
      }
      else {
         throw new ClassCastException( "Your file '" + experimentFilename + "' was not a Appr1RecallExperiment object.\n");
      }
      return experiment;
   }

   public Pyroprint getRepresentative(Isolate i, String region){
      ArrayList<Pyroprint> pyros = new ArrayList<Pyroprint>();

      for (Pyroprint p : i.getPyroprints().values()) {
         if (!p.isErroneous() && p.getAppliedRegion().equals(region)) {
            pyros.add(p);
         }
//         else {
//            System.out.printf("Skip(%s) %s, %s, %s\n", region, p, p.getAppliedRegion(), p.isErroneous());
//         }
      }
      if (pyros.size() > 0) {
         Collections.sort(pyros, new RepresentativeComparator());
         return pyros.get(0);
      }
      else {
         return null;
      }
   }

   public class RepresentativeComparator implements Comparator<Pyroprint>{
      public RepresentativeComparator() {
      }

      public int compare(Pyroprint a, Pyroprint b) {
         if (!a.isSameAppliedRegion(a)) {
            System.err.printf("Cannot compare %s (%s) and %s(%s)\n",
                  a,a.getAppliedRegion(),
                  b,b.getAppliedRegion());
            throw new IllegalArgumentException();
         }

         return a.getPyroId().compareTo( b.getPyroId());
      }
   }
}
