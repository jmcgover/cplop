import java.util.*;

public class ListCounter<S,I extends Classified<S>, D extends Comparable<D>> {
   HashMap<S, Count<S>> counts;
   public ListCounter() {
   }
   public Count<S> findMostPlural(List<ListEntry<I,D>> list) {
      ArrayList<Count<S>> sortedList;
      this.counts = new HashMap<S, Count<S>>();

      for (int i = 1; i < list.size(); i++) {
         addCount(list.get(i));
      }


      if (counts.values().size() > 0) {
         sortedList = new ArrayList<Count<S>>(counts.values());
         Collections.sort(sortedList);
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
