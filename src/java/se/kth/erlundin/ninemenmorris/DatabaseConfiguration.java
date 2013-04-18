/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.erlundin.ninemenmorris;

/**
 *Not used yet
 * put creation of connection string in one place. It would be even better to return a connection instead of a string
 * @author Erik
 */
public class DatabaseConfiguration {
    public static String getConnectionString(){
        StringBuilder sb = new StringBuilder("jdbc:mysql://");
        
        String host = System.getenv("OPENSHIFT_MYSQL_DB_HOST");
        String port = System.getenv("OPENSHIFT_MYSQL_DB_HOST");
        
        if(host == null){            
            sb.append("localhost/nmm");
            return sb.toString();
        }       
        
        sb.append(host);
        
        if(port != null){
            sb.append(":");
            sb.append(System.getenv("OPENSHIFT_MYSQL_DB_PORT"));
        }
        
        return sb.toString();
    }
    
    public static String getConnectionUser(){
        String db_user = System.getenv("OPENSHIFT_MYSQL_DB_USERNAME");
        if(db_user == null) {
            db_user = "root";
        }
        return db_user;
    }
    
    public static String getConnectionPassword(){        
        String db_password = System.getenv("OPENSHIFT_MYSQL_DB_PASSWORD");
        if(db_password == null) {
            db_password = "root";
        }
        return db_password;
    }
}
