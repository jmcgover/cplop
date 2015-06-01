import java.io.*;

public class Thing<T> 
   implements Countable, Comparable<Thing<T>>, Serializable{
   private T thing;
   private int count;
   public Thing(T thing){
      this.thing = thing;
      this.count = 1;
   }
   public T getThing(){
      return thing;
   }
   public int getCount(){
      return count;
   }
   public int increment(){
      return ++count;
   }
   public int compareTo(Thing<T> other){
      return -1*(this.count - other.count);
   }
   public String toString(){
      return thing.toString() + " " + count;
   }
}
