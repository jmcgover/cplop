import java.io.*;
import java.util.*;
//import java.sql.*;

public class RunAppr1Experiment{
   public static void printUsage(String msg){
      System.err.println(msg);
      printUsage();
   }
   public static void printOption(String flag, String explanation){
      System.err.printf("\t%s\t%s\n",flag,explanation);
   }
   public static void printUsage(){
      String usage = "";
      usage += "java";
      usage += " ";
      usage += "RunAppr1Experiment";
      usage += " ";
      usage += "<-t tree-filename>";
      usage += "<-r | --recall | -p | --precision>";
      usage += "[options...]";
      System.err.println(usage);
      System.err.println();
      System.err.println("Options:");
      printOption("[-h  | --help]","displays this help and exits");
      printOption("[-r  | --recall]","runs recall experiment");
      printOption("[-p  | --precision]","runs precision experiment");

      System.exit(1);
   }
   public static void main (String[] args) {
      java.util.Date date = new java.util.Date();

      String treeFilename = null;
      String outputFilename = null;
      String debugSpecies = null;
      boolean recallFlag = false;
      boolean precisionFlag = false;
      Phylogeny tree = null;

      // ARGS
      if (args.length > 0) {
         try {
            for (int i = 0; i < args.length; i++) {
               // Tree Load
               if (args[i].equals("-t") || args[i].equals("--load-tree")) {
                  treeFilename = args[++i];
                  System.err.printf("Loading the tree from '%s'.\n", treeFilename);
               }
               // Recall
               if (args[i].equals("-r") || args[i].equals("--recall")) {
                  if (precisionFlag) {
                     printUsage("Cannot run both precision AND recall.");
                  }
                  recallFlag = true;
                  System.err.printf("Will be performing RECALL.\n");
                  if (i + 1 < args.length && !args[i + 1].startsWith("-")) {
                     debugSpecies = args[++i];
                  }
               }
               // Precision
               if (args[i].equals("-p") || args[i].equals("--precision")) {
                  if (recallFlag) {
                     printUsage("Cannot run both recall AND precision.");
                  }
                  precisionFlag = true;
                  System.err.printf("Will be performing PRECISION.\n");
               }
               // Help
               if (args[i].equals("-h") | args[i].equals("--help")) {
                  printUsage();
               }
            }
         }
         catch (IndexOutOfBoundsException e) {
            System.err.println(args[args.length - 1] + " needs an argument.");
            System.exit(1);
         }
      }

      if (treeFilename == null) {
         printUsage("Please provide a tree filename.");
      }
      else {
         // Load Tree
         System.err.println("Loading tree from '" + treeFilename + "'...");
         tree = Phylogeny.load(treeFilename);
         System.err.println("Loaded.");
      }

      // Experiment Parameters
      int k[] = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16,17};
      double alpha[] = {0.0, .5, .9, .95, .96, .97, .98, .99, 1.0};

      Experiment<AggregateSpeciesAccuracy> experiment = null;
      ExperimentPrinter printer = null;
      String resultFilename = "res" + date.toString().replace(" ","_") + ".csv";
      if (precisionFlag) {
         experiment = new PrecisionExperiment(k, alpha, tree);
         resultFilename = "precision_" + resultFilename;
      }
      else if (recallFlag) {
         if (debugSpecies == null) {
            System.out.println("NORMAL");
            experiment = new Appr1RecallExperiment(k, alpha, tree);
         }
         else {
            System.out.println("DEBUG MODE");
            experiment = new Appr1RecallExperiment(k, alpha, tree, debugSpecies);
         }
         resultFilename = "recall_" + resultFilename;
      }
      else { 
         printUsage("Please provide a -r or -p argument.");
      }

      System.err.printf("Running experiments for k=%s, alpha=%s...\n",
            Arrays.toString(k),
            Arrays.toString(alpha));
      experiment.runExperiment();
      System.err.println("Done.");

      // Run Experiment
      PrintStream stream = null;
      try {
         stream = new PrintStream(resultFilename);
         System.err.println("Saving to file '" + resultFilename + "'.");
      } catch(FileNotFoundException e) {
         e.printStackTrace();
         System.exit(1);
      }
      printer = new CommasExperimentPrinter(experiment);
      printer.printToCsv();
      printer.printToCsv(stream);
      System.err.println("Well, I got here...");

   }
}
