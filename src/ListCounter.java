import java.util.*;

public class ListCounter<S,I extends Classified<S>, D extends Comparable<D>> {
   HashMap<S, Count<S>> counts;
   public ListCounter() {
   }
   public S findMostPlural(List<ListEntry<I,D>> list) {
      ArrayList<Count<S>> sortedList;
      this.counts = new HashMap<S, Count<S>>();

      for (int i = 1; i < list.size(); i++) {
         addCount(list.get(i));
      }


      if (counts.values().size() > 0) {
         sortedList = new ArrayList<Count<S>>(counts.values());
         Collections.sort(sortedList);
         return sortedList.get(0).getData();
      }
      return null;
   }

   private void addCount(ListEntry<I,D> entry) {
      S classification = entry.getData().getClassification();

      if (null !=this.counts.get(classification)) {
         this.counts.get(classification).add(entry.getPosition());
      }
      else {
         this.counts.put(classification, new Count<S>(classification, entry.getPosition()));
      }
   }

   private class Count<S> implements Comparable<Count<S>>{
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
      public int hashCode() {
         return this.data.hashCode();
      }
      public int compareTo(Count<S> other) {
         if (this.count == other.count) {
            return -1 * this.average.compareTo(other.average);
         }
         return -1* (this.count - other.count);
      }
      public String toString() {
         return String.format("%s(%d)(%f)", this.data, this.count, this.average);
      }
   }
}
