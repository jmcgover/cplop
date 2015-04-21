import java.io.*;
import java.util.*;

public class Species implements Comparable<Species>, Serializable{
   private String commonName;
   public Equivalence equivalenceClass;
   private HashMap<String, Host> hosts;

   // CONSTRUCTORS
   public Species(String commonName){
      this.commonName = commonName;
      this.equivalenceClass = new Equivalence(commonName);
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
   public int getGoodPyroprintCount(){
      int count = 0;
      for (Pyroprint p : getPyroprints().values()) {
         if (p.isErroneous() == false) {
            count++;
         }
      }
      return count;
   }

   public int getBadPyroprintCount(){
      int count = 0;
      for (Pyroprint p : getPyroprints().values()) {
         if (p.isErroneous()) {
            count++;
         }
      }
      return count;
   }

   public int getAllPyroprintCount(){
      return getPyroprints().values().size();
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

   //Equivalent Species
   public void addSameSpecies(String otherName){
      equivalenceClass.addEquivalence(otherName);
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
//      System.err.print("Is " + this.commonName + " equal to " + (((Species)other).getCommonName()) + "? ");
//      if (this.equivalenceClass.isEquivalent(((Species)other).getCommonName())){
//         System.err.println("YES");
//      }
//      else {
//         System.err.println("NO");
//      }
      return this.equivalenceClass.isEquivalent(((Species)other).getCommonName());
   }
}
