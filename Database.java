import java.util.*;
import java.sql.*;

public class Database {
   private static final int DISP_16_23 = 95;
   private static final int DISP_23_5 = 93;
   private Connection conn;
   private String url;
   private String user;
   private String password;
   private HashMap<String, String> replacements;
   private Phylogeny tree;
   public Database(String url, String user, String password){
      try {
         Class.forName("com.mysql.jdbc.Driver");
      }
      catch (ClassNotFoundException e){
         System.err.println("Driver not found...");
         e.printStackTrace();
         System.exit(1);
      } 
      this.url = url;
      this.user= user;
      this.password = password;
      try {
         this.conn = DriverManager.getConnection(url,user,password);
      }
      catch(SQLException e){
         System.err.println("Could not connect...");
         e.printStackTrace();
         System.exit(1);
      }
      replacements = new HashMap<String, String>();
      replacements.put("","EMPTY");
      replacements.put("dog","Dog");
      replacements.put("Human ","human");
      replacements.put("sheep","Sheep");
   }
   // SQL overwrites
   public Connection getConnection(){
      return conn;
   }
   public Statement createStatement() throws SQLException{
      return conn.createStatement();
   }
   public ResultSet executeQuery(String query) throws SQLException{
      return conn.createStatement().executeQuery(query);
   }
   // COLUMN returns
   public String retrieveCommonName(ResultSet result) throws SQLException{
      String commonName = result.getString("commonName").trim();
      if (replacements.containsKey(commonName)) {
         commonName = replacements.get(commonName);
      }
      return commonName;
   }
   public String retrieveHostId(ResultSet result) throws SQLException{
      return result.getString("hostId");
   }

   public String retrieveIsoId(ResultSet result) throws SQLException{
      return result.getString("isoId");
   }
   public String retrievePyroId(ResultSet result) throws SQLException{
      return result.getString("pyroId");
   }
   public String retrieveAppliedRegion(ResultSet result) throws SQLException{
      return result.getString("appliedRegion");
   }
   public int retrieveDispensations(ResultSet result) throws SQLException{
      String appliedRegion = retrieveAppliedRegion(result);
      if (appliedRegion.equals("23-5")) {
         return DISP_23_5;
      }
      if (appliedRegion.equals("16-23")) {
         return DISP_16_23;
      }
      return -1;
   }
}
