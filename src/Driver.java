import java.util.*;

public class Driver{

   public static void main(String[] args) {
      java.util.Date date = new java.util.Date();
      Phylogeny tree = null;
      String treeFilename = "";
      String resultFilename = "set-approach" +  date.toString().replace(" ","-") + ".res";
      boolean filenameSet = false;
      ApproachWinner experiment = null;
      TreeFilter filter;

      // Experiment Parameters
      int k[] = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16,17};
      double alpha[] = {0.0, .5, .9, .95, .96, .97, .98, .99, 1.0};

      /*Argument Parsing*/
      if (args.length > 0) {
         try {
            for (int i = 0; i < args.length; i++) {
               // Filename
               if (args[i].equals("-f") | args[i].equals("--filename")) {
                  filenameSet = true;
                  treeFilename = args[++i];
               }
               // Output
               if (args[i].equals("-o") | args[i].equals("--output")) {
                  resultFilename = args[++i];
                  System.out.printf("Using custom results filename %s.\n", resultFilename);
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

      if (filenameSet == false) {
         printUsage();
      }

      // Load Tree
      System.err.println("Loading tree from '" + treeFilename + "'...");
      tree = Phylogeny.load(treeFilename);
      System.err.println("Loaded.");


      // Filter Tree
      System.err.println("Filtering...");
      filter = new TreeFilter();
      tree = filter.removeEnvironmental(tree);
      tree = filter.removeBad(tree);
      tree = filter.removeIncompleteIsolates(tree);
      tree = filter.removeSpeciesBelow(tree, 5);

      //Run Experiments
      Classifier<Isolate, Phylogeny, Species> classifier = null;
      LinkedList<Species> allSpecies = new LinkedList<Species>(tree.getAllSpecies().values());
      Collections.sort(allSpecies);

      /*Track Results*/
      ExperimentResult[][] results = new ExperimentResult[k.length][alpha.length];
      for (int j = 0; j < k.length; j++) {
         for (int a = 0; a < alpha.length; a++) {
            results[j][a] = new ExperimentResult(k[j], alpha[a], tree);
         }
      }

      Species result = null;
      for (Species s : allSpecies) {
         System.err.println("Testing " + s + "...");
         for (Host h : s.getHosts().values()) {
            for (Isolate i : h.getIsolates().values()) {
               classifier = new Meanwise(i, tree);
               for (int j = 0; j < k.length; j++) {
                  for (int a = 0; a < alpha.length; a++) {
                     result = null;
                     result = classifier.classify(k[j], alpha[a]);
                     results[j][a].addClassification(s, result);
                  }
               }
            }
         }
      }

      // Save Experiment Results
      System.out.printf("Saving results to %s...\n", resultFilename);
      ExperimentResult.saveArray(results, resultFilename);
      System.out.println("Done.");
   }

   public static ExperimentResult[][] runTests(int[] k, double[] alpha, Phylogeny tree) {
      return null;
   }

   public static void printUsage(String msg){
      System.err.println(msg);
      printUsage();
   }
   public static void printUsage(){
      String usage = "usage: ";
      usage += "java";
      usage += " ";
      usage += Driver.class.getName();
      usage += " ";
      usage += "<options...>";
      System.err.println(usage);
      System.err.println();
      System.err.println("Options:");
      printOption("[-h  | --help]","displays this help and exits");
      printOption("<-f  | --filename>","filename of the tree to load");
      printOption("<-o  | --output>","filename to save the experiment results as");
      System.exit(1);
   }
   public static void printOption(String flag, String explanation){
      System.err.printf("\t%s\t%s\n",flag,explanation);
   }
}
