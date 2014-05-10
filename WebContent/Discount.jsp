<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
 <%@ page import="HelperClass.*"%>
 <%@ page import="java.sql.*"%> 
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Discount</title>
</head>
<body>
<form action="Discount.jsp" method="post">
<h1>Set Discount</h1>

<table align="center">

<tr>
<td align="left" width = "80">Start day:</td>
<td align = "left" width = "20"><select name="Sday">
           		<%
           		for(int i=1;i<=31;i++){
           		if(i<=9){
           		%>    
               <option value=0<%=i %>>0<%=i %></option>               
				<%}else{	%>
				<option value=<%=i %>><%=i %></option>
<%
				}
}
%>
</select>




</td>
<td align = "left" width = "20"><select name="Smonth">
           		<%
           		for(int i=1;i<=12;i++){
           		if(i<=9){
           		%>    
               <option value=0<%=i %>>0<%=i %></option>               
				<%}else{	%>
				<option value=<%=i %>><%=i %></option>
<%
				}
}
%>
</select>
</td>
<td align = "left" width = "50"><select name="Syear">
           		<%
           		for(int i=2014;i<2020;i++){
           		%>    
               <option value=<%=i %>><%=i %></option>             
<%
}
%>
</select>
</td>
</tr>



<tr>
<td align="left" width = "80">End day:</td>
<td align = "left" width = "20"><select name="Eday">
           		<%
           		for(int i=1;i<=31;i++){
           		if(i<=9){
           		%>    
               <option value=0<%=i %>>0<%=i %></option>               
				<%}else{	%>
				<option value=<%=i %>><%=i %></option>
<%
				}
}
%>
</select>
</td>
<td align = "left" width = "20"><select name="Emonth">
           		<%
           		for(int i=1;i<=31;i++){
           		if(i<=9){
           		%>    
               <option value=0<%=i %>>0<%=i %></option>               
				<%}else{	%>
				<option value=<%=i %>><%=i %></option>
<%
				}
}
%>
</select>
</td>
<td align = "left" width = "50"><select name="Eyear">
           		<%
           		for(int i=2014;i<=2020;i++){
           		%>    
               <option value=<%=i %>><%=i %></option>             
<%
}
%>
</select>
</td>
</tr>
</table>
<table align="center">
<tr>
<td align="left" width = "80">Discount:</td>
<td align = "left"><input type = "text" name="Discount"></td>
</tr>

<tr>
<td align="left" width = "80">Room Type:</td>
<td align = "left"><input type = "text" name="RoomType"></td>
</tr>

<tr>
<td align = "left"><input type = "submit" name="Discountconfirm" value= "confirm" align="left"></td>
<td align = "left"><a href="Occupency.jsp">Go Back</a></td>
</tr>
</table>


<%
	DatabaseHandler2 dh=new DatabaseHandler2();
if(request.getParameter("Discountconfirm")!=null){
	String discount=request.getParameter("Discount");
	String RT=request.getParameter("RoomType");
	if(discount.matches("\\d+")==true && RT.matches("[a-zA-Z]+")){
		Integer StartDate=Integer.parseInt(request.getParameter("Sday")+request.getParameter("Smonth")+request.getParameter("Syear"));	
		Integer EndDate=Integer.parseInt(request.getParameter("Eday")+request.getParameter("Emonth")+request.getParameter("Eyear"));
		String type=request.getParameter("RoomType");
		Integer Discount=Integer.parseInt(request.getParameter("Discount"));
		
		try{
			Connection con=dh.GetDbConnection();
			
			String insertTableSQL = "INSERT INTO Discount (STARTDATE, ENDDATE, ROOMTYPE, DISCOUNTRATE) VALUES (?,?,?,?)";
			PreparedStatement preparedStatement = con.prepareStatement(insertTableSQL);
			preparedStatement.setInt(1, StartDate);
			preparedStatement.setInt(2, EndDate);
			preparedStatement.setString(3, type);
			preparedStatement.setInt(4, Discount);		
			// execute insert SQL stetement
			int k=preparedStatement .executeUpdate();
			if(k>=1)
				out.println("Discount Set Successfully");
			else 
				out.println("ERROR");
		} catch (Exception e) {
			System.out.println(e);
			
		}
	}else {
		out.println ("Enter Valid value for Discount and Room type");
		
	}	
	
}
%>

</form>




</body>
</html>