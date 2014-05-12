

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

import HelperClass.DatabaseHandler2;
import HelperClass.Init;

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
		Integer pin = null;
		if (request.getParameter("type") == null)
			pin = Integer.parseInt(request.getParameter("PIN"));
		
		PrintWriter out = response.getWriter();
		out.println("<HTML>"); 
		out.println("<BODY>"); 
		out.println("<CENTER>");
		
		String qry = "SELECT * FROM BOOKINGS "
				+ "WHERE ID = " + bookingID;
		
		PreparedStatement ps;
		ResultSet rs;
		try {
			DatabaseHandler2 dh = new DatabaseHandler2();
			Connection con =dh.GetDbConnection();
			ps = con.prepareStatement(qry);
			rs = ps.executeQuery();
			rs.next();
			if (pin != null) {
				Integer savedPin = Integer.parseInt(rs.getString("PIN").trim());
				if (pin.equals(savedPin)) {
					Long checkIn = Long.parseLong(rs.getString("CHECKIN").trim());
					Long offset = (long)2*24*60*60*1000;
					if (Calendar.getInstance().getTimeInMillis() < checkIn + offset) {
						if (request.getParameter("type") == null) {
							out.println("<form name='AddForm' id='AddForm' action='EditBooking' method='post'>");
							out.println("<select name='type'>");
							out.println("<option value='Single'>Single</option>");
							out.println("<option value='Queen'>Queen</option>");
							out.println("<option value='Twin Bed'>Twin Bed</option>");
							out.println("<option value='Suite'>Suite</option>");
							out.println("<option value='Executive'>Executive</option>");
							out.println("</select>");
							out.println("<input type='submit' value='Add room' />");
							out.println("</form>");
						}
					} else {
						out.println("<h1>Sorry, you can only edit the page 48 hours prior to booking start.</h1>");
					}
				}
				dh.CloseDbConnection(con);
			} else if (pin == null && request.getParameter("type") != null) {
				SearchRes sr = new SearchRes(rs.getLong("CHECKIN"), rs.getLong("CHECKOUT"),
						rs.getInt("CITYID"));
				sr.getSearchResults();
				if (!sr.getRes().keySet().contains(request.getParameter("type"))) {
					out.println("Room does not exist. Please log in again.");
				} else {
					if (sr.getRes().get(request.getParameter("type")) > 0) {
						String allRoomsQry = "SELECT * FROM ROOMTYPES";
						con = dh.GetDbConnection();
						ps = con.prepareStatement(allRoomsQry);
						ResultSet rsAll = ps.executeQuery();
						rsAll.next();
						BookingRequest newB = new BookingRequest(false,rsAll.getInt("NUMBEDS"),1,rsAll.getInt("PRICE"),
								rs.getLong("CHECKIN"),rs.getLong("CHECKOUT"), rs.getInt("CITYID"), request.getParameter("type"));
						String custQuery = "SELECT * FROM CUSTOMERS WHERE EMAIL = '" + rs.getString("CUSTID") + "'";
						ps = con.prepareStatement(custQuery);
						ResultSet rsCust = ps.executeQuery();
						rsCust.next();
						
						String citiesQry = "SELECT * FROM CITIES";
						con = dh.GetDbConnection();
						ps = con.prepareStatement(citiesQry);
						ResultSet rsCities = ps.executeQuery();
						rsCities.next();
						
						out.println("<h1>New booking includes:</h1>");
						out.println("<b>Email:</b> " + rsCust.getString("EMAIL").trim() + "<br/>");
						out.println("<b>First name:</b> " + rsCust.getString("FIRSTNAME").trim() + "<br/>");
						out.println("<b>Last name:</b> " + rsCust.getString("LASTNAME").trim() + "<br/>");
						out.println("<b>New room:</b> " + request.getParameter("type") + "<br/>");
						out.println("<b>Checkin Time:</b> " + newB.getCheckInToString() + "<br/>");
						out.println("<b>Checkout Time:</b> " + newB.getCheckOutToString() + "<br/>");
						out.println("<b>Card to use</b>: " + rs.getLong("CARDNUM") + "<br/>");
						out.println("<b>City:</b>" + rsCities.getString("CITY") + "<br/><br/>");
						out.printf("<b>Regular total:</b> $%.2f<br/>", (newB.getTotalPrice() - newB.getPeakPrem() - newB.getDiscount()));
						out.printf("<b>Peak total:</b> $%.2f<br/>",newB.getPeakPrem());
						out.printf("<b>Discount total:</b> $%.2f<br/>", newB.getDiscount());
						out.printf("<b>Total Price:</b> <u>$%.2f</u><br/><br/>", newB.getTotalPrice());
						out.printf("<b>Total overall price:</b> <u>$%.2f</u><br/><br/>", newB.getTotalPrice() + rs.getInt("FAIR"));
						
						out.println("<form action='ConfirmServlet' method='post'>");
						out.println("<input type='submit' value='Accept'/>");
						out.println("<input type='hidden' name='cardnum' value='" + rs.getLong("CARDNUM") + "'/>");
						out.println("<input type='hidden' name='first' value='" + rsCust.getString("FIRSTNAME").trim() + "'/>");
						out.println("<input type='hidden' name='last' value='" + rsCust.getString("LASTNAME").trim() + "'/>");
						out.println("<input type='hidden' name='email' value='" + rsCust.getString("EMAIL").trim() + "'/>");
						session.setAttribute("BookingReq", newB);
						System.out.println(newB.getCheckIn());
						System.out.println(newB.getCheckOut());
						out.println("</form>");
						out.println("<form action='ConsumerPage'>");
						out.println("<input type='submit' value='Cancel'/>");
						out.println("</form>");
						//show updated stuff
					} else {
						for (String i : sr.getRes().keySet())
							out.println(i + sr.getRes().get(i));
						out.println("<form name='nomorerooms' action='ConsumerPage'>");
						out.println("<p>There are no more rooms left</p>");
						out.println("<input type='submit' name='cancelledBooking' value='Delete booking' />");
						out.println("<input type='submit' name='cancelledBooking' value='Keep booking' />");
						out.println("</form>");
					}
				}
			} else {
				out.println("<form method='post' action=EditBooking>");
				out.println("<label for='PIN'>PIN</label><input type='number' name='PIN' />");
				out.println("</form>");
				out.println("<br/><br/>Incorrect password");
			}
			dh.CloseDbConnection(con);
		} catch (SQLException | InstantiationException | IllegalAccessException e) {
			out.println("Booking does not exist. Please log in again.");
			out.println("<br/><form action='/group32'>" + 
					"<input type='submit' value='Back to Search'></form>");
			e.printStackTrace();
		} finally {
			out.println("</CENTER>");
			out.println("</BODY>"); 
			out.println("</HTML>");
			out.close();
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
		
        Connection conn = null;

        // Start the database and set up users, then close database
        try {
        	String dbName=Init.getWebInfPath()+ "/9321ass2";
            String connectionURL = "jdbc:derby:"+dbName;

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
