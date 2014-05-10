<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="HelperClass.*"%>
<%@ page import="java.sql.*"%>




<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>All Bookings</title>
</head>
<h1> BOOKINGS </h1>
<body>
<form action="ReserveRoom.jsp" method="post">
<table border = "1">
<tr>
 			<td width = 50>Check In</td>
            <td width = 50>Check Out</td>
            <td width = 50>Customer</td>
            <td width = 50>Customer ID</td>
            <td width = 50>City</td>
            <td width = 50>Room Type</td>
            <td width = 50>Extra Bed</td>
            <td width = 50>Rent</td>
            <td width = 50>Select</td>
</tr>


<%
try{
	DatabaseHandler2 dh=new DatabaseHandler2();	
	Connection con=dh.GetDbConnection();


Statement stmt = con.createStatement();

String sql = "Select Checkin,checkout,c.Firstname Customer,b.custid,t.id cityid, t.city City ,r.type Room,extrabed,cardnum,pin,fair from Bookings b, Customers c, Rooms r, Cities t where b.custid=c.EMAIL and b.cityid=t.id and b.roomid=r.id";
ResultSet rs = stmt.executeQuery(sql);
 
while (rs.next()){
	String selectedItems= rs.getInt("Checkin")+";"+rs.getInt("Checkout")+";"+rs.getString("Customer")+";"+rs.getString("City")+";"+rs.getString("Room")+";"+rs.getBoolean("extrabed")+";"+rs.getInt("fair")+";"+rs.getString("custid")+";"+rs.getInt("cityid");
	
	%>
	
	<tr>
 			<td width = 50><%=rs.getInt("Checkin") %></td>
            <td width = 50><%=rs.getInt("Checkout") %></td>
            <td width = 50><%=rs.getString("Customer") %></td>
            <td width = 50><%=rs.getString("custid") %></td>
            <td width = 50><%=rs.getString("City") %></td>
            <td width = 50><%=rs.getString("Room") %></td>
            <td width = 50><%=rs.getBoolean("extrabed") %></td>
            <td width = 50><%=rs.getInt("fair") %></td>
            <td><input type="checkbox" name="selectedItems" value="<%=selectedItems%>" /> </td>          
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
<input type="submit" name="Reserve" Value="Reserve">
</form>
<form action="checkout.jsp" method="post">
<input type = "submit" name="checkout" value= "checkout">
</form>


</body>
</html>