import java.util.*;

public class ListEntry<D,R extends Comparable<R>> implements Comparable<ListEntry<D,R>>{
   private D data;
   private R value;
   private Integer position;
   public ListEntry(D data, R value, Integer position) {
      this.data = data;
      this.value = value;
      this.position = position;
   }

   public D getData() {
      return this.data;
   }
   public R getValue() {
      return this.value;
   }
   public Integer getPosition(){
      return this.position;
   }

   public void setPosition(Integer position) {
      this.position = position;
   }

   public int compareTo(ListEntry<D,R> other) {
      if (this.position == other.position) {
         return -1 * this.value.compareTo(other.value);
      }
      return this.position - other.position;
   }

   public boolean equals(Object other) {
      ListEntry<?,?> otherList;
      if (other == null)
         return false;
      if (this.getClass() != other.getClass())
         return false;
      else {
         otherList = (ListEntry<?,?>)other;
      }
      if (!this.data.equals(otherList.data))
         return false;
//      if (!this.value.equals(otherList.value))
//         return false;
//      if (!this.position.equals(otherList.position))
//         return false;

      return true;
   }

   public int hashCode() {
      return this.data.hashCode();
   }
   public String toString(){
      return String.format("%d:(%s)%s", this.position, this.value, this.data);
   }
}




