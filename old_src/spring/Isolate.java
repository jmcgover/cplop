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
      this.pyroprints = other.pyroprints;
      this.rep1623Pyro = null;
      this.rep235Pyro = null;
   }

   // GETTERS
   public String getIsoId(){
      return this.isoId;
   }
   public HashMap<String, Pyroprint> getPyroprints(){
      return this.pyroprints;
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

   // Isolate Handling
   public boolean addPyroprint(Pyroprint pyroprint){
      if (!pyroprints.containsKey(pyroprint.key())) {
         pyroprints.put(pyroprint.key(), pyroprint);
         return true;
      }
      return false;
   }

   /*Create Representative pyroprints*/
   Pyroprint get1623Rep(){
      ArrayList<Pyroprint> pyroprints = new ArrayList<Pyroprint>();
      double[] repPHeights = new double[DISP_16_23 + 1];
      int numberOfPyros = -1;
      
      if (this.rep1623Pyro == null) {

         /*Gather all pyroprints of 16-23 region.*/
         for (Pyroprint p : this.pyroprints.values()) {
            if (!p.isErroneous() && p.getAppliedRegion().equals("16-23")) {
               pyroprints.add(p);
            }
         }

         /*If we don't have pyroprints here, then do nothing.*/
         if (pyroprints.size() == 0) {
            return null;
         }

         repPHeights = getAveragePHeights(pyroprints, DISP_16_23 + 1);

         /*Create new pyroprint*/
         this.rep1623Pyro = new Pyroprint( this.getCommonName(), this.getHostId(), this.getIsoId(), "REP" + this.getIsoId(), false, "16-23", repPHeights);
      }
      return this.rep1623Pyro;
   }

   Pyroprint get235Rep(){
      ArrayList<Pyroprint> pyroprints = new ArrayList<Pyroprint>();
      double[] repPHeights = new double[DISP_23_5 + 1];
      int numberOfPyros = -1;
      
      /*Gather all pyroprints of 23-5 region.*/
      if (this.rep235Pyro == null) {
         for (Pyroprint p : this.pyroprints.values()) {
            if (!p.isErroneous() && p.getAppliedRegion().equals("23-5")) {
               pyroprints.add(p);
            }
         }

         /*If we don't have pyroprints here, then do nothing.*/
         if (pyroprints.size() == 0) {
            return null;
         }

         repPHeights = getAveragePHeights(pyroprints, DISP_23_5 + 1);

         /*Create new pyroprint*/
         this.rep235Pyro = new Pyroprint( this.getCommonName(), this.getHostId(), this.getIsoId(), "REP" + this.getIsoId(), false, "23-5", repPHeights);
      }
      return this.rep235Pyro;
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

   public double[] getAveragePHeights(List<Pyroprint> pyroprints, int disps) {
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
