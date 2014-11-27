public class PearsonCorrelation extends SimilarityMetric<Pyroprint> {
   public PearsonCorrelation(Pyroprint original, Pyroprint other){
      super(original, other);
   }

   public double similarity(Pyroprint original, Pyroprint other){
      if (!original.isSameAppliedRegion(other)) {
         throw new IllegalArgumentException();
      }
      return pearsonCorrelation(original.getPHeights(), other.getPHeights());
   }

   public String toString(){
      Pyroprint pyroprint = super.getOther();
      String returnString = String.format("%s %s %s: %.3f",
            pyroprint.getCommonName(),
            pyroprint.getIsoId(),
            pyroprint.getPyroId(),
            super.getSimilarity());
      return returnString;
   }

   private double pearsonCorrelation(double[] x, double[] y){
      double pearson = 0.0;
      double xE = average(x);
      double yE = average(y);

      double covXY = 0.0;
      double varX = 0.0;
      double varY = 0.0;

      int dispensations = super.getOriginal().getDispensations();

      for (int i = 0; i <= dispensations; i++) {
         covXY += (x[i] - xE)*(y[i] - yE);
         varX += (x[i] - xE)*(x[i] - xE);
         varY += (y[i] - yE)*(y[i] - yE);
      }
      pearson = covXY/(Math.sqrt(varX)*Math.sqrt(varY));
      return pearson;
   }
   private double average(double[] x){
      double sum = 0.0;
      for (double a : x) {
         sum += a;
      }
      return sum / x.length;
   }

   private double stdDev(double[] x){
      return Math.sqrt(covariance(x,x));
   }
   private double variance(double[] x){
      return covariance(x,x);
   }
   private double covariance(double[] x, double[] y){
      double sum = 0.0;
      double xE = average(x);
      double yE = average(y);

      int dispensations = super.getOriginal().getDispensations();
      for (int i = 0; i <= dispensations; i++) {
         sum += (x[i] - xE)*(y[i] - yE);
      }
      return sum / (dispensations + 1);
   }

}
