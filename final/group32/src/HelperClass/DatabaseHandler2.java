package HelperClass;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseHandler2 {

	public Connection GetDbConnection() throws UnsupportedEncodingException, InstantiationException, IllegalAccessException {
		//
		String driver = "org.apache.derby.jdbc.ClientDriver";
		String dbName=Init.getWebInfPath()+ "/9321ass2";
		//String dbName="C:/9321ass2";
        String connectionURL = "jdbc:derby:"+dbName;
        
        Connection conn = null;

        // Load the driver
        try {
        	Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
            System.out.println(driver + " loaded.");
        } catch (java.lang.ClassNotFoundException e) {
            System.err.print("ClassNotFoundException: ");
            System.err.println(e.getMessage());
            System.out.println("\n Make sure your CLASSPATH variable " +
                "contains %DERBY_HOME%\\lib\\derby.jar (${DERBY_HOME}/lib/derby.jar). \n");
        }

        // Start the database and set up users, then close database
        try {
            System.out.println("Trying to connect to " + connectionURL);
            conn = DriverManager.getConnection(connectionURL);
            System.out.println("Connected to database " + connectionURL);
            return conn;
        }catch(Exception e){
        	System.out.println(e);
        }
        return null;
		//
	}
	
	
	public void CloseDbConnection(Connection conn) {
		try {// shut down the database
            conn.close();
            System.out.println("Closed connection");

            /* In embedded mode, an application should shut down Derby.
               Shutdown throws the XJ015 exception to confirm success. */
            boolean gotSQLExc = false;
            try {
                DriverManager.getConnection("jdbc:derby:;shutdown=true");
            } catch (SQLException se) {
                if ( se.getSQLState().equals("XJ015") ) {
                    gotSQLExc = true;
                }
            }
            if (!gotSQLExc) {
                 System.out.println("Database did not shut down normally");
            } else {
                 System.out.println("Database shut down normally");
            }

            // force garbage collection to unload the EmbeddedDriver
            //  so Derby can be restarted
            System.gc();
        } catch (Throwable e) {
        	System.out.println(e);;
            System.exit(1);
        }
	}

	
}
