public class IsolateCorrelation extends SimilarityMetric<Isolate>{
   private PearsonCorrelation sim1623;
   private PearsonCorrelation sim235;
   public IsolateCorrelation(Isolate original, Isolate other){
      super(original, other);

      if (original.get1623Rep() != null && other.get1623Rep() != null) {
         this.sim1623 = new PearsonCorrelation(original.get1623Rep(), other.get1623Rep());
      }
      else {
         this.sim1623 = null;
      }
      if (original.get235Rep() != null && other.get235Rep() != null) {
         this.sim235  = new PearsonCorrelation(original.get235Rep(), other.get235Rep());
      }
      else {
         this.sim235  = null;
      }
   }

   public PearsonCorrelation get1623() {
      return this.sim1623;
   }

   public PearsonCorrelation get235() {
      return this.sim235;
   }

   public double similarity(Isolate original, Isolate other){
      if (this.sim1623 == null || this.sim235 == null) {
         return 0;
      }
      return Math.sqrt(Math.pow(this.sim1623.getSimilarity(), 2) + Math.pow(this.sim235.getSimilarity(), 2));
   }

}

