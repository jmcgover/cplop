import java.util.*;

public class Isolate extends Host{
   private static final int DISP_16_23 = 95;
   private static final int DISP_23_5 = 93;
   private String isoId;
   private HashMap<String, Pyroprint> pyroprints;
   private Pyroprint rep1623Pyro;
   private Pyroprint rep235Pyro;

   // CONSTRUCTORS
   public Isolate(String commonName, String hostId, String isoId){
      super(commonName, hostId);
      this.isoId = isoId;
      this.pyroprints = new HashMap<String, Pyroprint>();
      this.rep1623Pyro = null;
      this.rep235Pyro = null;
   }
   public Isolate(Isolate other){
      super((Isolate)other);
      this.isoId = other.isoId;
      this.pyroprints = new HashMap<String, Pyroprint>(other.pyroprints);
      this.rep1623Pyro = null;
      this.rep235Pyro = null;
   }

   // GETTERS
   public String getIsoId(){
      return this.isoId;
   }
   public HashMap<String, Pyroprint> getPyroprints(){
      return new HashMap<String, Pyroprint>(this.pyroprints);
   }

   //Environmental Determination
   public boolean isEnvironmental(){
      return this.isoId.substring(0,2).equals("ES");
   }

   // Keys
   public String hostKey(){
      return super.key();
   }
   public String key(){
      /*return String.format("%s,%s",super.key(),isoId);*/
      return this.isoId;
   }

   // Pyroprint Handling
   public boolean addPyroprint(Pyroprint pyroprint){
      if (!pyroprints.containsKey(pyroprint.key())) {
         pyroprints.put(pyroprint.key(), pyroprint);
         return true;
      }
      return false;
   }
   public boolean removePyroprint(Pyroprint pyroprint){
      if (pyroprints.containsKey(pyroprint.key())) {
         pyroprints.remove(pyroprint.key());
         return true;
      }
      return false;
   }

   public int getPyroprintCount(String region) {
      int count = 0;
      for (Pyroprint p : this.pyroprints.values()) {
         if (p.getAppliedRegion().equals(region)) {
            count++;
         }
      }
      return count;
   }
   /*Create Representative Dispensations*/
   public double[] getRepresentative(String region, int disps){
      LinkedList<Pyroprint> relevantPyros = new LinkedList<Pyroprint>();
      for (Pyroprint p : this.pyroprints.values()) {
         if (p.getAppliedRegion().equals(region)) {
            relevantPyros.add(p);
         }
      }
      if (relevantPyros.size() == 0) {
         return null;
      }
      return getArithmeticMean(relevantPyros, disps);
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

   public static double[] getArithmeticMean(List<Pyroprint> pyroprints, int disps) {
      double[] repPHeights = new double[disps];
      int numberOfPyros = -1;

      /*Average the elements of the pyroprints*/
      for (Pyroprint p : pyroprints) {
         double[] pHeights = p.getPHeights();
         for (int i = 0; i < disps; i++) {
            repPHeights[i] += pHeights[i];
         }
      }
      numberOfPyros = pyroprints.size();
      for (int i = 0; i < disps; i++) {
         repPHeights[i] /= numberOfPyros;
      }
      return repPHeights;
   }
}
