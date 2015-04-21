import java.io.*;

public interface ExperimentPrinter<T>{
   public String getCsvHeader();
   public void printToCsv();
   public void printToCsv(PrintStream stream);
}
