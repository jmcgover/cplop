import java.io.*;
import java.util.*;

public class Species implements Comparable<Species>, Serializable{
   private String commonName;
   private HashMap<String, Host> hosts;

   // CONSTRUCTORS
   public Species(String commonName){
      this.commonName = commonName;
      this.hosts = new HashMap<String, Host>();
   }
   public Species(Species other){
      this.commonName = other.commonName;
      this.hosts = other.hosts;
   }

   // GETTERS
   public String getCommonName(){
      return this.commonName;
   }
   public HashMap<String, Host> getHosts(){
      return this.hosts;
   }
   public HashMap<String, Pyroprint> getPyroprints(){
      HashMap<String, Pyroprint> pyroprints = new HashMap<String, Pyroprint>();
      for (Host h : hosts.values()) {
         for (Isolate i : h.getIsolates().values()) {
            pyroprints.putAll(i.getPyroprints());
         }
      }
      return pyroprints;
   }

   // Key
   public String key(){
      return commonName;
   }

   //Comparator
   public int compareTo(Species other){
      return this.commonName.compareTo(other.commonName);
   }

   public boolean addHost(Host host){
      if (!hosts.containsKey(host.key())) {
         hosts.put(host.key(),host);
         return true;
      }
      return false;
   }
   // Object method overwriting
   public int hashCode(){
      return key().hashCode();
   }
   public String toString(){
      return this.commonName;
   }
   public boolean equals(Object other){
      if (other == null) {
         return false;
      }
      if (this.getClass() != other.getClass()) {
         return false;
      }
      return this.commonName.equals(((Species)other).commonName);
   }
}
