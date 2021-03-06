
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class SearchServlet
 */
@WebServlet("/Search")
public class Search extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Search() {
        super();
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		HttpSession session = request.getSession();
		
		response.setContentType("text/html"); 
		PrintWriter out = response.getWriter();
		out.println("<HTML>"); 
		out.println("<BODY>"); 
		out.println("<CENTER>");
		
		boolean isError = false;
		
		Integer cindate = 0;
		Integer cinmonth = 0;
		Integer cinyear = 0;
		
		Integer coutdate = 0;
		Integer coutmonth = 0;
		Integer coutyear = 0;
		
		Integer city = 0;
		
		Integer numRooms = 0;
		
		Integer maxPrice = 0;
		
		BookingRequest b;
		
		long cInToMS = 0;
		long cOutToMS = 0;
		
		try {
			cindate = Integer.parseInt(request.getParameter("cindate"));
			cinmonth = Integer.parseInt(request.getParameter("cinmonth"));
			cinyear = Integer.parseInt(request.getParameter("cinyear"));
			
			coutdate = Integer.parseInt(request.getParameter("coutdate"));
			coutmonth = Integer.parseInt(request.getParameter("coutmonth"));
			coutyear = Integer.parseInt(request.getParameter("coutyear"));
			
			city = Integer.parseInt(request.getParameter("city"));
			
			numRooms = Integer.parseInt(request.getParameter("numRooms"));
			
			maxPrice = Integer.parseInt(request.getParameter("maxPrice"));
			
			Calendar cIn = Calendar.getInstance();
			cIn.set(cinyear, (cinmonth-1), cindate,0,0,0);
			cInToMS = cIn.getTimeInMillis(); 
			Calendar cOut = Calendar.getInstance();
			cOut.set(coutyear, (coutmonth-1), coutdate,0,0,0);
			cOutToMS = cOut.getTimeInMillis();
			
			if (cInToMS > Calendar.getInstance().getTimeInMillis()
					&& numRooms > 0 && maxPrice > 0){}
			else {
				isError = true;
			}
			
			if (cOut.getTimeInMillis() < cIn.getTimeInMillis()) isError = true;
			
		} catch (Exception e) {
			b = (BookingRequest) session.getAttribute("BookingReq");
			maxPrice = (Integer) session.getAttribute("maxPrice");
			if (b == null || maxPrice == null) {
				isError = true;
			} else {
				cInToMS = b.getCheckIn();
				cOutToMS = b.getCheckOut();
				city = b.getCity();
				numRooms = b.getNumRooms();
			}
		}
		
		if (isError) {
			session.setAttribute("isError", true);
			response.sendRedirect("/Assignment2/ConsumerPage");
			return;
		}
		
		boolean foundOption = false; 
		
		out.println("<H1>Search results for query</H1><br/>"); 
		
		SearchRes sr = new SearchRes(cInToMS,cOutToMS, city, maxPrice);
		sr.getSearchResults();
		
		//just in case booking is requested...
		b = new BookingRequest(cInToMS, cOutToMS, city);
		session.setAttribute("BookingReq", b);
		
		out.println("<b>City: </b> " + city + "<br/>");
		out.println("<b>Number of rooms: </b> " + numRooms + "<br/>");
		out.println("<b>Maximum price per night: </b> "+ maxPrice + "<br/>");
		out.println("<b>Check-in: </b> "+ b.getCheckInToString() + "<br/>");
		out.println("<b>Check-out: </b> "+ b.getCheckOutToString() + "<br/>");
		
		out.println("<form action='Booking' method='POST'><table>");
		
		out.println("<tr>" +
					"<td><b>Type<b></td>" +
					"<td><b>Number of beds</b></td>" +
					"<td><b>Price</b></td>" +
					"<td><b>Number of rooms required</b></td>" +
					"<td><b>Number of rooms remaining</b></td>" +
					"<td><b>Select?</b></td></tr>");
					
		Map<String, Integer> prices = new HashMap<String, Integer>();
		Map<String, Integer> numberOfBeds = new HashMap<String, Integer>();
		String searchQry = "SELECT * FROM ROOMS";
		
		LoadDbDriver();
		Connection con = GetDbConnection();
		try {
			PreparedStatement ps = con.prepareStatement(searchQry);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				prices.put(rs.getString("TYPE").trim(),Integer.parseInt(rs.getString("PRICE")));
				numberOfBeds.put(rs.getString("TYPE").trim(),Integer.parseInt(rs.getString("NUMBEDS")));
			}
			
			for (String type : sr.getRes().keySet()) {
				if (prices.get(type) <= maxPrice) {
					out.println("<tr>");
					out.println("<td>" + type + "</td>" + "<td>" + numberOfBeds.get(type) + "</td>" + 
							"<td>" + prices.get(type) + "</td>"	+ "<td>" + numRooms + "</td>" +	
							"<td>" + sr.getRes().get(type) + "</td><td>");
					if (sr.getRes().get(type) > numRooms) {
						foundOption = true;
						out.println("<input type='radio' name='toBook' id='toBook' value='" + type + ";N;"
						+ numRooms + "' />");
					}

					if (type != "Single") {
						out.println("</td></tr>");
						out.println("<tr>");
						out.println("<td>" + type + "</td>" + "<td>" + (numberOfBeds.get(type) + 1) + "</td>" + 
								"<td>" + (prices.get(type) + 35) + "</td>" + "<td>" + numRooms + "</td>" +
								"<td>" + sr.getRes().get(type) + "</td><td>");
						if (sr.getRes().get(type) > 0) {
							foundOption = true;
							out.println("<input type='radio' name='toBook' id='toBook' value='" + type + ";Y;"
							+ numRooms + "' />");
						}
						out.println("</td></tr>");
					}

					out.println("<tr></tr>");
				}
			}
			
			out.println("</table>");
			out.println("<br/>Note: prices may change upon confirmation to account for peak-hour timing<br/>");
			
			
			if (!foundOption) {
				out.println("<br/>No available rooms. Please try again with a different query. Thank you, and "
						+ "sorry for the inconvenience.<br/>");
			} else {
				out.println("<br><input type='submit'  name='ChoiceSubmit' value='Book!'>");
			}
			
			out.println("</form><br/>");
			
			out.println("<br/><form action='/Assignment2'>" + 
					"<input type='submit' value='Back to Search'></form>");
			
		}  catch (SQLException e) {
			out.println("Cannot connect to database.");
		}
		CloseDbConnection(con);
		
		isError = (boolean) session.getAttribute("isError");
		
		if (isError) {
			out.println("Error in input");
		}

		out.println("</CENTER>");
		out.println("</BODY>"); 
		out.println("</HTML>");
		out.close();
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