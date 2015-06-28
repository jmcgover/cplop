import java.util.*;
import java.io.*;
public class ResultsPrinter {
   public static void main (String[] args) {
      java.util.Date date = new java.util.Date();

      ExperimentResult[][] results = null;
      String resultFilename = "";
      boolean filenameSet = false;

      /*Argument Parsing*/
      if (args.length > 0) {
         try {
            for (int i = 0; i < args.length; i++) {
               // Filename
               if (args[i].equals("-f") | args[i].equals("--filename")) {
                  filenameSet = true;
                  resultFilename = args[++i];
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
      
      System.out.printf("Loading %s...\n", resultFilename);
      results = ExperimentResult.loadArray(resultFilename);
      printOldCSV(results, System.out);
      System.out.println("SUCCESS!");

   }
   public static void printOldCSV(ExperimentResult[][] results, PrintStream stream) {

      for (int k = 0; k < results.length; k++) {
         for (int a = 0; a < results[k].length; a++) {
            LinkedList<SpeciesResult> perSpecies = new LinkedList<SpeciesResult>(results[k][a].getSpeciesResults().values());
            Collections.sort(perSpecies);
            for (SpeciesResult s : perSpecies) {
               LinkedList<Classification> classifications = new LinkedList<Classification>(s.getClassifications().values());
               Collections.sort(classifications);
               for (Classification c : classifications) {
                  if (c.getCommonName().equals(s.getCommonName())) {
                     stream.printf("%d,%.3f,%s,BOTH,%.3f\n",
                           s.getK(),
                           s.getAlpha(),
                           s.getCommonName(),
                           1.0*c.getClassifications() / s.getTotalClassifications());
                  }
               }
            }
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
      usage += "ResultsPrinter";
      usage += " ";
      usage += "<options...>";
      System.err.println(usage);
      System.err.println();
      System.err.println("Options:");
      printOption("[-h  | --help]","displays this help and exits");
      printOption("<-f  | --filename>","filename of the results to load");
      System.exit(1);
   }
   public static void printOption(String flag, String explanation){
      System.err.printf("\t%s\t%s\n",flag,explanation);
   }
}
