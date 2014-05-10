

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class EditBooking
 */
@WebServlet("/EditBooking")
public class EditBooking extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public EditBooking() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		
		HttpSession session = request.getSession();
		Integer bookingID = Integer.parseInt(request.getParameter("bookingID"));
		session.setAttribute("bookingID", bookingID);
		
		PrintWriter out = response.getWriter();
		out.println("<HTML>"); 
		out.println("<BODY>"); 
		out.println("<CENTER>");
		
		out.println("<form method='post' action=EditBooking>");
		out.println("<label for='PIN'>PIN</label><input type='number' name='PIN' />");
		out.println("</form>");

		out.println("</CENTER>");
		out.println("</BODY>"); 
		out.println("</HTML>");
		out.close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		HttpSession session = request.getSession(true);
		
		Integer bookingID = (Integer) session.getAttribute("bookingID");
		Integer pin = Integer.parseInt(request.getParameter("PIN"));
		
		PrintWriter out = response.getWriter();
		out.println("<HTML>"); 
		out.println("<BODY>"); 
		out.println("<CENTER>");
		
		String qry = "SELECT * FROM BOOKINGS "
				+ "WHERE ID = " + bookingID;
		
		LoadDbDriver();
		Connection con = GetDbConnection();
		PreparedStatement ps;
		try {
			ps = con.prepareStatement(qry);
			ResultSet rs = ps.executeQuery();
			rs.next();
			Integer savedPin = Integer.parseInt(rs.getString("PIN").trim());
	
			if (pin.equals(savedPin)) {
				Long checkIn = Long.parseLong(rs.getString("CHECKIN").trim());
				Long offset = (long)2*24*60*60*1000;
				if (Calendar.getInstance().getTimeInMillis() < checkIn + offset) {
					if (request.getParameter("type") == null) {
						out.println("<form name='AddForm' action='EditBooking' method='post'>");
						out.println("<input type='text' name='type' id='type' />");
						out.println("<input type='submit' value='Add room' />");
						out.println("</form>");
					} else if (request.getParameter("AddForm") == "Add room") {
						SearchRes sr = new SearchRes(rs.getLong("CHECKIN"), rs.getLong("CHECKOUT"),
								rs.getInt("CITY"));
						sr.getSearchResults();
						if (sr.getRes().get(request.getParameter("type")) > 0) {
							String allRoomsQry = "SELECT * FROM ROOMS";
							ps = con.prepareStatement(allRoomsQry);
							ResultSet rsAll = ps.executeQuery();
							rsAll.next();
							BookingRequest newB = new BookingRequest(false,rsAll.getInt("NUMBEDS"),1,rsAll.getInt("PRICE"),
									rs.getLong("CHECKIN"),rs.getLong("CHECKOUT"), rs.getInt("CITY"), request.getParameter("type"));
							out.println("<h1>New booking includes:</h1>");
							out.println("New room is a " + request.getParameter("type") + "<br/>");
							out.println("Checkin Time is " + newB.getCheckInToString() + "<br/>");
							out.println("Checkout Time is " + newB.getCheckOutToString() + "<br/>");
							String citiesQry = "SELECT * FROM CITIES";
							ps = con.prepareStatement(citiesQry);
							ResultSet rsCities = ps.executeQuery();
							rsCities.next();
							out.println(rsCities.getString("CITY"));
							//show updated stuff
						} else {
							out.println("<form name='nomorerooms' action='ConsumerPage' method='post'>");
							out.println("<p>There are no more rooms left</p>");
							out.println("<input type='submit' value='Delete booking' />");
							out.println("<input type='submit' value='Keep booking' />");
							out.println("</form>");
						}
					}
				} else {
					out.println("<h1>Sorry, you can only edit the page 48 hours prior to booking start.</h1>");
				}
			} else {
				out.println("<form method='post' action=EditBooking>");
				out.println("<label for='PIN'>PIN</label><input type='number' name='PIN' />");
				out.println("</form>");
				out.println("<br/><br/>Incorrect password");
			}
		} catch (SQLException e) {
			out.println("Booking does not exist.");
		} finally {
			out.println("</CENTER>");
			out.println("</BODY>"); 
			out.println("</HTML>");
			out.close();
			CloseDbConnection(con);
		}
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
