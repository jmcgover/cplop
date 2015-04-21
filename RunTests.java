import java.io.*;
import java.util.*;
import java.sql.*;

public class RunTests{
   private static final int LOW_END_FILTER = 4;
   public static void printUsage(String msg){
      System.err.println(msg);
      printUsage();
   }
   public static void printUsage(){
      String usage = "";
      usage += "java";
      usage += " ";
      usage += "PerSpeciesAccuracy";
      usage += " ";
      usage += "[options...]";
      System.err.println(usage);
      System.err.println();
      System.err.println("Options:");
      printOption("[-h  | --help]","displays this help and exits");

      printOption("[<-l | --load-tree> filename]", "loads the tree object from filename");
      printOption("[<-s | --save-tree> [filename]]","saves the tree object to filename");
      printOption("[-t  | --tree-only]","creates the tree and exits");

      printOption("[<-p | --print-experiment> filename]","prints an already run experiment");
      printOption("[<-r | --save-experiment> [filename]]","saves the experiment to filename");
      printOption("[-e  | --experiment-only]","just runs the experiment");
      System.exit(1);
   }
   public static void printOption(String flag, String explanation){
      System.err.printf("\t%s\t%s\n",flag,explanation);
   }
   public static void main(String[] args) throws SQLException{
      java.util.Date date = new java.util.Date();
      // Tree options
      boolean loadTree = false;
      boolean saveTree = false;
      boolean treeOnly = false;
      String phylogenyFilename = "";

      // Experiment options
      boolean loadExperiment = false;
      boolean saveExperiment = false;
      boolean experimentOnly = false;
      String experimentFilename = "";

      // Experiment Parameters
      int k[] = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16,17};
      double alpha[] = {0.0, .5, .9, .95, .96, .97, .98, .99, 1.0};

