

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
@WebServlet("/CheckoutServlet")
public class BookingServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public BookingServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		
		HttpSession session = request.getSession(true);
		
		BookingRequest b = (BookingRequest) session.getAttribute("BookingReq");
		String roomToBook  = request.getParameter("toBook");
		
		if (roomToBook == null) {
			//TODO: redo input
			return;
		} else {
			b.setRest(roomToBook);
			session.setAttribute("BookingReq", b);
		}
		
		PrintWriter out = response.getWriter();
		out.println("<HTML>"); 
		out.println("<BODY>"); 
		out.println("<CENTER>"); 
		
		out.println("<h1>Book your place!</h1>");
		
		out.println("<br/><b>Type:</b> " + roomToBook + "<br/>");
		out.println("<br/><b>City:</b> " + b.getCity() + "<br/>");
		out.println("<b>Number of beds:</b> " + b.getNumBeds() + "<br/>");
		out.println("<b>Number of rooms:</b> " + b.getNumRooms() + "<br/>");
		out.println("<b>Price per night:</b> "+ b.getPrice() + "<br/>");
		out.println("<b>Check-in:</b> "+ b.getCheckInToString() + "<br/>");
		out.println("<b>Check-out:</b> "+ b.getCheckOutToString() + "<br/>");
		out.println("<b>Total Price:</b> <u>"+ b.getTotalPrice() +"</u><br/><br/>");
		
		out.println("<form method='post' action='ConfirmServlet'>");
		out.println("<label for='email'>E-Mail</label><input type='email' name='email' /><br/>");
		out.println("<label for='first'>First Name</label><input type='text' name='first' /><br/>");
		out.println("<label for='last'>Last Name</label><input type='text' name='last' /><br/>");
		out.println("<label for='cardnum'>Card Number</label><input type='text' name='cardnum' /><br/>");
		//TODO need to verify input
		out.println("<input type='submit' value='Confirm!' />");
		out.println("</form>");
		out.println("<br/><form action='/Assignment2'>" + 
				"<input type='submit' value='Cancel'></form>");
		out.println("</CENTER>");
		out.println("</BODY>"); 
		out.println("</HTML>");
		out.close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
	}

}
