import java.io.*;

public interface ExperimentPrinter{
   public String getCsvHeader();
   public void printToCsv();
   public void printToCsv(PrintStream stream);
}
