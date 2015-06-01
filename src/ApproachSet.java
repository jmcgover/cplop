import java.util.*;

public class ApproachSet {
   /*Constructor*/
   private int k[];
   private double alpha[];
   private Phylogeny tree;
   private List<Pyroprint> isolates1623;
   private List<Pyroprint> isolates235;
   private ExperimentResult[][] results;

   public ApproachSet(int k[], double alpha[], Phylogeny tree) {
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
      Filter<Pyroprint> filter = new IsolateFilter();
      Pyroprint iso1623Rep = null;
      Pyroprint iso235Rep = null;

      NearestNeighbors nn1623 = null;
      NearestNeighbors nn235 = null;

      Species sp1623 = null;
      Species sp235  = null;
      Species spBoth = null;

      LinkedList<PearsonCorrelation> mergedList = null;

      for (Species s : tree.getAllSpecies().values()) {
         System.out.println("Testing " + s);
         for (Host h : s.getHosts().values()) {
            for (Isolate i : h.getIsolates().values()) {
               iso1623Rep = i.get1623Rep();
               iso235Rep = i.get235Rep();
               if (iso1623Rep != null && iso235Rep != null) {
                  nn1623 = new NearestNeighbors(iso1623Rep, this.isolates1623, filter);
                  nn235 = new NearestNeighbors(iso235Rep, this.isolates235, filter);
                  for (int j = 0; j < this.k.length; j++) {
                     for (int a = 0; a < this.alpha.length; a++) {
                        sp1623 = nn1623.classifySpecies(k[j], alpha[a]);
                        sp235 = nn235.classifySpecies(k[j], alpha[a]);

                        mergedList = new LinkedList<PearsonCorrelation>();
                        mergedList.addAll(nn1623.getTopKAlpha(k[j], alpha[a]));
                        mergedList.addAll(nn235.getTopKAlpha(k[j], alpha[a]));

                        spBoth = NearestNeighbors.classifySpecies(k[j], alpha[a], mergedList);
                        this.results[j][a].addClassification(s, spBoth);
                        
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
      Phylogeny tree = null;
      String treeFilename = "";
      boolean filenameSet = false;
      ApproachSet experiment = null;
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

      experiment = new ApproachSet(k, alpha, tree);
      results = experiment.runExperiment();
      for (ExperimentResult[] i : results) {
         for (ExperimentResult j : i) {
            System.out.printf("k: %d alpha: %.3f\n", j.getK(), j.getAlpha());
         }
      }
   }
   public static void printUsage(String msg){
      System.err.println(msg);
      printUsage();
   }
   public static void printUsage(){
      String usage = "usage: ";
      usage += "java";
      usage += " ";
      usage += "ApproachSet";
      usage += " ";
      usage += "<options...>";
      System.err.println(usage);
      System.err.println();
      System.err.println("Options:");
      printOption("[-h  | --help]","displays this help and exits");
      printOption("<-f  | --filename>","filename of the tree to load");
      System.exit(1);
   }
   public static void printOption(String flag, String explanation){
      System.err.printf("\t%s\t%s\n",flag,explanation);
   }
}