      // ARGS
      if (args.length > 0) {
         try {
            for (int i = 0; i < args.length; i++) {
               if (args[i].equals("-k") || args[i].equals("--list")) {
                  k[0] = Integer.parseInt(args[++i]);
               }
               if (args[i].equals("-a") || args[i].equals("--alpha")) {
                  alpha[0] = Double.parseDouble(args[++i]);
               }
               // Tree Load
               if (args[i].equals("-l") || args[i].equals("--load-tree")) {
                  if (saveTree) {
                     printUsage("Cannot both save and load a file.");
                  }
                  loadTree = true;
                  phylogenyFilename = args[++i];
                  System.err.printf("Loading the tree from '%s'.\n", phylogenyFilename);
               }
               // Tree Save
               if (args[i].equals("-s") || args[i].equals("--save-tree")) {
                  if (loadTree) {
                     printUsage("Cannot both save and load a file.");
                  }
                  saveTree = true;

                  if (i < args.length - 1 && args[i + 1].charAt(0) != '-') {
                     phylogenyFilename = args[++i];
                  }
                  else {
                     phylogenyFilename = date.toString().replace(" ","_") + ".phyl";
                  }
                  System.err.printf("Saving the tree to '%s'.\n", phylogenyFilename);
               }
               // Tree Only Build
               if (args[i].equals("-t") | args[i].equals("--tree")) {
                  if (experimentOnly || loadExperiment || saveExperiment) {
                     printUsage("Only building tree is incompatible with running experiments.");
                  }
                  treeOnly = true;
                  System.err.println("Only the tree will be built.");
               }

               // Experiment Print
               if (args[i].equals("-p") || args[i].equals("--print-experiment")) {
                  if (saveExperiment) {
                     printUsage("Cannot both save and load a file.");
                  }
                  if (treeOnly) {
                     printUsage("Only building tree is incompatible with running experiments.");
                  }
                  loadExperiment = true;
                  experimentFilename = args[++i];
                  System.err.printf("Loading the experiment from '%s'.\n", experimentFilename);
               }
               // Experiment Save 
               if (args[i].equals("-r") || args[i].equals("--save-experiment")) {
                  if (loadExperiment) {
                     printUsage("Cannot both save and load a file.");
                  }
                  if (treeOnly) {
                     printUsage("Only building tree is incompatible with running experiments.");
                  }
                  saveExperiment = true;

                  if (i < args.length - 1 && args[i + 1].charAt(0) != '-') {
                     experimentFilename = args[++i];
                  }
                  else {
                     experimentFilename = date.toString().replace(" ","_") + ".exp";
                  }
                  System.err.printf("Saving the experiment to '%s'.\n", experimentFilename);
               }
               // Experiment Only Run
               if (args[i].equals("-e") | args[i].equals("--experiment")) {
                  if (treeOnly) {
                     printUsage("Only building tree is incompatible with running experiments.");
                  }
                  experimentOnly = true;
                  System.err.println("Only the experiment will be run.");
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

      // START
      Phylogeny tree = null;
      if (loadTree) {
         // Load Tree
         System.err.println("Loading tree from '" + phylogenyFilename + "'...");
         tree = Phylogeny.load(phylogenyFilename);
         System.err.println("Loaded.");
      }
      else {
         // Build Tree
         String url  = "jdbc:mysql://localhost/CPLOP";
         String user = "root";
         String pass = "Jeffrey";
         System.err.printf("Connecting to %s...\n",url);
         Database database = new Database(url, user, pass);
         System.err.println("Successful connection.");
         TreeBuilder builder = new TreeBuilder(database);

         tree = builder.get();
         if(saveTree){
            // Save Tree
            System.err.println("Saving tree to '" + phylogenyFilename + "'...");
            tree.save(phylogenyFilename);
            System.err.println("Saved.");
         }
      }
      //Print Tree
      tree.printTree(System.err);
      ArrayList<Species> speciesList = new ArrayList<Species>(tree.getAllSpecies().values());
      // Print Species by COUNT
      Collections.sort(speciesList, new SortByPyroprintCount());
      int goodPyroCount = 0;
      int badPyroCount = 0;
      int pyroCount = 0;
      int numSpecies = 0;
      int i = 0;
      for (Species s : speciesList) {
         System.out.printf("%2d,%5d,%5d,%5d,%s\n",i,s.getGoodPyroprintCount(),s.getBadPyroprintCount(),s.getAllPyroprintCount(),s);
         goodPyroCount += s.getGoodPyroprintCount();
         badPyroCount += s.getBadPyroprintCount();
         pyroCount += s.getAllPyroprintCount();
         numSpecies++;
         i++;
      }
      System.out.printf("%s: %d\n","Good Pyros",goodPyroCount);
      System.out.printf("%s: %d\n","Bad  Pyros",badPyroCount);
      System.out.printf("%s: %d\n","All  Pyros",pyroCount);
      System.out.printf("%s: %d\n\n","Specs",numSpecies);

      for (Species s : speciesList) {
         System.out.printf("%5d\t%5d\t%5d\t%s\n",s.getGoodPyroprintCount(),s.getBadPyroprintCount(),s.getAllPyroprintCount(), s);
      }
      // Print Species by NAME
      Collections.sort(speciesList);
      for (Species s : speciesList) {
         System.out.printf("%5d\t%5d\t%5d\t%s\n",s.getGoodPyroprintCount(),s.getBadPyroprintCount(),s.getAllPyroprintCount(), s);
      }
      i = 0;
      for (Species s : speciesList) {
         System.out.printf("%2d,%5d,%5d,%5d,%s\n",i,s.getGoodPyroprintCount(),s.getBadPyroprintCount(),s.getAllPyroprintCount(),s);
         i++;
      }
      System.out.println();

      if (treeOnly) {
         // Quit if tree-only
         System.err.println("Tree built. Exiting now.");
         System.exit(0);
      }
      // END Parsing and Prebuilding

      Pyroprint deerThing = tree.getPyroprint("Deer Mouse","MM0899","Rt-001","159") ;
      Pyroprint creekThing = tree.getPyroprint("SLO Creek Water","Higuera Bridge","ES-261","11242");
      Pyroprint hu = tree.getPyroprint("Hu","250-253","Pp-054","12426");

      // Running Experiments
      Collections.sort(speciesList, new SortByPyroprintCount());
      Map<String, Species> speciesInTree = tree.getAllSpecies();
      int numRemoved = 0;
      for (Species s : speciesList) {
         if (s.getGoodPyroprintCount() <= LOW_END_FILTER) {
            System.err.printf("Removing %s: %5d/%5d/%5d\n",s,s.getGoodPyroprintCount(),s.getBadPyroprintCount(),s.getAllPyroprintCount());
            speciesInTree.remove(s.key());
            numRemoved++;
         }
         else {
            for (Pyroprint p : s.getPyroprints().values()) {
               if (p.isEnvironmental()) {
                  System.err.println("Removing " + s + " " + p.key());
                  speciesInTree.remove(s.key());
                  numRemoved++;
                  break;
               }
            }
         }
      }
      goodPyroCount = 0;
      badPyroCount = 0;
      pyroCount = 0;
      numSpecies = 0;
      for (Species s : speciesList) {
         System.out.printf("t%5d\t%5d\t%5d\t%s\n",s.getGoodPyroprintCount(),s.getBadPyroprintCount(),s.getAllPyroprintCount(),s);
         goodPyroCount += s.getGoodPyroprintCount();
         badPyroCount += s.getBadPyroprintCount();
         pyroCount += s.getAllPyroprintCount();
         numSpecies++;
      }

      i = 0;
      speciesList = new ArrayList<Species>(tree.getAllSpecies().values());
      Collections.sort(speciesList, new SortByPyroprintCount());
      for (Species s : speciesList) {
         System.out.printf("%2d,%5d,%5d,%5d,%s\n", i, s.getGoodPyroprintCount(), s.getBadPyroprintCount(), s.getAllPyroprintCount(), s);
         i++;
      }
      System.out.printf("%s: %d\n","Pyros",pyroCount);
      System.out.printf("%s: %d\n\n","Specs",numSpecies);
      System.out.printf("%s: %d\n\n","Rem'd",numRemoved);
      RecallExperiment speciesExperiment = new RecallExperiment(k, alpha, tree);
      if (loadExperiment) {
         // Load Experiment
         System.err.println("Loading species experiment from '" 
               + experimentFilename + "'...");
         speciesExperiment = RecallExperiment.load(experimentFilename);
         System.err.println("Done.");
      }
      else {
         System.err.printf("Running experiments for k=%s, alpha=%s...\n",
               Arrays.toString(k),
               Arrays.toString(alpha));
         speciesExperiment = new RecallExperiment(k, alpha, tree);
         speciesExperiment.runExperiment();
         System.err.println("Done.");
         // Run Experiment
         if (saveExperiment) {
            // Save Experiment
            System.err.println("Saving species experiment to '" 
                  + experimentFilename + "'...");
            speciesExperiment.save(experimentFilename);
            System.err.println("Saved.");
         }
      }

      PrintStream stream = null;
//      String accuracyFilename = "overallAccuracies.csv";
      String accuracyFilename = "everyonesAccuracies.csv";
      try {
         stream = new PrintStream(accuracyFilename);
         System.err.println("Saving to file '" + accuracyFilename + "'.");
      } catch(FileNotFoundException e) {
         e.printStackTrace();
         System.exit(1);
      }
      ExperimentPrinter printer = new CommasExperimentPrinter(speciesExperiment);
      printer.printToCsv();
      printer.printToCsv(stream);
      System.err.println("Well, I got here...");
   }
}
