import java.util.*;

public class Isolate extends Host{
   private String isoId;
   private HashMap<String, Pyroprint> pyroprints;

   // CONSTRUCTORS
   public Isolate(String commonName, String hostId, String isoId){
      super(commonName, hostId);
      this.isoId = isoId;
      this.pyroprints = new HashMap<String, Pyroprint>();
   }
   public Isolate(Isolate other){
      super((Isolate)other);
      this.isoId = other.isoId;
      this.pyroprints = other.pyroprints;
   }

   // GETTERS
   public String getIsoId(){
      return this.isoId;
   }
   public HashMap<String, Pyroprint> getPyroprints(){
      return this.pyroprints;
   }

   // Keys
   public String hostKey(){
      return super.key();
   }
   public String key(){
      return String.format("%s,%s",super.key(),isoId);
   }

   // Isolate Handling
   public boolean addPyroprint(Pyroprint pyroprint){
      if (!pyroprints.containsKey(pyroprint.key())) {
         pyroprints.put(pyroprint.key(), pyroprint);
         return true;
      }
      return false;
   }

   // Object method overwriting
   public int hashCode(){
      return key().hashCode();
   }
   public String toString(){
      return this.isoId;
   }
   public boolean equals(Object other){
      return super.equals(other) && this.isoId.equals(((Isolate)other).isoId);
   }
}
