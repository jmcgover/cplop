public abstract class SimilarityMetric<T> implements Comparable<SimilarityMetric>{
   private T original;
   private T other;
   private double similarity;
   public SimilarityMetric(T original, T other){
      this.original = original;
      this.other = other;
      this.similarity = similarity(this.original,this.other);
   }

   public abstract double similarity(T x, T y);

   public double getSimilarity(){
      return this.similarity;
   }
   public T getOriginal(){
      return this.original;
   }
   public T getOther(){
      return this.other;
   }
   public int compareTo(SimilarityMetric other){
      if (this.similarity < other.similarity) {
         return 1;
      }
      if (this.similarity > other.similarity) {
         return -1;
      }
      return 0;
   }
}
