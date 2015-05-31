import java.io.*;
import java.util.*;
import java.sql.*;

public class PrintTree {
   public static void printUsage(String msg){
      System.err.println(msg);
      printUsage();
   }
   public static void printUsage(){
      String usage = "";
      usage += "java";
      usage += " ";
      usage += "PrintTree";
      usage += " ";
      usage += "<-t tree-file.phyl>";
      usage += " ";
      usage += "[options...]";
      System.err.println(usage);
      System.err.println();
      System.err.println("Options:");
      printOption("[-h  | --help]","displays this help and exits");

      printOption("<-t  | --tree>","specifies the tree file to print");
      printOption("[-a  | --alphabetical]","prints csv format alphabetically");
      printOption("[-c  | --csv]","prints only in csv format");
      printOption("[-d  | --detail]","prints only detailed format");

      System.exit(1);
   }
   public static void printOption(String flag, String explanation){
      System.err.printf("\t%s\t%s\n",flag,explanation);
   } // End printOption
   public static void main(String[] args){
      String treeFilename = null;
      boolean csvOnly = false;
      boolean alphabetical = false;
      boolean detailOnly = false;
      Phylogeny tree = null;

      // ARGS
      if (args.length > 0) {
         try {
            for (int i = 0; i < args.length; i++) {
               // Tree
               if (args[i].equals("-t") | args[i].equals("--tree")) {
                  treeFilename = args[++i];
               }

               // CSV
               if (args[i].equals("-c") | args[i].equals("--csv")) {
                  csvOnly = true;
                  System.err.println("Only printing CSV view.");
               }

               // CSV -- Alphabetical
               if (args[i].equals("-a") | args[i].equals("--alphabetic")) {
                  alphabetical = true;
                  if (detailOnly) {
                     System.err.println("Detail view ignores alphabetical.");
                  }
                  else {
                     System.err.println("Printing CSV alphabetically.");
                  }
               }

               // Detail 
               if (args[i].equals("-d") | args[i].equals("--detail")) {
                  detailOnly = true;
                  System.err.println("Only printing DETAIL view.");
                  if (alphabetical) {
                     System.err.println("Alphabetical sort will be ignored.");
                  }
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
      } // Args Check

      if (treeFilename == null) {
         printUsage("Please provide a tree filename.");
      } // tree filename check

      // Load Tree
      System.err.println("Loading tree from '" + treeFilename + "'...");
      tree = Phylogeny.load(treeFilename);
      System.err.println("Loaded.");

      if (!csvOnly) {
         System.err.println("Printing DETAIL view...");
         printDetail(tree);
         System.err.println("Done.");
      }

      if (!detailOnly) {
         if (alphabetical) {
            System.err.println("Printing alphabetical Species CSV...");
            printSpeciesCSV(tree, null);
            System.err.println("Done.");
         }
         else {
            System.err.println("Printing Species by count CSV...");
            printSpeciesCSV(tree, new SortByPyroprintCount());
            System.err.println("Done.");
         }
      }
   } // End main

   static void printDetail(Phylogeny tree) {
      //DETAIL
      tree.printTree(System.out);
   }

   static void printSpeciesCSV(Phylogeny tree, Comparator<Species> speciesSort) {
      //CSV
      ArrayList<Species> speciesList = new ArrayList<Species>(tree.getAllSpecies().values());
      if (speciesSort == null) {
         Collections.sort(speciesList);
      }
      else {
         Collections.sort(speciesList, speciesSort);
      }
      int goodPyroCount = 0;
      int badPyroCount = 0;
      int pyroCount = 0;
      int numSpecies = 0;
      int i = 0;
      System.out.printf("%3s,%5s,%5s,%5s,%s\n","ndx","Good","Bad","All","Species Name");
      for (Species s : speciesList) {
         System.out.printf("%3s,%5d,%5d,%5d,%s\n",i,s.getGoodPyroprintCount(),s.getBadPyroprintCount(),s.getAllPyroprintCount(),s);
         goodPyroCount += s.getGoodPyroprintCount();
         badPyroCount += s.getBadPyroprintCount();
         pyroCount += s.getAllPyroprintCount();
         numSpecies++;
         i++;
      }
      System.out.printf("%3s,%5d,%5d,%5d,%s\n",i,goodPyroCount,badPyroCount,pyroCount,"Total");
      System.err.printf("%s: %d\n","Good Pyros",goodPyroCount);
      System.err.printf("%s: %d\n","Bad  Pyros",badPyroCount);
      System.err.printf("%s: %d\n","All  Pyros",pyroCount);
      System.err.printf("%s: %d\n","NumSpecies",numSpecies);
   }
}
