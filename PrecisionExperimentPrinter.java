import java.io.*;
import java.util.*;

public class PrecisionExperimentPrinter implements ExperimentPrinter{
   private int[] k;
   private double[] alpha;
   private AggregateSpeciesAccuracy[][] testAccuracies;
   public PrecisionExperimentPrinter(Experiment<AggregateSpeciesAccuracy> experiment){
      this.k = experiment.getKValues();
      this.alpha = experiment.getAlphaValues();
      this.testAccuracies = experiment.getTestAccuracies();
   }
   public void printToCsv(){
      printToCsv(System.err);
   }
   private void printAccuracy(PrintStream stream, 
         int k, double alpha, Species s, String region, Accuracy acc){
      stream.printf("%d,%.3f,%s,%s,%d,%d,%d,%d,%.3f,%.3f\n",
            k,
            alpha,
            s.getCommonName(),region,
            acc.getSuccesses(),
            acc.getFailures(),
            acc.getNonDecisions(),
            acc.getTotal(),
            acc.getSuccessFailureAccuracy() * 100,
            acc.getTotalAccuracy() * 100);
   }
   public void printToCsv(PrintStream stream){
      stream.println(this.getCsvHeader());
      for (int i = 0; i < this.k.length; i++) {
         for (int a = 0; a < this.alpha.length; a++) {
            Species s = null;
            Accuracy regAcc = null;
            Collection<SpeciesAccuracy> thisTest = this.testAccuracies[i][a].getAccuracies();
            for (SpeciesAccuracy acc : thisTest) {
               s = acc.getSpecies();
               regAcc = acc.get1623Accuracy();
               printAccuracy(stream, k[i], alpha[a], s, "16-23", regAcc);
               regAcc = acc.get235Accuracy();
               printAccuracy(stream, k[i], alpha[a], s, "23-5" , regAcc);
            }
            TotalSpeciesAccuracy totalAccuracy = new TotalSpeciesAccuracy(thisTest);
            s = totalAccuracy.getSpecies();
            regAcc = totalAccuracy.get1623Accuracy();
            printAccuracy(stream, k[i], alpha[a], s, "16-23", regAcc);
            regAcc = totalAccuracy.get235Accuracy();
            printAccuracy(stream, k[i], alpha[a], s, "23-5" , regAcc);

         }
      }
   }
   public String getCsvHeader(){
      String header = "k,alpha,species,applied-region";
      header += ",";
      header += "Successes (# Pyroprints)";
      header += ",";
      header += "Failures (# Pyroprints)";
      header += ",";
      header += "NonDecisions (# Pyroprints)";
      header += ",";
      header += "Total";
      header += ",";
      header += "Decision Accuracy(\\%)";
      header += ",";
      header += "Overall Accuracy(\\%)";
      return header;
   }

}
