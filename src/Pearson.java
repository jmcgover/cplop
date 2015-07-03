public class Pearson {

   public static Double correlation(double[] x, double[] y, int size) {
      if (x.length < size || y.length < size) {
         throw new IllegalArgumentException(
               String.format(
                  "Pearson Correlation requires vectors be at least %d big. Received %d and %d.",
                  size,
                  x.length,
                  y.length
                  ));
      }
      Double pearson = 0.0;
      double xE = average(x, size);
      double yE = average(y, size);

      double covXY = 0.0;
      double varX = 0.0;
      double varY = 0.0;

      for (int i = 0; i < x.length; i++) {
         covXY += (x[i] - xE)*(y[i] - yE);
         varX += (x[i] - xE)*(x[i] - xE);
         varY += (y[i] - yE)*(y[i] - yE);
      }

      pearson = covXY/(Math.sqrt(varX)*Math.sqrt(varY));

      return pearson;
   }
   private static double average(double[] x, int size){
      double sum = 0.0;
      for (double a : x) {
         sum += a;
      }
      return sum / size;
   }

   private static double stdDev(double[] x, int size){
      return Math.sqrt(covariance(x,x, size));
   }
   private static double variance(double[] x, int size){
      return covariance(x,x, size);
   }
   private static double covariance(double[] x, double[] y, int size){
      if (x.length < size || y.length < size) {
         throw new IllegalArgumentException(String.format("Covariance requires vectors be at least %d big.",size));
      }
      double sum = 0.0;
      double xE = average(x, size);
      double yE = average(y, size);

      for (int i = 0; i < size; i++) {
         sum += (x[i] - xE)*(y[i] - yE);
      }
      return sum / (size + 1);
   }
}
