/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.erlundin.ninemenmorris;

import java.sql.*;
import java.util.ArrayList;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

/**
 * JAX-WS application for the server part of the Nine Men Morris app for Android.
 * The server side has a connection to a MySQL database which holds registered players and their scores
 * The database also holds key-values relevant to Google's C2DM service 
 * and device ID's that have registered fro push notifications i.e. C2D messages.
 * @author Erik
 */
@WebService(serviceName = "GameWebService")
public class GameWebService {

    private String db_address = "localhost";
    private String db_name = "nmm";
    private String db_port = "";
    private String db_user = "root";
    private String db_password = "root";

    private void updateEnvironmentVariables() {
        db_address = System.getenv("OPENSHIFT_MYSQL_DB_HOST");
        if (db_address == null) {
            db_address = "localhost";
        }
        db_port = ":" + System.getenv("OPENSHIFT_MYSQL_DB_PORT");
        if (db_port.equals(":null")) {
            db_port = "";
        }
        db_user = System.getenv("OPENSHIFT_MYSQL_DB_USERNAME");
        if (db_user == null) {
            db_user = "root";
        }
        db_password = System.getenv("OPENSHIFT_MYSQL_DB_PASSWORD");
        if (db_password == null) {
            db_password = "root";
        }

    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "trylogin")
    public boolean trylogin(@WebParam(name = "name") String name, @WebParam(name = "password") String password) {
        if (login(name, password) >= 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "updateHighscore")
    public boolean updateHighscore(@WebParam(name = "name") String name, @WebParam(name = "password") String password, @WebParam(name = "win") boolean win) {

        // TODO call login
        int user = login(name, password);
        if (user > -1) {
            Connection conn = null;
            Statement st = null;
            ResultSet rs = null;
            boolean success = false;
            int currentWins = -1, currentLosses = -1;

            try {
                Class.forName("com.mysql.jdbc.Driver").newInstance();
                conn = DriverManager.getConnection("jdbc:mysql://" + db_address + db_port + "/" + db_name, db_user, db_password);

                String selectQuery = "SELECT wins,losses FROM users WHERE name = '" + name + "' AND password = '" + password + "'";

                st = conn.createStatement();
                rs = st.executeQuery(selectQuery);

                //get current number of wins/losses
                if (rs.next()) {

                    currentWins = rs.getInt("wins");
                    currentLosses = rs.getInt("losses");
                }
                //check that db values have been read
                if (currentWins > -1 && currentLosses > -1) {
                    if (win) {
                        currentWins += 1;
                    } else {
                        currentLosses += 1;
                    }
                    String updateQuery = "UPDATE users SET wins = '" + currentWins + "', losses = '" + currentLosses + "' WHERE name = '" + name + "' AND password = '" + password + "'";
                    st = conn.createStatement();
                    int result = st.executeUpdate(updateQuery);
                    if (result > 0) {
                        success = true;

                        //Notify all devices that highscores have been updated
                        //This method is called twice for each finihed game, once for each player.
                        //Therefore the notification only needs to take place in one of the method calls,
                        //an easy way to achieve that is to call notify when win == true, which only be correct for one of the calls.
                        if (win) {
                            notifyUsersAboutNewHighscores();
                        }
                    }
                }

            } catch (ClassNotFoundException ex) {
                System.out.println("ELU ERROR" + ex.getMessage());
            } catch (IllegalAccessException ex) {
                System.out.println("ELU ERROR" + ex.getMessage());
            } catch (InstantiationException ex) {
                System.out.println("ELU ERROR" + ex.getMessage());
            } catch (SQLException ex) {
                System.out.println("ELU ERROR" + ex.getMessage());
            } finally {
                try {
                    if (rs != null) {
                        rs.close();
                    }
                    if (st != null) {
                        st.close();
                    }
                    if (conn != null) {
                        conn.close();
                    }
                } catch (Exception ex) {
                    System.out.println("ELU ERROR" + ex.getMessage());
                }

                return success;
            }
        } else {
            return false;
        } //login failed

    }

    /**
     * Web service operation
     * Get a list of all scores on the server
     */
    @WebMethod(operationName = "getHighscores")
    public Score[] getHighscores(@WebParam(name = "name") String name, @WebParam(name = "password") String password, @WebParam(name = "groupId") int groupId) {

        int user = login(name, password);
        if (user > -1) {
            Connection conn = null;
            Statement st = null;
            ResultSet rs = null;
            //ArrayList<Score> highscores = new ArrayList<Score>();
            Score[] scores = null;
            try {
                Class.forName("com.mysql.jdbc.Driver").newInstance();
                conn = DriverManager.getConnection("jdbc:mysql://" + db_address + db_port + "/" + db_name, db_user, db_password);

                //String query = "SELECT name,wins,losses FROM users";
                String query = "SELECT name, wins, losses FROM users ORDER BY (IF(wins<=0, 1, wins)/IF(losses<=0, 1, losses)) DESC, wins DESC LIMIT 100";
                
                st = conn.createStatement();
                rs = st.executeQuery(query);
                rs.last();
                int rows = rs.getRow();
                scores = new Score[rows];
                rs.beforeFirst();

                int i = 0;
                while (rs.next()) {
                    Score s = new Score(rs.getString("name"), rs.getInt("wins"), rs.getInt("losses"));
                    //highscores.add(s); 
                    scores[i] = s;
                    i++;
                }
            } catch (ClassNotFoundException ex) {
                System.out.println("ELU ERROR" + ex.getMessage());
            } catch (IllegalAccessException ex) {
                System.out.println("ELU ERROR" + ex.getMessage());
            } catch (InstantiationException ex) {
                System.out.println("ELU ERROR" + ex.getMessage());
            } catch (SQLException ex) {
                System.out.println("ELU ERROR" + ex.getMessage());
            } catch (Exception ex) {
                System.out.println("ELU ERROR" + ex.getMessage());
            } finally {
                try {
                    if (rs != null) {
                        rs.close();
                    }
                    if (st != null) {
                        st.close();
                    }
                    if (conn != null) {
                        conn.close();
                    }
                } catch (Exception ex) {
                    System.out.println("ELU ERROR" + ex.getMessage());
                }
                if (scores.length <= 0) {
                    return null;
                } //no scores were read
                return scores;
            }
        } else {
            return null;
        } 
    }

    /**
     * Web service operation
     * Register a user/player in the database. The server will now keep records of the user's wins and losses.
     */
    @WebMethod(operationName = "register")
    public boolean register(@WebParam(name = "name") String name, @WebParam(name = "password") String password) {

        updateEnvironmentVariables();
        
        Connection conn = null;
        Statement st = null;
        ResultSet rs = null;
        boolean success = false;

        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            conn = DriverManager.getConnection("jdbc:mysql://" + db_address + db_port + "/" + db_name, db_user, db_password);

            String selectQuery = "SELECT idUser FROM users WHERE name = '" + name + "'";

            st = conn.createStatement();
            rs = st.executeQuery(selectQuery);

            //if user did not exist already
            if (!rs.next()) {
                String insertQuery = "INSERT INTO users values(default, 0, 0, '" + name + "', '" + password + "')";

                st = conn.createStatement();
                int result = st.executeUpdate(insertQuery);
                if (result > 0) {
                    success = true;
                }
            }
        } catch (ClassNotFoundException ex) {
            System.out.println("ELU ERROR" + ex.getMessage());
        } catch (IllegalAccessException ex) {
            System.out.println("ELU ERROR" + ex.getMessage());
        } catch (InstantiationException ex) {
            System.out.println("ELU ERROR" + ex.getMessage());
        } catch (SQLException ex) {
            System.out.println("ELU ERROR" + ex.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (st != null) {
                    st.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception ex) {
                System.out.println("ELU ERROR" + ex.getMessage());
            }
            return success;
        }
    }

    /**
     * Web service operation
     * Register a device in the database so as to receive push notifications when high-scores have been updated
     */
    @WebMethod(operationName = "regDevice")
    public int regDevice(@WebParam(name = "regId") String regId) {

        updateEnvironmentVariables();
        
        int rowId = -1;
        Connection conn = null;
        Statement st = null;
        ResultSet rs = null;

        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            conn = DriverManager.getConnection("jdbc:mysql://" + db_address + db_port + "/" + db_name, db_user, db_password);

            String selectQuery = "SELECT rowId, active FROM devices WHERE registrationId = '" + regId + "'";

            st = conn.createStatement();
            rs = st.executeQuery(selectQuery);

            //TODO call reactivate device instead, this is redundant
            if (rs.next()) { //if device is registered already, get the row id
                rowId = rs.getInt("rowId");
                boolean active = rs.getBoolean("active");
                //set to active if it was deactivated
                if (!active) {
                    String updateQuery = "UPDATE devices SET active = 1 WHERE rowId = '" + rowId + "'";
                    st = conn.createStatement();
                    int result = st.executeUpdate(updateQuery);
                    if (result < 1) {
                        rowId = -2;
                    }
                }
            } else { //if device was not registered, do register it and retrieve the row id upon insertion.
                String insertQuery = "INSERT INTO devices values(default, '" + regId + "', default)";

                st = conn.createStatement();
                int result = st.executeUpdate(insertQuery);
                if (result > 0) {
                    st = conn.createStatement();
                    rs = st.executeQuery(selectQuery);
                    rs.next();
                    rowId = rs.getInt("rowId");
                }
            }
        } catch (ClassNotFoundException ex) {
            System.out.println("ELU ERROR" + ex.getMessage());
        } catch (IllegalAccessException ex) {
            System.out.println("ELU ERROR" + ex.getMessage());
        } catch (InstantiationException ex) {
            System.out.println("ELU ERROR" + ex.getMessage());
        } catch (SQLException ex) {
            System.out.println("ELU ERROR" + ex.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (st != null) {
                    st.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception ex) {
                System.out.println("ELU ERROR" + ex.getMessage());
            }

            return rowId;
        }
    }

    /**
     * Web service operation When user has registered the device already and just needs to set it to active in the database
     */
    @WebMethod(operationName = "reactivateRegDevice")
    public boolean reactivateRegDevice(@WebParam(name = "rowId") String rowId) {

        updateEnvironmentVariables();

        boolean success = false;
        Connection conn = null;
        Statement st = null;
        ResultSet rs = null;

        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            conn = DriverManager.getConnection("jdbc:mysql://" + db_address + db_port + "/" + db_name, db_user, db_password);


            String selectQuery = "SELECT rowId FROM devices WHERE rowId = '" + rowId + "'";

            st = conn.createStatement();
            rs = st.executeQuery(selectQuery);

            //if device with given rowId exists
            if (rs.next()) {
                String updateQuery = "UPDATE devices SET active = 1 WHERE rowId = '" + rowId + "'";

                st = conn.createStatement();
                int result = st.executeUpdate(updateQuery);
                if (result > 0) {
                    success = true;
                }
            }
        } catch (ClassNotFoundException ex) {
            System.out.println("ELU ERROR" + ex.getMessage());
        } catch (IllegalAccessException ex) {
            System.out.println("ELU ERROR" + ex.getMessage());
        } catch (InstantiationException ex) {
            System.out.println("ELU ERROR" + ex.getMessage());
        } catch (SQLException ex) {
            System.out.println("ELU ERROR" + ex.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (st != null) {
                    st.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception ex) {
                System.out.println("ELU ERROR" + ex.getMessage());
            }

            return success;
        }
    }

    /**
     * Web service operation User wants to unregister device for cloud messaging, 'active' is set to 'false' in db
     */
    @WebMethod(operationName = "unRegDevice")
    public boolean unRegDevice(@WebParam(name = "rowId") String rowId) {

        updateEnvironmentVariables();

        boolean success = false;
        Connection conn = null;
        Statement st = null;
        ResultSet rs = null;

        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            conn = DriverManager.getConnection("jdbc:mysql://" + db_address + db_port + "/" + db_name, db_user, db_password);

            //check if it exists in db? don't know why else this would be here
            String selectQuery = "SELECT rowId FROM devices WHERE rowId = '" + rowId + "'";

            st = conn.createStatement();
            rs = st.executeQuery(selectQuery);

            //if device with given rowId exists
            if (rs.next()) {
                String updateQuery = "UPDATE devices SET active = 0 WHERE rowId = '" + rowId + "'";

                st = conn.createStatement();
                int result = st.executeUpdate(updateQuery);
                if (result > 0) {
                    success = true;
                }
            }
        } catch (ClassNotFoundException ex) {
            System.out.println("ELU ERROR" + ex.getMessage());
        } catch (IllegalAccessException ex) {
            System.out.println("ELU ERROR" + ex.getMessage());
        } catch (InstantiationException ex) {
            System.out.println("ELU ERROR" + ex.getMessage());
        } catch (SQLException ex) {
            System.out.println("ELU ERROR" + ex.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (st != null) {
                    st.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception ex) {
                System.out.println("ELU ERROR" + ex.getMessage());
            }

            return success;
        }
    }

    public ArrayList<String> getAllRegistrationIds() {

        updateEnvironmentVariables();

        Connection conn = null;
        Statement st = null;
        ResultSet rs = null;
        ArrayList<String> ids = new ArrayList<String>();

        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            conn = DriverManager.getConnection("jdbc:mysql://" + db_address + db_port + "/" + db_name, db_user, db_password);

            String query = "SELECT registrationId FROM devices WHERE active = true";

            st = conn.createStatement();
            rs = st.executeQuery(query);

            int i = 0;
            while (rs.next()) {
                ids.add(rs.getString("registrationId"));
                i++;
            }
        } catch (ClassNotFoundException ex) {
            System.out.println("ELU ERROR" + ex.getMessage());
        } catch (IllegalAccessException ex) {
            System.out.println("ELU ERROR" + ex.getMessage());
        } catch (InstantiationException ex) {
            System.out.println("ELU ERROR" + ex.getMessage());
        } catch (SQLException ex) {
            System.out.println("ELU ERROR" + ex.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (st != null) {
                    st.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception ex) {
                System.out.println("ELU ERROR" + ex.getMessage());
            }
            if (ids.size() <= 0) {
                return null;
            }
            return ids;
        }
    }

    private void notifyUsersAboutNewHighscores() {

        ArrayList<String> devices = getAllRegistrationIds();
        String auth_token = ServerConfiguration.getAuthToken();
        //prevent infinity loops if auth_token never is correct
        int failCount = 0;
        for (int i = 0; i < devices.size(); i++) {
            try {
                int responseCode = MessageUtil.sendMessage(ServerConfiguration.getAuthToken(), devices.get(i), "HIGHSCORES");
                if (responseCode != MessageUtil.RESPONSE_OK) {
                    ServerConfiguration.updateAuthToken();
                    ServerConfiguration.getAuthToken();
                    i--;
                    //prevent infinity loops
                    failCount++;
                    if (failCount > 5) {
                        System.out.println("ELU ERROR with auth token @notifyUsersAboutNewHighscores, failcount > 5");
                        System.out.println(responseCode + "-> " + ServerConfiguration.getAuthToken());
                        i++;
                        if (failCount > 30) {
                            System.out.println("ELU ERROR with auth token @notifyUsersAboutNewHighscores, failcount > 30");
                            break;
                        }
                    }

                }
            } catch (Exception e) {
                System.out.println("ELU ERROR: Failed to send c2dm message to device with id: " + devices.get(i) + "\nError message: " + e.getMessage() + "\n-----");
            }
        }
    }

    private int login(String name, String password) {

        updateEnvironmentVariables();

        //global login credentials for downloading highscores. 
        if (name.equalsIgnoreCase("default") && password.equalsIgnoreCase("default")) {
            return 1337;
        }

        Connection conn = null;
        Statement st = null;
        ResultSet rs = null;
        int user = -1;

        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            conn = DriverManager.getConnection("jdbc:mysql://" + db_address + db_port + "/" + db_name, db_user, db_password);

            String query = "SELECT idUser FROM users WHERE name = '" + name + "' AND password = '" + password + "'";

            st = conn.createStatement();
            rs = st.executeQuery(query);

            if (rs.next()) {
                user = rs.getInt("idUser");
            }

        } catch (ClassNotFoundException ex) {
            System.out.println("ELU ERROR" + ex.getMessage());
        } catch (IllegalAccessException ex) {
            System.out.println("ELU ERROR" + ex.getMessage());
        } catch (InstantiationException ex) {
            System.out.println("ELU ERROR" + ex.getMessage());
        } catch (SQLException ex) {
            System.out.println("ELU ERROR" + ex.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (st != null) {
                    st.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception ex) {
                System.out.println("ELU ERROR" + ex.getMessage());
            }
            return user;
        }
    }
}
