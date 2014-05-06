


import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class SearchServlet
 */
@WebServlet("/SearchServlet")
public class SearchServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SearchServlet() {
        super();
        // TODO Auto-generated constructor stub
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
		
		System.out.println(request.getParameter("cindate"));
		Integer cindate = Integer.parseInt(request.getParameter("cindate"));
		Integer cinmonth = Integer.parseInt(request.getParameter("cinmonth"));
		Integer cinyear = Integer.parseInt(request.getParameter("cinyear"));
		
		Integer coutdate = Integer.parseInt(request.getParameter("coutdate"));
		Integer coutmonth = Integer.parseInt(request.getParameter("coutmonth"));
		Integer coutyear = Integer.parseInt(request.getParameter("coutyear"));
		
		Integer city = Integer.parseInt(request.getParameter("city"));
		
		Integer numBeds = Integer.parseInt(request.getParameter("numBeds"));
		
		out.println("<H1>Search results for query</H1>"); 
		
		HttpSession session = request.getSession(true);
		SearchRes sr = (SearchRes) session.getAttribute("SearchFlag");

		sr = new SearchRes(cindate, cinmonth, cinyear, coutdate, coutmonth, coutyear, city, numBeds);
		sr.getSearchResults();
		
		//only for testing - format properly for page later
		out.println(cindate + " " + cinmonth + " " + cinyear + " " + coutdate + " " + coutmonth + " " + coutyear + " " + 
				city + " " + numBeds);
		
		if (sr.getRes().keySet().size() > 0) {
			out.println("<form action='ChoiceServlet' method='POST'><table>");
			
			out.println("<tr>" +
						"<td><b>Type<b></td>" +
						"<td><b>Number of beds<b></td>" +
						"<td><b>Price</b></td>" +
						"<td><b>Number remaining</b></td>" +
						"<td><b>Select?</b></td></tr>");
			for (String type : sr.getRes().keySet()) {
				out.println("<tr>");
				out.println("<td>" + type + "</td>" + "<td>" + /*TODO +*/ "</td>" + 
				"<td>" + /*TODO +*/ "</td>"	+ "<td>" + sr.getRes().get(type) + "</td>" +
				"<td>" + "<input type='checkbox' name='toBook' value='" + type + "' />" +"</td>");
				out.println("</tr>");
			}
					
			out.println("</table>");
			out.println("<br><input type='submit'  name='ChoiceSubmit' value='Book!'>");
			out.println("</form>");
		} else {
			out.println("No available rooms. Please try again with a different query. Thank you, and"
					+ "sorry for the inconvenience.");
		}
		out.println("<form action='/Assignment2'>" + 
				"<input type='submit' value='Back to Search'></form>");
		
		out.println("</CENTER>");
		out.println("</BODY>"); 
		out.println("</HTML>");
		out.close();
	}

}
