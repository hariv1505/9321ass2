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

<form action="Discount.jsp">
<input type = "submit" name="Set Discount" value="Set Discount">
<h1>Occupied Rooms</h1>
<table border = "1">
<tr>
 			
            <td width = 50>Customer</td>
            <td width = 50>City</td>
            <td width = 50>Room Type</td>
          
</tr>


<%
try{
	DatabaseHandler2 dh=new DatabaseHandler2();	
	Connection con=dh.GetDbConnection();


Statement stmt = con.createStatement();

String sql = "Select c.Firstname Customer,t.city City ,r.type Room from RESERVED b, Customers c, Rooms r, Cities t where b.custid=c.EMAIL and b.cityid=t.id and b.roomid=r.id";
ResultSet rs = stmt.executeQuery(sql);
 
while (rs.next()){
	//String selectedItems= rs.getInt("Checkin")+";"+rs.getInt("Checkout")+";"+rs.getString("Customer")+";"+rs.getString("City")+";"+rs.getString("Room")+";"+rs.getBoolean("extrabed")+";"+rs.getInt("fair")+";"+rs.getString("custid")+";"+rs.getInt("cityid");
	
	%>
	
	<tr>
 			
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






<!-- --------- -->
<!-- --------- -->
<h1> Available Rooms</h1>
<table border = "1">
<tr>
 			<td width = 50>Count</td>
            <td width = 50>City</td>
            <td width = 50>Room Type</td>                  
            <td width = 50>Rent</td>
            
</tr>

<%
try{
	DatabaseHandler2 dh=new DatabaseHandler2();	
	Connection con=dh.GetDbConnection();


Statement stmt = con.createStatement();

String sql = "SELECT City,type,price FROM available a, Cities c, ROOMS r WHERE a.roomid=r.id and a.cityid=c.id and Not exists (SELECT * FROM Reserved where r.id=roomid and c.id=Cityid)";
ResultSet rs = stmt.executeQuery(sql);
 int i=1;
while (rs.next()){
//String selectedItems= rs.getInt("Checkin")+";"+rs.getInt("Checkout")+";"+rs.getString("Customer")+";"+rs.getString("City")+";"+rs.getString("Room")+";"+rs.getBoolean("extrabed")+";"+rs.getInt("fair");
	
	%>
	
	<tr>
 			
            <td width = 50><%=i++ %></td>
            <td width = 50><%=rs.getString("City") %></td>
            <td width = 50><%=rs.getString("type") %></td>
            <td width = 50><%=rs.getInt("Price") %></td>            
            
                     
        </tr>
	

<%
}
}catch(Exception e){
	System.out.println(e);
}
%>
</table>


<!-- --------- -->












</form>



</body>
</html>