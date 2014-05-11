


import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
//import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class WelcomeServlet
 */
@WebServlet("/ConsumerPage")
public class Consumer extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Consumer() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException	{ 
    	 
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html"); 
    	PrintWriter out = response.getWriter();
    	
    	HttpSession session = request.getSession(true);
    	
    	if (request.getParameter("cancelledBooking").equals("Delete booking")) {
    		String delQuery = "DELETE FROM BOOKINGS WHERE ID = " + session.getAttribute("bookingID");
    		Connection con = GetDbConnection();
    		try {
	    		PreparedStatement ps = con.prepareStatement(delQuery);
	    		ps.executeUpdate();
    		} catch (SQLException e) {
    			out.println("Could not be deleted - sorry.");
    			e.printStackTrace();
    		}
    	}
    	
    	session.setAttribute("bookingID", null);
    	session.setAttribute("BookingReq", null);
    	if (session.getAttribute("isError") == null) {
    		session.setAttribute("isError", false);
    	}
    	    	
    	out.println("<HTML>");
    	out.println("<BODY>"); 
    	out.println("<CENTER>");
    	out.println("<H1>Search for a room!</H1>"); 
    	
    	out.println("<FORM ACTION='Search' METHOD='POST'>"); 
    	out.println("<label for='checkin'>Check In</label><div name='checkin'><select name='cindate'>");
    	for (int i = 1; i <= 31; i++) {
    		out.println("<option value='" + i + "'>" + i + "</option>");
    	}
    	out.println("</select><select name='cinmonth'>");
    	for (int i = 1; i <= 12; i++) {
    		out.println("<option value='" + i + "'>" + i + "</option>");
    	}
    	out.println("</select><input type='number' name='cinyear' /></div><br/>");
    	out.println("<label for='checkout'>Check out</label><div name='checkout'><select name='coutdate'>");
    	for (int i = 1; i <= 31; i++) {
    		out.println("<option value='" + i + "'>" + i + "</option>");
    	}
    	out.println("</select><select name='coutmonth'>");
    	for (int i = 1; i <= 12; i++) {
    		out.println("<option value='" + i + "'>" + i + "</option>");
    	}
    	out.println("</select><input type='number' name='coutyear' /></div><br/>");
    	out.println("<label for='city'>City</label><select name='city'>");
        out.println("<option value='1'>Sydney</option>");
        out.println("<option value='2'>Brisbane</option>");
        out.println("<option value='3'>Melbourne</option>");
        out.println("<option value='4'>Adelaide</option>");
        out.println("<option value='5'>Hobart</option>");
        out.println("</select><br/><br/>");
        out.println("<label for='numRooms'>Number of Rooms</label><select name='numRooms'>");
        for (int i = 1; i <= 3; i++) {
    		out.println("<option value='" + i + "'>" + i + "</option>");
    	}
        out.println("</select><br/><br/>");
        out.println("<label for='maxPrice'>Max Price ($)</label><input type='number' name='maxPrice'><br/><br/>");
        out.println("<input type='submit' value='Submit'></form>");
    	
    	if ((boolean)session.getAttribute("isError")) {
    		out.println("Wrong input. Start again.");
    	}
    	
    	out.println("</CENTER>");
    	out.println("</BODY>");
    	out.println("</HTML>");
    	out.close();
    	
    	session.setAttribute("isError", false);
    	
	}
	
	public static void LoadDbDriver() {
        // Load the driver
		String driver = "org.apache.derby.jdbc.EmbeddedDriver";
		
        try {
        	Class.forName(driver).newInstance();
            System.out.println(driver + " loaded.");
        } catch (java.lang.ClassNotFoundException e) {
            System.err.print("ClassNotFoundException: ");
            System.err.println(e.getMessage());
            System.out.println("\n Make sure your CLASSPATH variable " +
                "contains %DERBY_HOME%\\lib\\derby.jar (${DERBY_HOME}/lib/derby.jar). \n");
        } catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	public static Connection GetDbConnection() {
		
        String connectionURL = "jdbc:derby:/home/hari/University/7thYear/COMP9321/Labs/Assignment2/WebContent/WEB-INF/9321ass2";
        Connection conn = null;

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
		
	public static void CloseDbConnection(Connection conn) {
		try {// shut down the database
            conn.close();
            System.out.println("Closed connection");

            /* In embedded mode, an application should shut down Derby.
               Shutdown throws the XJ015 exception to confirm success. */
            boolean gotSQLExc = false;
            try {
                DriverManager.getConnection("jdbc:derby:;shutdown=true");
                DriverManager.getConnection("exit");
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
