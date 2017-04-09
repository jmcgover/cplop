public class Count<S> implements Comparable<Count<S>>{
   private S data;
   private int positionSum;
   private int count;
   private Double average;
   public Count(S data, int position) {
      this.data = data;
      this.positionSum = position;
      this.count = 1;
      this.average = 0.0;
   }
   public int add(int position) {
      this.positionSum += position;
      this.average = (double)positionSum / ++this.count;
      return this.count;
   }
   public S getData() {
      return this.data;
   }
   public int getCount() {
      return this.count;
   }
   public int hashCode() {
      return this.data.hashCode();
   }
   public int compareTo(Count<S> other) {
      if (this.count == other.count) {
         return this.average.compareTo(other.average);
      }
      return -1* (this.count - other.count);
   }
   public String toString() {
      return String.format("%s(%d)(%f)", this.data, this.count, this.average);
   }
}
