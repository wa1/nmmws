package se.kth.erlundin.ninemenmorris;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;



public class ServerConfiguration {
        
        public static void updateAuthToken(){            
                
		String token;
                try{
                    token = AuthenticationUtil.getToken("ninemenmorris@gmail.com","nn215NN6");
                    saveAuthToken(token);
                }
                catch(Exception e){
                    System.out.println("Error in ServerConfiguration.java - updateauthtoken(), message: " + e.getMessage());
                }
        }
        
        public static String getAuthToken(){
            String db_address = System.getenv("OPENSHIFT_MYSQL_DB_HOST");
        if(db_address == null) {
            db_address = "localhost";
        }
        String db_port = ":" + System.getenv("OPENSHIFT_MYSQL_DB_PORT");
        if(db_port.equals(":null")) {
            db_port = "";
        }
        String db_user = System.getenv("OPENSHIFT_MYSQL_DB_USERNAME");
        if(db_user == null) {
            db_user = "root";
        }
        String db_password = System.getenv("OPENSHIFT_MYSQL_DB_PASSWORD");
        if(db_password == null) {
            db_password = "root";
        }
        Connection conn = null;
            Statement st = null;
            ResultSet rs = null; 
            String token = null;
        
            try
            {
              Class.forName("com.mysql.jdbc.Driver").newInstance();          
          conn = DriverManager.getConnection("jdbc:mysql://" + db_address + db_port + "/nmm", db_user, db_password); 
                String query = "SELECT `value` FROM `keys` WHERE `key` = 'AUTHENTICATION_TOKEN'";
                st = conn.createStatement();
                rs = st.executeQuery(query);
                if(rs.next()){
                    token = rs.getString("value"); 
                }
            }
            catch (ClassNotFoundException ex) {System.out.println("ELU classnotfound ERROR in getAuthToken: " + ex.getMessage());}
            catch (IllegalAccessException ex) {System.out.println("ELU illegalaccess ERROR in getAuthToken: " + ex.getMessage());}
            catch (InstantiationException ex) {System.out.println("ELU instantiation ERROR in getAuthToken: " + ex.getMessage());}
            catch (SQLException ex)           {System.out.println("ELU sql ERROR in getAuthToken: " + ex.getMessage());}
            finally{
                try{
                    if (rs != null) {
                        rs.close();
                    }
                    if (st != null) {
                        st.close();
                    }
                    if(conn != null){
                        conn.close();
                    }
                }
                catch(Exception ex){System.out.println("ELU close() ERROR in getAuthToken: " + ex.getMessage());}
                return token;
            }
        }
        
        public static boolean saveAuthToken(String _token){
            
            String db_address = System.getenv("OPENSHIFT_MYSQL_DB_HOST");
        if(db_address == null) {
            db_address = "localhost";
        }
        String db_port = ":" + System.getenv("OPENSHIFT_MYSQL_DB_PORT");
        if(db_port.equals(":null")) {
            db_port = "";
        }
        String db_user = System.getenv("OPENSHIFT_MYSQL_DB_USERNAME");
        if(db_user == null) {
            db_user = "root";
        }
        String db_password = System.getenv("OPENSHIFT_MYSQL_DB_PASSWORD");
        if(db_password == null) {
            db_password = "root";
        }
        Connection conn = null;
            Statement st = null;
            boolean success = false;

            try
            {
              Class.forName("com.mysql.jdbc.Driver").newInstance();          
          conn = DriverManager.getConnection("jdbc:mysql://" + db_address + db_port + "/nmm", db_user, db_password);
                    String updateQuery = "UPDATE `keys` SET `value` = '" + _token + "' WHERE `key` = 'AUTHENTICATION_TOKEN'";
                    st = conn.createStatement();
                    int result = st.executeUpdate(updateQuery);
                    if(result > 0){
                        success = true;     
                    }

            }
            catch (ClassNotFoundException ex) {System.out.println("ELU ERROR" + ex.getMessage());}
            catch (IllegalAccessException ex) {System.out.println("ELU ERROR" + ex.getMessage());}
            catch (InstantiationException ex) {System.out.println("ELU ERROR" + ex.getMessage());}
            catch (SQLException ex)           {System.out.println("ELU ERROR" + ex.getMessage());}
            finally{
                    try{
                        if(conn != null){
                            conn.close();
                        }
                    }
                    catch(Exception ex){System.out.println("ELU ERROR" + ex.getMessage());}           
                
            }          
            return success;
        }
}