

import java.io.IOException;
import java.io.PrintWriter;

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
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		
		HttpSession session = request.getSession(true);
		
		BookingRequest b = (BookingRequest) session.getAttribute("BookingReq");
		long cardNum = Long.parseLong(request.getParameter("cardnum"));
		String emailAdd = request.getParameter("email");
		String firstN = request.getParameter("first");
		String lastN = request.getParameter("last");
		
		int pin = 0; //TODO: create PIN
		
		//TODO: look up people with email address. If non-existant, add them in
		String searchPersonQry = "SELECT COUNT(*) FROM PEOPLE " +
				"WHERE ID = " + emailAdd + ";";
		//TODO: execute
		//if (count == 0) {
			String addPersonQuery = "INSERT INTO PEOPLE VALUES " +
					"(" + emailAdd + "," + firstN + "," + lastN + ");";
			//execite query
		//}
		
		PrintWriter out = response.getWriter();
		out.println("<HTML>"); 
		out.println("<BODY>"); 
		out.println("<CENTER>"); 
		out.println("<h1>Congratulations on your booking!</h1>");

		out.println("<br/><b>First Name:</b> " + firstN + "<br/>");
		out.println("<br/><b>Last Name:</b> " + lastN + "<br/>");
		out.println("<br/><b>Email Address (username):</b> " + emailAdd + "<br/>");
		out.println("<br/><b>Type:</b> " + b.getType() + "<br/>");
		out.println("<br/><b>City:</b> " + b.getCity() + "<br/>");
		out.println("<b>Number of beds:</b> " + b.getNumBeds() + "<br/>");
		out.println("<b>Number of rooms:</b> " + b.getNumRooms() + "<br/>");
		out.println("<b>Price per night:</b> "+ b.getPrice() + "<br/>");
		out.println("<b>Check-in:</b> "+ b.getCheckInToString() + "<br/>");
		out.println("<b>Check-out:</b> "+ b.getCheckOutToString() + "<br/>");
		out.println("<b>Total Price:</b> <u>"+ b.getTotalPrice() +"</u><br/><br/>");
		out.println("<br/><b>Card Number:</b> " + cardNum + "<br/>");

		//TODO: add INSERT INTO BOOKINGS sql for new booking
		String insertBookingQry =  "INSERT INTO BOOKINGS(CHECKIN, CHECKOUT,CUSTID,CITYID,ROOMID,EXTRABED,CARDNUM,"
				+ "PIN) " + "VALUES (" + b.getCheckIn() + "," + b.getCheckOut() + "," +
				emailAdd + "," + b.getCity() + "," + /*TODO +*/ "," + 
				b.isExtraRoom() + "," + cardNum + "," + pin + ");"; 
		
		//TODO: get from getBookingQry sql
		String getBookingQry = "SELECT ID FROM BOOKINGS " + 
				"WHERE CHECKIN = " + b.getCheckIn() + " AND CHECKOUT = " + b.getCheckOut() + 
				" AND CUSTID = " + emailAdd + " AND CITYID = " + b.getCity() + 
				" AND ROOMID = " + /*TODO +*/ " AND EXTRABED = " + b.isExtraRoom() +
				" AND CARDNUM = " + cardNum + " AND PIN = " + pin + ";";
		int bookingID = 0;
		
		out.println("Your details can be seen and edited (minimum 48 hours prior to check-in date)"
				+ " at the following URL: EditServlet?bookingID=" + bookingID + "<br/>");
		out.println("Your PIN is: " + pin + "</br>"); 
		
		out.println("<br/><form action='/Assignment2'>" + 
				"<input type='submit' value='Back to Start'></form>");
		
		out.println("</CENTER>");
		out.println("</BODY>"); 
		out.println("</HTML>");
		out.close();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

}
