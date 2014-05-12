

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class CheckoutServlet
 */
@WebServlet("/Booking")
public class Booking extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Booking() {
        super();
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		
		HttpSession session = request.getSession();
		
		BookingRequest b = (BookingRequest) session.getAttribute("BookingReq");
		String roomToBook  = request.getParameter("toBook");
		
		if (roomToBook == null) {
			if (b.getType() != null) {
				roomToBook = b.getType();
				if (b.isExtraBed())	roomToBook += ";Y;";
				else roomToBook += ";N;";
				roomToBook += b.getNumRooms();
			} else {
				session.setAttribute("isError", true);
				response.sendRedirect("/group32/ConsumerPage");
				return;
			}
		} else {
			session.setAttribute("maxPrice", null);
			if (b != null) {
				b.setRest(roomToBook);
				session.setAttribute("BookingReq", b);
			}
		}
		
		PrintWriter out = response.getWriter();
		out.println("<HTML>"); 
		out.println("<BODY>"); 
		out.println("<CENTER>"); 
		
		out.println("<h1>Book your place!</h1>");
		
		out.println("<br/><b>Type:</b> " + roomToBook.split(";")[0] + "<br/>");
		out.println("<br/><b>City:</b> " + b.getCity() + "<br/>");
		out.println("<b>Number of beds:</b> " + b.getNumBeds() + "<br/>");
		out.println("<b>Number of rooms:</b> " + b.getNumRooms() + "<br/>");
		out.println("<b>Price per night:</b> "+ b.getPricePerNight() + "<br/>");
		out.println("<b>Check-in:</b> "+ b.getCheckInToString() + "<br/>");
		out.println("<b>Check-out:</b> "+ b.getCheckOutToString() + "<br/><br/>");
		out.printf("<b>Regular total:</b> $%.2f<br/>", (b.getTotalPrice() - b.getPeakPrem() - b.getDiscount()));
		out.printf("<b>Peak total:</b> $%.2f<br/>",b.getPeakPrem());
		out.printf("<b>Discount total:</b> $%.2f<br/>", b.getDiscount());
		out.printf("<b>Total Price:</b> <u>$%.2f</u><br/><br/>", b.getTotalPrice());
		
		out.println("<form method='post' action='ConfirmServlet'>");
		out.println("<label for='email'>E-Mail</label><input type='email' name='email' /><br/>");
		out.println("<label for='first'>First Name</label><input type='text' name='first' /><br/>");
		out.println("<label for='last'>Last Name</label><input type='text' name='last' /><br/>");
		out.println("<label for='cardnum'>Card Number</label><input type='text' name='cardnum' /><br/>");	
		out.println("<input type='submit' value='Confirm!' />");
		out.println("</form>");
		out.println("<br/><form action='/group32'>" + 
				"<input type='submit' value='Cancel'></form>");
		
		boolean isError = (Boolean) session.getAttribute("isError");
		
		if (isError) {
			out.println("Error in input");
			out.println("<br/><form action='/group32'>" + 
					"<input type='submit' value='Back to Search'></form>");
		}
		
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
