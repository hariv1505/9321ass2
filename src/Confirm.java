

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class ConfirmServlet
 */
@WebServlet("/ConfirmServlet")
public class Confirm extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Confirm() {
        super();
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("<HTML>"); 
		out.println("<BODY>"); 
		out.println("<CENTER>"); 
		
		HttpSession session = request.getSession(true);
		boolean isError = false;
		
		BookingRequest b = (BookingRequest) session.getAttribute("BookingReq");
		long cardNum = 0;
		String sCardNum = request.getParameter("cardnum");
		String emailAdd = request.getParameter("email");
		String firstN = request.getParameter("first");
		String lastN = request.getParameter("last");
		
		if (sCardNum.matches("[0-9]+")) {
			cardNum = Long.parseLong(sCardNum);
		} else {
			isError = true;
		}
		
		if (!(firstN.matches("[a-zA-Z]+") && lastN.matches("[a-zA-Z]+"))) {
			isError = true;
		}
		
		if (isError) {
			request.setAttribute("isError", true);
			response.sendRedirect("/Booking");
			return;
		}
		
		int pin = 0;
		for (int i = 0; i < 8; i++) {
			int digit = (int) (Math.random()*10);
			pin = 10*pin + digit;
		}
		
		String searchPersonQry = "SELECT COUNT(*) AS COUNT FROM CUSTOMERS " +
				"WHERE EMAIL = '" + emailAdd + "'";
		LoadDbDriver();
		Connection con = GetDbConnection();
		PreparedStatement ps;
		ResultSet rs;
		try {
			ps = con.prepareStatement(searchPersonQry);
			rs = ps.executeQuery();
			if (rs.next()) {
				int count = Integer.parseInt(rs.getString("COUNT").trim());
				
				if (count == 0) {
					String addPersonQry = "INSERT INTO CUSTOMERS VALUES " +
							"('" + emailAdd + "','" + firstN + "','" + lastN + "')";
					ps = con.prepareStatement(addPersonQry);
					ps.executeUpdate();
				}
			}
		} catch (SQLException e) {
			out.println("Cannot add user.");
			out.println("</CENTER>");
			out.println("</BODY>"); 
			out.println("</HTML>");
			out.close();
			return;
		}
				
		try {
			String roomQry = "SELECT r.ID from ROOMS r LEFT JOIN BOOKINGS b on r.ID = b.ROOMID "
					+ "WHERE R.TYPE = '" + b.getType() + "' AND b.ROOMID IS NULL";
			ps = con.prepareStatement(roomQry);
			rs = ps.executeQuery();
			rs.next();
			int roomID = Integer.parseInt(rs.getString("ID").trim());
			String insertBookingQry =  "INSERT INTO BOOKINGS(CHECKIN, CHECKOUT,CUSTID,CITYID,ROOMID,EXTRABED,CARDNUM,"
					+ "PIN) " + "VALUES (" + b.getCheckIn() + "," + b.getCheckOut() + ",'" +
					emailAdd + "'," + b.getCity() + "," + roomID + "," + 
					b.isExtraBed() + "," + cardNum + "," + pin + ")";
			ps = con.prepareStatement(insertBookingQry);
			ps.executeUpdate();
			
			String getBookingQry = "SELECT ID FROM BOOKINGS " + 
					"WHERE CHECKIN = " + b.getCheckIn() + " AND CHECKOUT = " + b.getCheckOut() + 
					" AND CUSTID = '" + emailAdd + "' AND CITYID = " + b.getCity() + 
					" AND ROOMID = " + roomID + " AND EXTRABED = " + b.isExtraBed() +
					" AND CARDNUM = " + cardNum + " AND PIN = " + pin ;
			System.out.println(getBookingQry);

			ps = con.prepareStatement(getBookingQry);
			rs = ps.executeQuery();
			rs.next();
			int bookingID = Integer.parseInt(rs.getString("ID"));
			
			out.println("<h1>Congratulations on your booking!</h1>");
	
			out.println("<br/><b>First Name:</b> " + firstN + "<br/>");
			out.println("<br/><b>Last Name:</b> " + lastN + "<br/>");
			out.println("<br/><b>Email Address (username):</b> " + emailAdd + "<br/>");
			out.println("<br/><b>Type:</b> " + b.getType() + "<br/>");
			out.println("<br/><b>City:</b> " + b.getCity() + "<br/>");
			out.println("<b>Number of beds:</b> " + b.getNumBeds() + "<br/>");
			out.println("<b>Number of rooms:</b> " + b.getNumRooms() + "<br/>");
			out.println("<b>Price per night:</b> "+ b.getPricePerNight() + "<br/>");
			out.println("<b>Check-in:</b> "+ b.getCheckInToString() + "<br/>");
			out.println("<b>Check-out:</b> "+ b.getCheckOutToString() + "<br/>");
			out.println("<b>Total Price:</b> <u>"+ b.getTotalPrice() +"</u><br/><br/>");
			out.println("<br/><b>Card Number:</b> " + cardNum + "<br/>");
			
			out.println("Your details can be seen and edited (minimum 48 hours prior to check-in date)"
					+ " at the following URL: EditBooking?bookingID=" + bookingID + "<br/>"); //TODO: fix URL
			out.println("Your PIN is: " + pin + "<br/>");
			out.println("We have sent an e-mail with these details.<br/><br/>");
			
			out.println("<br/><form action='/Assignment2'>" + 
					"<input type='submit' value='Back to Start'></form>");
		} catch (SQLException e) {
			out.println("Cannot add booking.");
			return;
		} finally {	
			out.println("</CENTER>");
			out.println("</BODY>"); 
			out.println("</HTML>");
			out.close();
			CloseDbConnection(con);
		}
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
