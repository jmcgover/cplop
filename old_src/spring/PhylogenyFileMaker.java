import java.io.*;
import java.util.*;
import java.sql.*;

public class PhylogenyFileMaker{
   private static final int LOW_END_FILTER = 4;
   public static void printUsage(String msg){
      System.err.println(msg);
      printUsage();
   }
   public static void printUsage(){
      String usage = "";
      usage += "java";
      usage += " ";
      usage += "PhylogenyFileMaker";
      usage += " ";
      usage += "[options...]";
      System.err.println(usage);
      System.err.println();
      System.err.println("Builds and then saves the tree to file as (timestamp).phyl");
      System.err.println("Options:");
      printOption("[-h  | --help]","displays this help and exits");
      printOption("[<-f | --filename> <filename>]","saves the tree object to filename");
      System.exit(1);
   }
   public static void printOption(String flag, String explanation){
      System.err.printf("\t%s\t%s\n",flag,explanation);
   }
   public static void main(String[] args) throws SQLException{
      java.util.Date date = new java.util.Date();
      // Tree options
      boolean loadTree = false;
      boolean saveTree = true;
      boolean customName = false;
      String phylogenyFilename = "tree" +  date.toString().replace(" ","-") + ".phyl";
      Phylogeny tree = null;
      String url  = "jdbc:mysql://localhost/CPLOP";
      String user = "root";
      String pass = "Jeffrey";

      // ARGS
      if (args.length > 0) {
         try {
            for (int i = 0; i < args.length; i++) {
               // Help
               if (args[i].equals("-f") || args[i].equals("--filename")) {
                  if (i < args.length - 1 && args[i + 1].charAt(0) != '-') {
                     phylogenyFilename = args[++i];
                  }
                  else {
                     printUsage("Option flag instead of filename.");
                  }
                  System.err.printf("Saving the tree to '%s'.\n", phylogenyFilename);
               }
               if (args[i].equals("-h") | args[i].equals("--help")) {
                  printUsage();
               }
            }
         }
         catch (IndexOutOfBoundsException e) {
            /*Reached the end, but wanted an argument.*/
            System.err.println(args[args.length - 1] + " needs an argument.");
            System.exit(1);
         }
      }

      // Build Tree
      System.err.printf("Connecting to %s...\n",url);
      Database database = new Database(url, user, pass);
      System.err.println("Successful connection.");
      TreeBuilder builder = new TreeBuilder(database);
      tree = builder.get();

      // Save Tree
      System.err.println("Saving tree to '" + phylogenyFilename + "'...");
      tree.save(phylogenyFilename);
      System.err.println("Saved.");

      //Print Tree
      tree.printTree(System.err);
      ArrayList<Species> speciesList = new ArrayList<Species>(tree.getAllSpecies().values());

      System.err.println("Well, I got here...");
   }
}
