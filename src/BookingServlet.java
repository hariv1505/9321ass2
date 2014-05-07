

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html"); 
		PrintWriter out = response.getWriter();
		out.println("<HTML>"); 
		out.println("<BODY>"); 
		out.println("<CENTER>"); 
		
		out.println("<h1>Book your place!</h1>");
		out.println("<form method='post' action='ConfirmServlet'>");
		out.println("<label for='email'>E-Mail</label><input type='email' name='email' /><br/>");
		out.println("<label for='email'>First Name</label><input type='text' name='first' /><br/>");
		out.println("<label for='email'>Last Name</label><input type='text' name='last' /><br/>");
		//TODO need to verify input
		out.println("<input type='submit' value='Confirm!' />");
		out.println("</CENTER>");
		out.println("</BODY>"); 
		out.println("</HTML>");
		out.close();
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
		
		out.println("<h1>Book your place!</h1>");
		out.println("<form method='post' action='ConfirmServlet'>");
		out.println("<label for='email'>E-Mail</label><input type='email' name='email' /><br/>");
		out.println("<label for='email'>First Name</label><input type='text' name='first' /><br/>");
		out.println("<label for='email'>Last Name</label><input type='text' name='last' /><br/>");
		out.println("<input type='submit' value='Confirm!' />");
		out.println("</CENTER>");
		out.println("</BODY>"); 
		out.println("</HTML>");
		out.close();
	}

}
