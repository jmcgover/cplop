import java.io.*;
import java.util.*;
import java.sql.*;

public class TreeBuilder{
   private Database database;
   private Phylogeny tree;
   public TreeBuilder(Database database){
      this.database = database;
      this.tree = null;
   }
   public TreeBuilder(TreeBuilder treeBuilder){
      this.database = treeBuilder.database;
      this.tree = treeBuilder.tree;
   }
   private void printError(String msg){
      System.err.println("Error: " + msg);
      System.exit(1);
   }
   public Phylogeny build(){
      Statement allStatement = null;
      Statement pHeightStatement = null;
      try{
      allStatement = database.getConnection().createStatement();
      }
      catch(SQLException e){
         e.printStackTrace();
         printError("all STATEMENT CREATION");
      }
      try{
      pHeightStatement = database.getConnection().createStatement();
      }
      catch(SQLException e){
         e.printStackTrace();
         printError("pHeight STATEMENT CREATION");
      }

      String commonName = "";
      String hostId = "";
      String isoId = "";
      String pyroId = "";
      String appliedRegion = "";
      double[] pHeights = null;
      int position = 0;
      double pHeight = 0.0;

      tree = new Phylogeny();
      String allQuery = "select p.appliedRegion,p.pyroId,p.isoID,i.hostId,i.commonName ";
      allQuery += "from Pyroprints p, Histograms h, Isolates i ";
      allQuery += "where h.pyroID = p.pyroID and i.isoId = p.isoId ";
      allQuery += "group by pyroid ";
      allQuery += "order by commonName, hostId, isoId, pyroId, appliedRegion;";

      ResultSet allResults = null;

      System.err.println("Querying for EVERYTHING...");
      try{
      allResults = database.executeQuery(allQuery);
      }
      catch(SQLException e){
         e.printStackTrace();
         printError("EVERYTHING QUERY");
      }

      System.err.println("Building Phylogeny...");
      String pHeightQuery = "";
      ResultSet pHeightResults = null;
      Species spec = null;
      Host host = null;
      Isolate isol = null;
      Pyroprint pyro = null;
      try{
      if (allResults.first()) {
         do{
            commonName = "";
            hostId = "";
            isoId = "";
            pyroId = "";
            appliedRegion = "";
            try{
            commonName = database.retrieveCommonName(allResults);
            hostId = database.retrieveHostId(allResults);
            isoId = database.retrieveIsoId(allResults);
            pyroId = database.retrievePyroId(allResults);
            appliedRegion = database.retrieveAppliedRegion(allResults);
            pHeights = new double[database.retrieveDispensations(allResults) + 1];
            }
            catch(SQLException e){
               e.printStackTrace();
               System.err.printf("commonName: '%s'\n", commonName);
               System.err.printf("hostId: '%s'\n", hostId);
               System.err.printf("isoId: '%s'\n", isoId);
               System.err.printf("pyroId: '%s'\n", pyroId);
               System.err.printf("appliedRegion: '%s'\n", appliedRegion);
               printError("Attempting to get the above values.");
            }
            pHeightQuery = "";
            pHeightQuery += "select position, pheight, pyroId ";
            pHeightQuery += "from Histograms ";
            pHeightQuery += "where pyroId = '" + allResults.getString("pyroId") + "' ";
            pHeightQuery += "order by position;";

            try{
            pHeightResults = pHeightStatement.executeQuery(pHeightQuery);
            if (pHeightResults.first()) {
               do{
                  position = pHeightResults.getInt("position");
                  pHeight = pHeightResults.getDouble("pHeight");
                  pHeights[position] = pHeight;
               }while(position < pHeights.length-1 && pHeightResults.next());
            }
            else {
               printError("Strangely, there were no pHHeights for " + pyroId + "...");
            }
            }
            catch(SQLException e){
               e.printStackTrace();
               System.err.printf("commonName: '%s'\n", commonName);
               System.err.printf("hostId: '%s'\n", hostId);
               System.err.printf("isoId: '%s'\n", isoId);
               System.err.printf("pyroId: '%s'\n", pyroId);
               System.err.printf("appliedRegion: '%s'\n", appliedRegion);
               printError("pHeight loop");
            }
            spec = new Species(commonName);
            host = new Host(commonName, hostId);
            isol = new Isolate(commonName, hostId, isoId);
            pyro = new Pyroprint(commonName, hostId, isoId, pyroId, appliedRegion, pHeights);
            tree.add(spec);
            tree.add(host);
            tree.add(isol);
            tree.add(pyro);
         }while(allResults.next());
      }
      }// loop try
      catch(SQLException e){
         e.printStackTrace();
         printError("LOOP");
      }
      addEquivalencies();
      return this.tree;
   }
   private void addEquivalencies(){
      System.err.println("Adding equivalencies...");
      addBidirectionalEquivalence("Human","Hu");
      addBidirectionalEquivalence("Human","Hu and Cw");
      addBidirectionalEquivalence("Human","Hu and Dg");
      addBidirectionalEquivalence("Human","Hu Cw and Dg");

      addBidirectionalEquivalence("Dog","Dg");
      addBidirectionalEquivalence("Dog","Cw and Dg");
      addBidirectionalEquivalence("Dog","Hu and Dg");
      addBidirectionalEquivalence("Dog","Hu Cw and Dg");

      addBidirectionalEquivalence("Cow","Cw");
      addBidirectionalEquivalence("Cow","Cw and Dg");
      addBidirectionalEquivalence("Cow","Hu and Cw");
      addBidirectionalEquivalence("Cow","Hu Cw and Dg");

      addBidirectionalEquivalence("Pig","Pig/Swine");
      addBidirectionalEquivalence("Pig/Swine","Wild Pig");
      addBidirectionalEquivalence("Pig","Wild Pig");

      System.err.println("Done.");
   }
   public void addBidirectionalEquivalence(String first, String second){
      tree.getSpecies(first).addSameSpecies(second);
      tree.getSpecies(second).addSameSpecies(first);
   }
   public Phylogeny get(){
      if (this.tree == null) {
         build();
      }
      return this.tree;
   }
}
