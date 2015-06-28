import java.io.*;

public class AccuracyPrinter{
   private PrintStream stream;
   public AccuracyPrinter(PrintStream stream){
      this.stream = stream;
   }
   public void print(Accuracy acc, int k, double alpha){
      stream.printf("%s,%d,%.3f,%d,%d,%d,%d,%.3f,%.3f\n",
            acc,
            acc.getSuccesses(),
            acc.getFailures(),
            acc.getNonDecisions(),
            acc.getTotal(),
            acc.getSuccessFailureAccuracy(),
            acc.getTotalAccuracy());
   }
}
