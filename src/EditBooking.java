

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
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
		
		PrintWriter out = response.getWriter();
		out.println("<HTML>"); 
		out.println("<BODY>"); 
		out.println("<CENTER>");
		
		String qry = "SELECT * FROM BOOKINGS "
				+ "WHERE ID = " + bookingID + ";";
		Connection con = DatabaseHandle.GetDbConnection();
		PreparedStatement ps;
		try {
			ps = con.prepareStatement(qry);
		
			ResultSet rs = ps.executeQuery();
			rs.next();
			Integer savedPin = Integer.parseInt(rs.getString("PIN"));
			boolean okLogin = (pin == savedPin);
	
			if (okLogin) {
				long checkIn = Long.parseLong(rs.getString("CHECKIN"));
			
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
		} catch (SQLException e) {
			out.println("Booking does not exist.");
		} finally {
			out.println("</CENTER>");
			out.println("</BODY>"); 
			out.println("</HTML>");
			out.close();
		}
	}

}
