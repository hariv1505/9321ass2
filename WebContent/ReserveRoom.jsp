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
<form action = "RoomAssigned.jsp" method ="post">

<%

String[] selectedItems = request.getParameterValues("selectedItems");
String Room=null;
String City=null;
String Customer=null;
String Custid=null;
int BookingID=0;

int cityID=0;
int occupied=0;
int reserv=0;
for(String S:selectedItems){
	String[] tokens=S.split(";");
	Room= tokens[4];
	City= tokens[3];
	Customer= tokens[2];
	Custid=tokens[7];
	cityID=Integer.parseInt(tokens[8]);
	BookingID=Integer.parseInt(tokens[9]);
}

Room=Room.replaceAll("\\s+","");
City=City.replaceAll("\\s+","");
Customer=Customer.replaceAll("\\s+","");
Custid=Custid.replaceAll("\\s+","");
/*
String Room="Single";
String City="Sydney";
String Customer="hassam";
String Custid="sam";
int cityID=1;
int occupied=0;
*/
%>

Available Rooms of type <%=Room %> in <%=City%> Hotel 


<%
try{
	DatabaseHandler2 dh=new DatabaseHandler2();	
	Connection con=dh.GetDbConnection();
String selectSQL = "Select count(*) from Bookings where id=? ";
PreparedStatement preparedStatement = con.prepareStatement(selectSQL);


preparedStatement.setInt(1,BookingID);
ResultSet rs = preparedStatement.executeQuery();

if(rs.next()){
	occupied=rs.getInt(1);
	//out.println ("Booked->" + occupied);
}


selectSQL = "Select count(*) from Reserved where id=?";
preparedStatement = con.prepareStatement(selectSQL);

preparedStatement.setInt(1,BookingID);
rs = preparedStatement.executeQuery();

if(rs.next()){
	reserv=rs.getInt(1);
	//out.println ("Occupied->"+reserv);
}

int FreeRooms=0;
if (Room.equals("Single")){
	FreeRooms=15-occupied-reserv;
}else if (Room.equals("Twin Bed")){
	FreeRooms=10-occupied-reserv;
}else if (Room.equals("Queen")){
	FreeRooms=10-occupied-reserv;
}else if (Room.equals("Executive")){
	FreeRooms=5-occupied-reserv;
}else if (Room.equals("Suite")){
	FreeRooms=2-occupied-reserv;
}

%>

<table border =1>
<tr> 
			<td width = 50>Serial number</td>
 			<td width = 50>Room Type</td>
            <td width = 50>Number of beds</td>
            <td width = 50>Price of Room</td>
            <td width = 50>Reserve</td>        
        </tr>

<%
selectSQL = "Select id,type,Numbeds,Price from Rooms where type=? and id not in(select ROOMID from Reserved)";
preparedStatement = con.prepareStatement(selectSQL);
preparedStatement.setString(1,Room);
rs = preparedStatement.executeQuery();
int i=1;
while(rs.next()){
	String reserved=rs.getInt("id")+";"+rs.getString("type")+";"+rs.getInt("Numbeds")+";"+rs.getInt("price")+";"+Custid+";"+Customer+";"+cityID+";"+BookingID;
	%>	
<tr> 
			<td width = 50><%=i%></td>
			<td width = 50><%=rs.getString("type") %></td>
            <td width = 50><%=rs.getInt("Numbeds") %></td>
            <td width = 50><%=rs.getInt("price") %></td>
            <td width = 50><input type="checkbox" name="selectedItems1" value="<%=reserved%>"></td>
        </tr>

<%	
i++;

}

}catch(Exception e){
	
	System.out.println(e);
}
%>

</table>




<input type = "submit" name = "ReserveRoom" value = "ReserveRoom">
</form>

</body>
</html>