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

<h1>Please Enter the name of Customer to checkout</h1>
<form action="checkout.jsp" method="post">

<input type="text" name="customer">
<br>
<%
if(request.getParameter("Confirm")!=null){
	out.println(request.getParameter("customer"));
	
	DatabaseHandler2 dh=new DatabaseHandler2();	
	Connection con=dh.GetDbConnection();
	
	String sql = "DELETE FROM Bookings where custid = ?";
    PreparedStatement prest = con.prepareStatement(sql);
    prest.setString(1, request.getParameter("customer"));
    int del = prest.executeUpdate();
    
    
    sql = "DELETE FROM Reserved where custid = ?";
    prest = con.prepareStatement(sql);
    prest.setString(1, request.getParameter("customer"));
     del = prest.executeUpdate();
	out.println("Bookings removed :"+del);

%>


<%
}
%>
<input type="submit" name="Confirm" value="Confirm">

</form>
<form action="AllBookings.jsp">
<input type="submit" name="Bookings" value="Bookings">
</form>

</body>
</html>