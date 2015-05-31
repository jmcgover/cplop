import java.io.*;
import java.util.*;

public class Equivalence implements Serializable{
   private String commonName;
   public HashMap<String, String> equivalencies;

   public Equivalence(String commonName){
      this.commonName = commonName;
      this.equivalencies = new HashMap<String,String>();
      this.equivalencies.put(commonName, commonName);
   }
   public String getCommonName(){
      return this.commonName;
   }
   public boolean isEquivalent(String commonName){
//      System.err.println("Is " + commonName + "in here?");
//      System.err.println("Keys:");
//      for (String name : equivalencies.keySet()) {
//         System.err.println(name);
//      }
//      System.err.println("Values:");
//      for (String name : equivalencies.values()) {
//         System.err.println(name);
//      }

      return equivalencies.containsKey(commonName);
   }
   public void addEquivalence(String commonName){
//      System.err.println("putting " + commonName);
      equivalencies.put(commonName, commonName);
//      System.err.println();
   }
}
