

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
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
public class ConfirmServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ConfirmServlet() {
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
		
		BookingRequest b = (BookingRequest) session.getAttribute("BookingReq");
		long cardNum = Long.parseLong(request.getParameter("cardnum"));
		String emailAdd = request.getParameter("email");
		String firstN = request.getParameter("first");
		String lastN = request.getParameter("last");
		
		int pin = 0;
		for (int i = 0; i < 8; i++) {
			int digit = (int) (Math.random()*10);
			pin = 10*pin + digit;
		}
		
		String searchPersonQry = "SELECT COUNT(*) FROM PEOPLE " +
				"WHERE ID = " + emailAdd + ";";
		Connection con = DatabaseHandle.GetDbConnection();
		PreparedStatement ps;
		ResultSet rs;
		
		try {
			ps = con.prepareStatement(searchPersonQry);
			rs = ps.executeQuery();
			rs.next();
			int count = Integer.parseInt(rs.getString("COUNT"));
			
			if (count == 0) {
				String addPersonQry = "INSERT INTO PEOPLE VALUES " +
						"(" + emailAdd + "," + firstN + "," + lastN + ");";
				ps = con.prepareStatement(addPersonQry);
				rs = ps.executeQuery();
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
			String roomQry = "SELECT roomID from ROOMS r join BOOKINGS b on r.ID = b.ROOMID "
					+ "WHERE R.TYPE = 'Single' AND r.ID != b.ROOMID;";
			ps = con.prepareStatement(roomQry);
			rs = ps.executeQuery();
			rs.next();
			int roomID = Integer.parseInt(rs.getString("ID"));
					
			String insertBookingQry =  "INSERT INTO BOOKINGS(CHECKIN, CHECKOUT,CUSTID,CITYID,ROOMID,EXTRABED,CARDNUM,"
					+ "PIN) " + "VALUES (" + b.getCheckIn() + "," + b.getCheckOut() + "," +
					emailAdd + "," + b.getCity() + "," + roomID + "," + 
					b.isExtraBed() + "," + cardNum + "," + pin + ");";
				ps = con.prepareStatement(insertBookingQry);
				rs = ps.executeQuery();
			
			String getBookingQry = "SELECT ID FROM BOOKINGS " + 
					"WHERE CHECKIN = " + b.getCheckIn() + " AND CHECKOUT = " + b.getCheckOut() + 
					" AND CUSTID = " + emailAdd + " AND CITYID = " + b.getCity() + 
					" AND ROOMID = " + roomID + " AND EXTRABED = " + b.isExtraBed() +
					" AND CARDNUM = " + cardNum + " AND PIN = " + pin + ";";
			
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
					+ " at the following URL: EditServlet?bookingID=" + bookingID + "<br/>"); //TODO: fix URL
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
		}
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

}
