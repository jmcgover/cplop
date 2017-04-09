import java.util.*;

public class SetCounter<S,I extends Classified<S>, D extends Comparable<D>> {
   HashMap<S, Count<S>> counts;
   public SetCounter() {
   }
   public Count<S> findMostPlural(Set<ListEntry<I,D>> set) {
      ArrayList<Count<S>> sortedList;
      this.counts = new HashMap<S, Count<S>>();

      for (ListEntry<I,D> entry : set) {
         addCount(entry);
      }

      if (this.counts.values().size() > 0) {
         sortedList = new ArrayList<Count<S>>(this.counts.values());
         Collections.sort(sortedList);
         if (sortedList.get(0).getCount() == 1) {
             for (Count<S> count : sortedList) {
                 if (count != sortedList.get(0) && count.getCount() == sortedList.get(0).getCount()) {
                     return null;
                 }
             }
         }
         return sortedList.get(0);
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

}
