


import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
//import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class WelcomeServlet
 */
@WebServlet("/")
public class WelcomeServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public WelcomeServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException	{ 
    	 
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html"); 
    	PrintWriter out = response.getWriter();
    	
    	//HttpSession session = request.getSession(true);
    	    	
    	out.println("<HTML>");
    	out.println("<BODY>"); 
    	out.println("<CENTER>");
    	out.println("<H1>Search for a room!</H1>"); 
    	 
    	out.println("<FORM ACTION='SearchServlet' METHOD='POST'>"); 
    	out.println("<label for='checkin'>Check In</label><div name='checkin'><select name='cindate'>");
    	for (int i = 1; i <= 31; i++) {
    		out.println("<option value='" + i + "'>" + i + "</option>");
    	}
    	out.println("</select><select name='cinmonth'>");
    	for (int i = 1; i <= 12; i++) {
    		out.println("<option value='" + i + "'>" + i + "</option>");
    	}
    	out.println("</select><input type='number' name='cinyear' /></div><br/>");
    	out.println("<label for='checkout'>Check out</label><div name='checkout'><select name='coutdate'>");
    	for (int i = 1; i <= 31; i++) {
    		out.println("<option value='" + i + "'>" + i + "</option>");
    	}
    	out.println("</select><select name='coutmonth'>");
    	for (int i = 1; i <= 12; i++) {
    		out.println("<option value='" + i + "'>" + i + "</option>");
    	}
    	out.println("</select><input type='number' name='coutyear' /></div><br/>");
    	out.println("<label for='city'>City</label><select name='city'>");
        out.println("<option value='1'>Sydney</option>");
        out.println("<option value='2'>Brisbane</option>");
        out.println("<option value='3'>Melbourne</option>");
        out.println("<option value='4'>Adelaide</option>");
        out.println("<option value='5'>Hobart</option>");
        out.println("</select><br/><br/>");
        out.println("<label for='numRooms'>Number of Rooms</label><select name='numRooms'>");
        for (int i = 1; i <= 3; i++) {
    		out.println("<option value='" + i + "'>" + i + "</option>");
    	}
        out.println("</select><br/><br/>");
        out.println("<label for='maxPrice'>Max Price ($)</label><input type='number' name='maxPrice'><br/><br/>");
        out.println("<input type='submit' value='Submit'></form>");
    	out.println("</CENTER>");
    	out.println("</BODY>");
    	out.println("</HTML>");
    	out.close();
    	
    	/*TODO: validation*/
	}

}
