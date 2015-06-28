import java.util.*;

public class ApproachMean {
   /*Constructor*/
   private int k[];
   private double alpha[];
   private Phylogeny tree;
   private List<Pyroprint> isolates1623;
   private List<Pyroprint> isolates235;
   private ExperimentResult[][] results;

   public ApproachMean(int k[], double alpha[], Phylogeny tree) {
      this.k = k;
      this.alpha = alpha;
      this.tree = tree;
      this.isolates1623 = get1623Reps();
      this.isolates235 = get235Reps();
      this.results = new ExperimentResult[k.length][alpha.length];
      for (int i = 0; i < k.length; i++) {
         for (int a = 0; a < alpha.length; a++) {
            this.results[i][a] = new ExperimentResult(k[i], alpha[a], tree);
         }
      }
   }

   public ExperimentResult[][] runExperiment(){
      NearestIsolates ni = null;

      Species spResult = null;

      LinkedList<Species> speciesList = new LinkedList<Species>(tree.getAllSpecies().values());
      Collections.sort(speciesList);

      for (Species s : speciesList) {
         System.out.println("Testing " + s + "...");
         for (Host h : s.getHosts().values()) {
            for (Isolate i : h.getIsolates().values()) {
               if (!i.isEnvironmental()) {
                  ni = new NearestIsolates(i, tree.getAllIsolates().values());
                  for (int j = 0; j < this.k.length; j++) {
                     for (int a = 0; a < this.alpha.length; a++) {

                        spResult = ni.classifySpecies(k[j], alpha[a]);

                        this.results[j][a].addClassification(s, spResult);
                     }
                  }
               }
            }
         }
      }
      return this.results;
   }

   List<Pyroprint> get1623Reps() {
      LinkedList<Pyroprint> reps = new LinkedList<Pyroprint>(); 
      Pyroprint rep = null;
      for (Isolate i : this.tree.getAllIsolates().values()) {
         rep = i.get1623Rep();
         if (rep != null) {
            reps.add(rep);
         }
      }
      return reps;
   }

   List<Pyroprint> get235Reps() {
      LinkedList<Pyroprint> reps = new LinkedList<Pyroprint>(); 
      Pyroprint rep = null;
      for (Isolate i : this.tree.getAllIsolates().values()) {
         rep = i.get235Rep();
         if (rep != null) {
            reps.add(rep);
         }
      }
      return reps;
   }

   public static void main(String[] args) {
      java.util.Date date = new java.util.Date();
      Phylogeny tree = null;
      String treeFilename = "";
      String resultFilename = "set-mean" +  date.toString().replace(" ","-") + ".res";
      boolean filenameSet = false;
      ApproachMean experiment = null;
      ExperimentResult[][] results;

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

      experiment = new ApproachMean(k, alpha, tree);
      results = experiment.runExperiment();

      System.out.printf("Saving results to %s...\n", resultFilename);
      ExperimentResult.saveArray(results, resultFilename);
      System.out.println("Done.");
   }
   public static void printUsage(String msg){
      System.err.println(msg);
      printUsage();
   }
   public static void printUsage(){
      String usage = "usage: ";
      usage += "java";
      usage += " ";
      usage += "ApproachMean";
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
