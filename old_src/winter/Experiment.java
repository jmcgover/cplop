import java.io.*;
import java.util.*;

public interface Experiment<T>{
   public int[] getKValues();
   public double[] getAlphaValues();
   public T[][] getTestAccuracies(); /*Collection of Accuracies*/

   public void runExperiment();
}
