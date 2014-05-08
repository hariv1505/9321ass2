

import java.io.IOException;
import java.io.PrintWriter;
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
		
		HttpSession session = request.getSession(true);
		Integer bookingID = Integer.parseInt(request.getParameter("bookingID"));
		session.setAttribute("bookingID", bookingID);
		
		PrintWriter out = response.getWriter();
		out.println("<HTML>"); 
		out.println("<BODY>"); 
		out.println("<CENTER>");
		
		out.println("<form method='post' action=EditServlet>");
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
		Integer bookingID = (Integer) request.getAttribute("bookingID");
		Integer pin = Integer.parseInt(request.getParameter("bookingID"));
		
		String qry = "SELECT * FROM BOOKINGS "
				+ "WHERE ID = " + bookingID + ";";
		
		//TODO: get PIN
		Integer savedPin = 0;
		boolean okLogin = (pin == savedPin);

		PrintWriter out = response.getWriter();
		out.println("<HTML>"); 
		out.println("<BODY>"); 
		out.println("<CENTER>");
		
		if (okLogin) {
			//TODO: get from qry
			long checkIn = 0;
		
			if (Calendar.getInstance().getTimeInMillis() + (48*24*60*60*1000) < checkIn) {
				//TODO: use query to fill out form with appropriate values
				
				//TODO: send back to controller. Controller will delete record and add new record				
			} else {
				out.println("<h1>Sorry, you can only edit the page 48 hours prior to booking start.</h1>");
			}
		} else {
			out.println("<form method='post' action=EditServlet>");
			out.println("<label for='PIN'>PIN</label><input type='number' name='PIN' />");
			out.println("</form>");
			out.println("<br/><br/>Incorrect password");
		}
		
		out.println("</CENTER>");
		out.println("</BODY>"); 
		out.println("</HTML>");
		out.close();
		
	}

}
