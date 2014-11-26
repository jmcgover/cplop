import java.util.*;

public class Host extends Species{
   private String hostId;
   private HashMap<String, Isolate> isolates;

   // CONSTRUCTORS
   public Host(String commonName, String hostId){
      super(commonName);
      this.hostId = hostId;
      this.isolates = new HashMap<String, Isolate>();
   }
   public Host(Host other){
      super((Species)other);
      this.hostId = other.hostId;
      this.isolates = other.isolates;
   }

   // GETTERS
   public String getHostId(){
      return this.hostId;
   }
   public HashMap<String, Isolate> getIsolates(){
      return this.isolates;
   }
   //Keys
   public String speciesKey(){
      return super.key();
   }
   public String key(){
      return String.format("%s,%s",super.key(),hostId);
   }

   // Isolate Handling
   public boolean addIsolate(Isolate isolate){
      if (!isolates.containsKey(isolate.key())) {
         isolates.put(isolate.key(), isolate);
         return true;
      }
      return false;
   }

   // Object method overwriting
   public int hashCode(){
      return key().hashCode();
   }
   public String toString(){
      return this.hostId;
   }
   public boolean equals(Object other){
      return super.equals(other) && this.hostId.equals(((Host)other).hostId);
   }
}
