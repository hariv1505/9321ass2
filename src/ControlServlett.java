

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSetMetaData;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import HelperClass.DatabaseHandler2;
import HelperClass.Init;



/**
 * Servlet implementation class ControlServlett
 */
@WebServlet("/ControlServlett")
public class ControlServlett extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ControlServlett() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		PrintWriter out = response.getWriter();
		HttpSession session;
		if(request.getParameter("ReserveRoom")!=null){
			session=request.getSession();
			out.println("Room has been reserved for Client Name "+session.getAttribute("User"));
			
		}
		//Owner Login
		if(request.getParameter("Owner")!=null){
			String password=request.getParameter("pwd");
			String user=request.getParameter("Owner");
	        if(password == null || password.equals(""))
	            out.println( "Password can't be null or empty");
	        
			try{
				DatabaseHandler2 dh=new DatabaseHandler2();	
				Connection con=dh.GetDbConnection();
			PreparedStatement ps = con.prepareStatement("select * from Owner where USERNAME=? and password=?");
            ps.setString(1, user);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
             
            if(rs != null && rs.next()){
                 
               // User user = new User(rs.getString("name"), rs.getString("email"), rs.getString("country"), rs.getInt("id"));
               //ogger.info("User found with details="+user);
                session = request.getSession();
                session.setAttribute("Owner", user);
                session.setAttribute("Connection", con);
                response.sendRedirect("Occupency.jsp");;
                
            }else{
               
                out.println(user +" not found");
                out.println("No user found with given email id, please register first.");
                
            }
			
			
			//CloseDbConnection(con);
			}catch(Exception e){
				System.out.println(e);
			}
		}
		
		//Reception Manager
		if(request.getParameter("UserName")!=null){
			String password=request.getParameter("pwd");
			String user=request.getParameter("UserName");
	        if(password == null || password.equals(""))
	            out.println( "Password can't be null or empty");
	        
			try{
			DatabaseHandler2 dh=new DatabaseHandler2();	
			Connection con=dh.GetDbConnection();
			PreparedStatement ps = con.prepareStatement("select * from STAFF where USERNAME=? and password=?");
            ps.setString(1, user);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
             
            if(rs != null && rs.next()){
                 
               // User user = new User(rs.getString("name"), rs.getString("email"), rs.getString("country"), rs.getInt("id"));
               //ogger.info("User found with details="+user);
                session = request.getSession();
                session.setAttribute("User", user);
                session.setAttribute("Connection", con);
                response.sendRedirect("AllBookings.jsp");;
                
            }else{
               
                out.println(user +" not found");
                out.println("No user found with given email id, please register first.");
                
            }
			
			
			//CloseDbConnection(con);
			}catch(Exception e){
				System.out.println(e);
			}
		}
	
	
	}


	

}
