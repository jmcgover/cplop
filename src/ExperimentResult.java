import java.io.*;
import java.util.*;

public class ExperimentResult implements Serializable{
   private int k;
   private double alpha;
   HashMap<String, SpeciesResult> speciesResults;

   public ExperimentResult(int k, double alpha, Phylogeny tree) {
      this.k = k;
      this.alpha = alpha;
      this.speciesResults = new HashMap<String, SpeciesResult>();
      for (Species s : tree.getAllSpecies().values()) {
         this.speciesResults.put(s.getCommonName(), new SpeciesResult(k, alpha, s, tree));
      }
   }
   public int addClassification(Species s, Species c) {
      if (s == null) {
         return 0;
      }
      return this.speciesResults.get(s.getCommonName()).addClassification(c);
   }

   public int getK() {
      return this.k;
   }
   public double getAlpha() {
      return this.alpha;
   }
   public HashMap<String, SpeciesResult> getSpeciesResults() {
      return this.speciesResults;
   }

   /*Saving and Loading*/
   public static void saveArray(ExperimentResult[][] results, String filename){
      FileOutputStream fileOut = null;
      ObjectOutputStream objectOut = null;
      try {
         fileOut = new FileOutputStream(filename);
         objectOut = new ObjectOutputStream(fileOut);
         objectOut.writeObject(results);
      }
      catch (FileNotFoundException e) {
         System.err.println("Could not find? '" + filename + "'.");
         System.exit(1);
      }
      catch (IOException e) {
         System.err.println("Could not save '" + filename + "'.");
         e.printStackTrace();
         System.exit(1);
      }
   }

   public static ExperimentResult[][] loadArray(String filename){
      ExperimentResult experiment[][] = null;
      FileInputStream fileIn = null;
      ObjectInputStream objectIn = null;
      Object obj = null;
      try {
         fileIn = new FileInputStream(filename);
         objectIn = new ObjectInputStream(fileIn);
         obj = objectIn.readObject();
      }
      catch (FileNotFoundException e) {
         System.err.println("Could not open '" + filename + "'.");
         e.printStackTrace();
         System.exit(1);
      }
      catch (IOException e) {
         System.err.println("IOException for '" + filename + "'.");
         e.printStackTrace();
         System.exit(1);
      }
      catch (ClassNotFoundException e) {
         System.err.println("Could not find the Class of the object you are trying to load.");
         System.exit(1);
      }

      if (obj instanceof ExperimentResult[][]) {
         experiment = (ExperimentResult[][])obj;
      }
      else {
         throw new ClassCastException( "Your file '" + filename + "' was not a ExperimentResult object.\n");
      }
      return experiment;
   }

   public void save(String resultFilename){
      FileOutputStream fileOut = null;
      ObjectOutputStream objectOut = null;
      try {
         fileOut = new FileOutputStream(resultFilename);
         objectOut = new ObjectOutputStream(fileOut);
         objectOut.writeObject(this);
      }
      catch (FileNotFoundException e) {
         System.err.println("Could not find? '" + resultFilename + "'.");
         System.exit(1);
      }
      catch (IOException e) {
         System.err.println("Could not save '" + resultFilename + "'.");
         e.printStackTrace();
         System.exit(1);
      }
   }
   public static ExperimentResult load(String resultFilename){
      ExperimentResult experiment = null;
      FileInputStream fileIn = null;
      ObjectInputStream objectIn = null;
      Object obj = null;
      try {
         fileIn = new FileInputStream(resultFilename);
         objectIn = new ObjectInputStream(fileIn);
         obj = objectIn.readObject();
      }
      catch (FileNotFoundException e) {
         System.err.println("Could not open '" + resultFilename + "'.");
         e.printStackTrace();
         System.exit(1);
      }
      catch (IOException e) {
         System.err.println("IOException for '" + resultFilename + "'.");
         e.printStackTrace();
         System.exit(1);
      }
      catch (ClassNotFoundException e) {
         System.err.println("Could not find the Class of the object you are trying to load.");
         System.exit(1);
      }

      if (obj instanceof ExperimentResult) {
         experiment = (ExperimentResult)obj;
      }
      else {
         throw new ClassCastException( "Your file '" + resultFilename + "' was not a ExperimentResult object.\n");
      }
      return experiment;
   }
}
