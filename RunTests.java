import java.io.*;
import java.util.*;
import java.sql.*;

public class RunTests{
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
      printOption("[<-f | --filename> filename]","loads the tree object from filename");
      printOption("[<-s | --save> [filename]]","saves the tree object to filename");
      printOption("[-t  | --treeOnly]","creates the tree and exits");
      System.exit(1);
   }
   public static void printOption(String flag, String explanation){
      System.err.printf("\t%s\t%s\n",flag,explanation);
   }
   public static void main(String[] args) throws SQLException{
      java.util.Date date = new java.util.Date();
      boolean loadTree = false;
      boolean saveTree = false;
      boolean treeOnly = false;
      String phylogenyFilename = "";
      int k = 17;
      double alpha = .95;
      // ARGS
      if (args.length > 0) {
         try {
            for (int i = 0; i < args.length; i++) {
               if (args[i].equals("-k") || args[i].equals("--list")) {
                  k = Integer.parseInt(args[++i]);
               }
               if (args[i].equals("-a") || args[i].equals("--alpha")) {
                  alpha = Double.parseDouble(args[++i]);
               }
               if (args[i].equals("-f") || args[i].equals("--filename")) {
                  if (saveTree) {
                     printUsage("Cannot both save and load a file.");
                  }
                  loadTree = true;
                  phylogenyFilename = args[++i];
                  System.err.printf("Loading the tree from '%s'.\n", phylogenyFilename);
               }
               if (args[i].equals("-s") || args[i].equals("--save")) {
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
               if (args[i].equals("-t") | args[i].equals("--tree")) {
                  treeOnly = true;
                  System.err.println("Only the tree will be built.");
               }
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
         System.err.println("Loading tree...");
         tree = Phylogeny.load(phylogenyFilename);
         System.err.println("Loaded.");
      }
      else {
         String url  = "jdbc:mysql://localhost/CPLOP";
         String user = "root";
         String pass = "Jeffrey";
         System.err.printf("Connecting to %s...\n",url);
         Database database = new Database(url, user, pass);
         System.err.println("Successful connection.");
         TreeBuilder builder = new TreeBuilder(database);

         System.err.println("Getting tree...");
         tree = builder.get();
         if(saveTree){
            System.err.println("Saving tree...");
            tree.save(phylogenyFilename);
            System.err.println("Saved.");
         }
      }
      tree.printTree(System.err);
      if (treeOnly) {
         System.err.println("Tree built. Exiting now.");
         System.exit(0);
      }
      

      ArrayList<Pyroprint> allPyroprints = new ArrayList<Pyroprint>(tree.getAllPyroprints().values());
      Pyroprint deerThing = tree.getPyroprint("Deer Mouse","MM0899","Rt-001","159") ;
      KAlphaNearest test = new KAlphaNearest(tree.getPyroprint("Deer Mouse","MM0899","Rt-001","159"), 17, .95);

      System.out.println("Calculating all...");
      KAlphaNearest allTest = null;
      Filter<Pyroprint> regionFilter = new RegionFilter();
      Filter<Pyroprint> isolateFilter = new IsolateFilter();

      ArrayList<Species> speciesList = new ArrayList<Species>(tree.getAllSpecies().values());
      ArrayList<RegionAccuracy<Species>> speciesAccuracy = new ArrayList<RegionAccuracy<Species>>();
      Species result = null;
      RegionAccuracy<Species> specAcc1623 = null;
      RegionAccuracy<Species> specAcc235 = null;
      RegionAccuracy<Species> currentAccObj = null;
      Collections.sort(speciesList);
      for (Species s : speciesList) {
         System.out.println(s);
      }

      NearestNeighbors neighborTest = new NearestNeighbors(deerThing, allPyroprints);

      result = neighborTest.classifySpecies(17, .95, regionFilter);
      LinkedList<NearestNeighbors> calculatedDists = new LinkedList<NearestNeighbors>();
      Collections.sort(allPyroprints);
      System.err.println("Calculating distances...");
      for (Pyroprint p : allPyroprints) {
         neighborTest = new NearestNeighbors(p, allPyroprints);
         calculatedDists.add(neighborTest);
      }
      System.err.println("Done.");

      for (NearestNeighbors n : calculatedDists) {
         System.out.println();
      }

      System.err.println("Well, I got here...");

   }
}
