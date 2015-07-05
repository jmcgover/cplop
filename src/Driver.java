import java.util.*;

public class Driver{

   public enum Method {
      MEAN, WINNER, SETWISE, INTERSECTION
   }

   public static void main(String[] args) {
      java.util.Date date = new java.util.Date();
      Phylogeny tree = null;
      String treeFilename = "";
      String resultFilename = "set-approach" +  date.toString().replace(" ","-") + ".res";
      boolean filenameSet = false;
      ApproachWinner experiment = null;
      TreeFilter filter;
      Method method = null;

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
               if (args[i].equals("-m") | args[i].equals("--meanwise")) {
                  method = Method.MEAN;
               }
               if (args[i].equals("-w") | args[i].equals("--winner")) {
                  method = Method.WINNER;
               }
               if (args[i].equals("-s") | args[i].equals("--setwise")) {
                  method = Method.SETWISE;
               }
               if (args[i].equals("-i") | args[i].equals("--intersection")) {
                  method = Method.INTERSECTION;
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
         printUsage("Please provide a filename.");
      }
      if (method == null) {
         printUsage("Please specify which method you want to perform.");
      }
      System.err.printf("Using method ");
      switch (method) {
         case MEAN:           System.err.printf("MEAN\n");
                              break;
         case WINNER:         System.err.printf("WINNER\n");
                              break;
         case SETWISE:        System.err.printf("SETWISE\n");
                              break;
         case INTERSECTION:   System.err.printf("INTERSECTION\n");
                              break;
         default:             System.err.printf("INVALID METHOD %s!\n", method);
                              printUsage("Please proved a valid method.");
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
               switch (method) {
                  case MEAN:           classifier = new Meanwise(i, tree);
                                       break;
                  case WINNER:         classifier = new Winner(i, tree);
                                       break;
                  case SETWISE:        classifier = new Setwise(i, tree);
                                       break;
                  case INTERSECTION:   classifier = new Intersection(i, tree);
                                       break;
                  default:             printUsage(String.format("Invalid method: %d", method));
               }
               for (int a = 0; a < alpha.length; a++) {
                  for (int j = 0; j < k.length; j++) {
                     result = null;
                     result = classifier.classify(k[j], alpha[a]);
//                     System.out.printf("CLASSIFIED %s as %s\n", s, result);
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
      printOption("[-h  | --help]","\tdisplays this help and exits");
      printOption("<-f  | --filename>","filename of the tree to load");
      printOption("[-o  | --output]","filename to save the experiment results as");
      printOption("<method>","\tmethod of classification to use");
      System.err.println("Methods:");
      printOption("[-m  | --meanwise]","mean-based method");
      printOption("[-w  | --winner]","winner-based method");
      printOption("[-s  | --setwise]","set-based method");
      printOption("[-i  | --intersection]","intersection-based method");
      System.exit(1);
   }
   public static void printOption(String flag, String explanation){
      System.err.printf("\t%s\t%s\n",flag,explanation);
   }
}
