import java.io.*;
import java.util.*;

public interface Experiment{
   public int[] getKValues();
   public double[] getAlphaValues();
   public void runExperiment();
}
