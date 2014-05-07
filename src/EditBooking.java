

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//TODO: fill out password. if password is correct then...
		Integer booking = Integer.parseInt(request.getParameter("bookingID"));
		String qry = "SELECT * FROM BOOKINGS "
				+ "WHERE ID = " + booking + ";";
		//TODO: use query to fill out form with appropriate values
		
		//TODO: send back to controller. Controller will delete record and add new record
		
		//TODO: else, if password is incorrect, re-load page with error message
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
