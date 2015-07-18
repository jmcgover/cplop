import java.util.*;
import java.io.*;
public class ResultsPrinter {
   public enum OutType {
      OLD, MATRIX
   }
   public static void main (String[] args) throws FileNotFoundException {
      java.util.Date date = new java.util.Date();

      ExperimentResult[][] results = null;
      String resultFilename = "";
      boolean filenameSet = false;
      boolean column = false;
      String outFilename = null;
      OutType outputType = null;

      /*Argument Parsing*/
      if (args.length > 0) {
         try {
            for (int i = 0; i < args.length; i++) {
               // Filename
               if (args[i].equals("-f") | args[i].equals("--filename")) {
                  filenameSet = true;
                  resultFilename = args[++i];
               }
               if (args[i].equals("-o") | args[i].equals("--output")) {
                  outFilename = args[++i];
               }
               if (args[i].equals("-d") | args[i].equals("--deprecated")) {
                  outputType = OutType.OLD;
               }
               if (args[i].equals("-m") | args[i].equals("--matrix")) {
                  outputType = OutType.MATRIX;
               }
               if (args[i].equals("-c") | args[i].equals("--column")) {
                  column = true;
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
         printUsage("Please provide a filename to read from.");
      }
      if (outputType == null) {
         printUsage("Please provide a format to output to.");
      }
      
      PrintStream outStream = System.out;
      if (outFilename != null) {
         outStream = new PrintStream(outFilename);
      }
      System.out.printf("Loading %s...\n", resultFilename);
      results = ExperimentResult.loadArray(resultFilename);
      switch (outputType) {
         case OLD:   printOldCSV(results, outStream); 
                     break;
         case MATRIX:if (column) 
                        printMatrixColumn(results, outStream);
                     else 
                        printMatrixCSV(results, outStream);
                     break;
         default:    printUsage(String.format("What option is this? %d", outputType));
      }
      System.out.println("SUCCESS!");

   }
   public static void printMatrixColumn(ExperimentResult[][] results, PrintStream stream) {
      stream.printf("| k|alpha|");
      LinkedList<SpeciesResult> speciesNames = new LinkedList<SpeciesResult>(results[0][0].getSpeciesResults().values());
      Collections.sort(speciesNames);
      for (SpeciesResult speciesName : speciesNames) {
         String name = speciesName.toString();
         stream.printf("%5s|", name.length() > 5 ? name.substring(0,5) : name);
      }
      stream.printf("%s|", "total");
      stream.printf("\n");
      for (int k = 0; k < results.length; k++) {
         for (int a = 0; a < results[k].length; a++) {
            stream.printf("| k|alpha|speci|");
            for (SpeciesResult speciesName : speciesNames) {
               String name = speciesName.toString();
               stream.printf("%5s|", name.length() > 5 ? name.substring(0,5) : name);
            }
            stream.printf("%s|", "total");
            stream.printf("\n");

            ExperimentResult thisResult = results[k][a];
            LinkedList<SpeciesResult> perSpecies = new LinkedList<SpeciesResult>(thisResult.getSpeciesResults().values());
            Collections.sort(perSpecies);
            for (SpeciesResult species : perSpecies) {
               ArrayList<Classification> classifications = new ArrayList<Classification>(species.getClassifications().values());
               Collections.sort(classifications);
               stream.printf("|%2d|%1.3f|%5s|", thisResult.getK(), thisResult.getAlpha(), species.toString().length() > 5 ? species.toString().substring(0,5) : species);
               for (Classification c : classifications) {
                  stream.printf("%5d|", c.getNumClassifications());
               }
               stream.printf("%4d|", species.getTotal());
               stream.printf("\n");
            }
         }
      }
   }
   public static void printMatrixCSV(ExperimentResult[][] results, PrintStream stream) {
      stream.printf("k,alpha,species,");
      LinkedList<SpeciesResult> speciesNames = new LinkedList<SpeciesResult>(results[0][0].getSpeciesResults().values());
      Collections.sort(speciesNames);
      for (SpeciesResult speciesName : speciesNames) {
         stream.printf("%s,", speciesName);
      }
      stream.printf("%s,", "total");
      stream.printf("%s", "attempts");
      stream.printf("\n");
      for (int k = 0; k < results.length; k++) {
         for (int a = 0; a < results[k].length; a++) {
            ExperimentResult thisResult = results[k][a];
            LinkedList<SpeciesResult> perSpecies = new LinkedList<SpeciesResult>(thisResult.getSpeciesResults().values());
            Collections.sort(perSpecies);
            for (SpeciesResult species : perSpecies) {
               ArrayList<Classification> classifications = new ArrayList<Classification>(species.getClassifications().values());
               Collections.sort(classifications);
               stream.printf("%d,%1.3f,%s,", thisResult.getK(), thisResult.getAlpha(), species);
               for (Classification c : classifications) {
                  stream.printf("%d,", c.getNumClassifications());
               }
               stream.printf("%d,", species.getTotal());
               stream.printf("%d", species.getAttempts());
               stream.printf("\n");
            }
            stream.printf("%d,%1.3f,%s,", thisResult.getK(), thisResult.getAlpha(), "Overall");
            for (SpeciesResult species : perSpecies) {
               stream.printf("%d,", species.getNumClassifiedAs());
            }
            stream.printf("%d,", thisResult.getTotal());
            stream.printf("%d", thisResult.getAttempts());
            stream.printf("\n");
         }
      }
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
                           1.0*c.getClassifications() / s.getTotal());
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
      printOption("[-o  | --output]","output to print the resulting formatted data to");
      printOption("<format>","\toutput format");
      System.err.println("Formats:");
      printOption("[-d  | --deprecated]","old version compatible with gnuplot scripts");
      printOption("[-m  | --matrix]","old version compatible with gnuplot scripts");
      System.exit(1);
   }
   public static void printOption(String flag, String explanation){
      System.err.printf("\t%s\t%s\n",flag,explanation);
   }
}
