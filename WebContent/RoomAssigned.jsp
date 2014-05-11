<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ page import="HelperClass.*"%>
<%@ page import="java.sql.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
<%
if(request.getParameter("ReserveRoom")!=null){
	
	
	DatabaseHandler2 dh=new DatabaseHandler2();	
	try{
		Connection con=dh.GetDbConnection();
		
		String[] selectedItems1 = request.getParameterValues("selectedItems1");
		for (String S:selectedItems1){
		
		String[] tokens=S.split(";");		
			
		String insertTableSQL = "INSERT INTO RESERVED (ROOMID,CUSTID,CITYID,BOOKINGID) VALUES (?,?,?,?)";
		PreparedStatement preparedStatement = con.prepareStatement(insertTableSQL);
		preparedStatement.setInt(1, Integer.parseInt(tokens[0]));
		preparedStatement.setString(2, tokens[4]);
		preparedStatement.setInt(3, Integer.parseInt(tokens[6]));
		preparedStatement.setInt(4, Integer.parseInt(tokens[7]));
		// execute insert SQL stetement
		int k=preparedStatement .executeUpdate();
		if(k>=1)
			out.println("Room(s) has been reserved by the manager = "+session.getAttribute("User"));
		else 
			out.println("ERROR");
		out.println("<br>");
		}
	}catch(Exception e){
		System.out.println(e);
		
	}
	
			
}
%>
<br>
<br>
<b>Reserved Rooms</b>
<table border = "1">
<tr>
 			<td width = 50>Booking ID</td>
            <td width = 50>Customer</td>
            <td width = 50>City</td>
            <td width = 50>Room Type</td>
          
</tr>


<%
try{
	DatabaseHandler2 dh=new DatabaseHandler2();	
	Connection con=dh.GetDbConnection();


Statement stmt = con.createStatement();

String sql = "Select BOOKINGID, c.Firstname Customer,t.city City ,r.type Room from RESERVED b, Customers c, Rooms r, Cities t where b.custid=c.EMAIL and b.cityid=t.id and b.roomid=r.id";
ResultSet rs = stmt.executeQuery(sql);
 
while (rs.next()){
	//String selectedItems= rs.getInt("Checkin")+";"+rs.getInt("Checkout")+";"+rs.getString("Customer")+";"+rs.getString("City")+";"+rs.getString("Room")+";"+rs.getBoolean("extrabed")+";"+rs.getInt("fair")+";"+rs.getString("custid")+";"+rs.getInt("cityid");
	
	%>
	
	<tr>
 			<td width = 50><%=rs.getString("BOOKINGID") %></td>
            <td width = 50><%=rs.getString("Customer") %></td>
            
            <td width = 50><%=rs.getString("City") %></td>
            <td width = 50><%=rs.getString("Room") %></td>
            
        </tr>
	

<%
}
//CloseDbConnection(con);
}
catch(Exception e){
	System.out.println(e);
	
}
%>

</table>

</body>
</html>