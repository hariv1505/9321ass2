

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import HelperClass.DatabaseHandler2;
import HelperClass.Init;
import HelperClass.MailSender;

/**
 * Servlet implementation class ConfirmServlet
 */
@WebServlet("/ConfirmServlet")
public class Confirm extends HttpServlet {
	private static final long serialVersionUID = 1L;
	static Logger logger = Logger.getLogger(Confirm.class.getName());
	PrintWriter out;
       
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
		out = response.getWriter();
		out.println("<HTML>"); 
		out.println("<BODY>"); 
		out.println("<CENTER>");
		
		HttpSession session = request.getSession(true);
		session.setAttribute("bookingID", null);
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
			session.setAttribute("isError", true);
			response.sendRedirect("/group32/ConsumerPage");
			return;
		}
		
		int pin = 0;
		for (int i = 0; i < 8; i++) {
			int digit = (int) (Math.random()*10);
			pin = 10*pin + digit;
		}
		
		String searchPersonQry = "SELECT COUNT(*) AS COUNT FROM CUSTOMERS " +
				"WHERE EMAIL = '" + emailAdd + "'";
		PreparedStatement ps;
		ResultSet rs;
		try {
			DatabaseHandler2 dh = new DatabaseHandler2();
			Connection con = dh.GetDbConnection();
			
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
					} else {
						searchPersonQry = "SELECT * FROM CUSTOMERS " +
								"WHERE EMAIL = '" + emailAdd + "'";
						PreparedStatement psCheck = con.prepareStatement(searchPersonQry);
						rs = psCheck.executeQuery();
						if (rs.next()) {
							if (!rs.getString("FIRSTNAME").trim().equals(firstN) || !rs.getString("LASTNAME").trim().equals(lastN)) {
								out.println("Wrong user details.");
								out.println("</CENTER>");
								out.println("</BODY>"); 
								out.println("</HTML>");
								out.close();
								return;
							}
						}
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
				String insertBookingQry =  "INSERT INTO BOOKINGS(CHECKIN, CHECKOUT,CUSTID,CITYID,ROOMTYPE,EXTRABED,CARDNUM,PIN,NUMROOMS,FAIR) " + 
						"VALUES (" + b.getCheckIn() + "," + b.getCheckOut() + ",'" +
						emailAdd + "'," + b.getCity() + ",'" + b.getType() + "'," + 
						b.isExtraBed() + "," + cardNum + "," + pin + "," + b.getNumRooms() + "," + b.getTotalPrice() + ")";
				con = dh.GetDbConnection();
				ps = con.prepareStatement(insertBookingQry);
				ps.executeUpdate();
				
				String getBookingQry = "SELECT ID FROM BOOKINGS " + 
						"WHERE CHECKIN = " + b.getCheckIn() + " AND CHECKOUT = " + b.getCheckOut() + 
						" AND CUSTID = '" + emailAdd + "' AND CITYID = " + b.getCity() + 
						" AND ROOMTYPE = '" + b.getType() + "' AND EXTRABED = " + b.isExtraBed() +
						" AND CARDNUM = " + cardNum + " AND PIN = " + pin + " AND NUMROOMS = " + b.getNumRooms()
						+ " AND FAIR = " + b.getTotalPrice();
				System.out.println(getBookingQry);
				
				con = dh.GetDbConnection();
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
				out.println("<b>Card Number:</b> " + cardNum + "<br/><br/>");
				out.printf("<b>Regular total:</b> $%.2f<br/>", (b.getTotalPrice() - b.getPeakPrem() - b.getDiscount()));
				out.printf("<b>Peak total:</b> $%.2f<br/>",b.getPeakPrem());
				out.printf("<b>Discount total:</b> $%.2f<br/>", b.getDiscount());
				out.printf("<b>Total Price:</b> <u>$%.2f</u><br/><br/>", b.getTotalPrice());
				
				out.println("Your details can be seen and edited (minimum 48 hours prior to check-in date)"
						+ " at the following URL: "+ "<a href = /group32/EditBooking?bookingID=" + bookingID + ">/group32/EditBooking?bookingID=" + bookingID + "</a><br/>");
				out.println("Your PIN is: " + pin + "<br/>");
				out.println("We have sent an e-mail with these details.<br/><br/>");
				
				out.println("<br/><form action='/Assignment2'>" + 
						"<input type='submit' value='Back to Start'></form>");
				handleMail(request, response, emailAdd, firstN, lastN, bookingID, pin);
			} catch (SQLException e) {
				e.printStackTrace();
				out.println("Cannot add booking.");
				out.println("<br/><form action='/group32'>" + 
						"<input type='submit' value='Back to Search'></form>");
				return;
			}
			dh.CloseDbConnection(con);
		} catch (InstantiationError | InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		} finally {	
			out.println("</CENTER>");
			out.println("</BODY>"); 
			out.println("</HTML>");
			out.close();
		}
	}
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}
	
	private void handleMail(HttpServletRequest request,
		HttpServletResponse response, String emailAdd, String f, String l, int bookingID, int pin) throws ServletException, IOException {
		MailSender sender = null;
		RequestDispatcher disp;
		String msg = "Hi " + ",<br/>";
		msg += "Your details can be seen and edited (minimum 48 hours prior to check-in date)"
				+ " at the following URL: "+ "<a href = /group32/EditBooking?bookingID=" + bookingID + ">/group32/EditBooking?bookingID=" + bookingID + "</a><br/>";
		msg += "Your PIN is: " + pin + "<br/>";
		msg += "We have sent an e-mail with these details.<br/><br/>";
		boolean isSuccess = false;
		try {
			sender = MailSender.getMailSender();
			String fromAddress = emailAdd;
			String toAddress = "hazmaestro@gmail.com";
			String subject = "Congratulations on your booking!";
			StringBuffer mailBody = new StringBuffer();
			mailBody.append(msg);
			sender.sendMessage(fromAddress, toAddress, subject, mailBody);
			isSuccess = true;
		} catch (Exception e){
			logger.severe("Oopsies, could not send message "+e.getMessage());
			e.printStackTrace();
			isSuccess = false;
		}
		/* Standard RequestDispatcher **
		disp = request.getRequestDispatcher(target);
		disp.forward(request, response);
		*/
		/* Post-Redirect-Get implementation */
		URL url = new URL(request.getScheme(),request.getServerName(),request.getServerPort(),
				request.getContextPath());
		logger.info(url.toExternalForm());
		
		if (!isSuccess) {
			out.println("<br/>Email failed to send - sorry.");
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
        } catch(Exception e){
        	System.out.println(e);
        }
        return null;
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
