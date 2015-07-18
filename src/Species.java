import java.io.*;
import java.util.*;

public class Species implements Comparable<Species>, Serializable, Classified<Species>{
   private String commonName;
   public Equivalence equivalenceClass;
   private HashMap<String, Host> hosts;

   // CONSTRUCTORS
   public Species(String commonName){
      this.commonName = commonName;
      this.equivalenceClass = new Equivalence(commonName);
      this.hosts = new HashMap<String, Host>();
   }
   public Species(String commonName, Equivalence equivalenceClass){
      this.commonName = commonName;
      this.equivalenceClass = new Equivalence(equivalenceClass);
      this.hosts = new HashMap<String, Host>();
   }
   public Species(Species other){
      this.commonName = other.commonName;
      this.equivalenceClass = new Equivalence(other.equivalenceClass);
      this.hosts = new HashMap<String, Host>(other.hosts);
   }

   // GETTERS
   public String getCommonName(){
      return this.commonName;
   }
   public HashMap<String, Host> getHosts(){
      return new HashMap<String, Host>(this.hosts);
   }
   public int getHostCount() {
      return this.hosts.values().size();
   }
   public int getIsolateCount() {
      return getIsolates().values().size();
   }
   public int getPyroprintCount(){
      return getPyroprints().values().size();
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

   public HashMap<String, Isolate> getIsolates(){
      HashMap<String, Isolate> isolates = new HashMap<String, Isolate>();
      for (Host h : hosts.values()) {
         isolates.putAll(h.getIsolates());
      }
      return isolates;
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

   public Species getClassification() {
      return new Species(this.commonName, this.equivalenceClass);
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

   //Host Handling
   public boolean addHost(Host host){
      if (!hosts.containsKey(host.key())) {
         hosts.put(host.key(),host);
         return true;
      }
      return false;
   }
   public boolean removeHost(Host host){
      if (hosts.containsKey(host.key())) {
         hosts.remove(host.key());
         return true;
      }
      return false;
   }
   public boolean removeIsolate(Isolate i){
      Host h = this.hosts.get(i.hostKey());
      if (h != null) {
         return h.removeIsolate(i);
      }
      return false;
   }
   public boolean removePyroprint(Pyroprint p){
      Host h = this.hosts.get(p.hostKey());
      if (h != null) {
         return h.removePyroprint(p);
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
