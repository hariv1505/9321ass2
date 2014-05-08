
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

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
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		HttpSession session = request.getSession(true);
		
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
		
		Integer numRooms = Integer.parseInt(request.getParameter("numRooms"));
		
		Integer maxPrice = Integer.parseInt(request.getParameter("maxPrice"));
		
		boolean foundOption = false; 
		
		out.println("<H1>Search results for query</H1><br/>"); 
		
		SearchRes sr = new SearchRes(cindate, cinmonth, cinyear, coutdate, coutmonth, coutyear, city, numRooms, maxPrice);
		sr.getSearchResults();
		
		//just in case booking is requested...
		Calendar cIn = Calendar.getInstance();
		cIn.set(cinyear, cinmonth-1, cindate,0,0,0);
		long cInToMS = cIn.getTimeInMillis(); 
		Calendar cOut = Calendar.getInstance();
		cOut.set(coutyear, coutmonth-1, coutdate,0,0,0);
		long cOutToMS = cOut.getTimeInMillis();
		BookingRequest b = new BookingRequest(cInToMS, cOutToMS, city);
		session.setAttribute("BookingReq", b);
		out.println(cInToMS + " " + cOutToMS);
		
		out.println("<b>City: </b> " + city + "<br/>");
		out.println("<b>Number of rooms: </b> " + numRooms + "<br/>");
		out.println("<b>Maximum price per night: </b> "+ maxPrice + "<br/>");
		out.println("<b>Check-in: </b> "+ b.getCheckInToString() + "<br/>");
		out.println("<b>Check-out: </b> "+ b.getCheckOutToString() + "<br/>");
		
		//if (sr.getRes().keySet().size() > 0) {
			out.println("<form action='CheckoutServlet' method='POST'><table>");
			
			out.println("<tr>" +
						"<td><b>Type<b></td>" +
						"<td><b>Number of beds</b></td>" +
						"<td><b>Price</b></td>" +
						"<td><b>Number of rooms required</b></td>" +
						"<td><b>Number of rooms remaining</b></td>" +
						"<td><b>Select?</b></td></tr>");
						
			Map<String, Integer> prices = new HashMap<String, Integer>();
			Map<String, Integer> numberOfBeds = new HashMap<String, Integer>();
			String searchQry = "SELECT * FROM ROOMS";
			Connection con = DatabaseHandle.GetDbConnection();
			try {
				PreparedStatement ps = con.prepareStatement(searchQry);
				ResultSet rs = ps.executeQuery();
				while (rs.next()) {
					prices.put(rs.getString("TYPE"),Integer.parseInt(rs.getString("PRICE")));
					prices.put(rs.getString("TYPE"),Integer.parseInt(rs.getString("NUMBEDS")));
				}
				
				for (String type : sr.getRes().keySet()) {
					if (prices.get(type) < maxPrice) {
						out.println("<tr>");
						out.println("<td>" + type + "</td>" + "<td>" + numberOfBeds.get(type) + "</td>" + 
								"<td>" + prices.get(type) + "</td>"	+ "<td>" + numRooms + "</td>" +	
								"<td>" + sr.getRes().get(type) + "</td><td>");
						if (sr.getRes().get(type) > numRooms) {
							foundOption = true;
							out.println("<input type='radio' name='toBook' value='" + type + ";N" + "' />");
						}
	
						if (type != "Single") {
							out.println("</td></tr>");
							out.println("<tr>");
							out.println("<td>" + type + "</td>" + "<td>" + numberOfBeds.get(type) + 1 + "</td>" + 
									"<td>" + prices.get(type) + 35 + "</td>" + "<td>" + numRooms + "</td>" +
									"<td>" + sr.getRes().get(type) + "</td><td>");
							if (sr.getRes().get(type) > 0) {
								foundOption = true;
								out.println("<input type='radio' name='toBook' value='" + type + ";Y" + "' />");
							}
							out.println("</td></tr>");
						}
	
						out.println("<tr></tr>");
					}
				}
				
				out.println("</table>");
				out.println("<br/>Note: prices may change upon confirmation to account for peak-hour timing<br/>");
				
				
				if (!foundOption) {
					out.println("<br/>No available rooms. Please try again with a different query. Thank you, and "
							+ "sorry for the inconvenience.<br/>");
				} else {
					out.println("<br><input type='submit'  name='ChoiceSubmit' value='Book!'>");
				}
				
				out.println("</form><br/>");
				
				out.println("<br/><form action='/Assignment2'>" + 
						"<input type='submit' value='Back to Search'></form>");
				
			}  catch (SQLException e) {
				out.println("Cannot connect to database.");
			}
			DatabaseHandle.CloseDbConnection(con);
		/*} else {
			out.println("<br/>No available rooms. Please try again with a different query. Thank you, and "
					+ "sorry for the inconvenience.<br/>");
			out.println("<br/><form action='/Assignment2'>" + 
					"<input type='submit' value='Back to Search'></form>");
		}*/
		
		out.println("</CENTER>");
		out.println("</BODY>"); 
		out.println("</HTML>");
		out.close();
	}

}