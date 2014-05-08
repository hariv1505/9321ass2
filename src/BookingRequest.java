import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javafx.scene.chart.PieChart.Data;


public class BookingRequest {
	private boolean isExtraBed = false;
	private int numBeds;
	private int numRooms;
	private int price;
	private long checkIn;
	private long checkOut;
	private int city;
	private int type;
	public static Map<Long, Long> publicPeakPeriods;
	private Map<Long, Long> peakPeriods;
	
	
	public BookingRequest(long checkIn, long checkOut, int city) {
		this.checkIn = checkIn;
		this.checkOut = checkOut;
		this.city = city;
		this.setPeakPeriods();
	}
	
	private void setPeakPeriods() {
		peakPeriods = new HashMap<Long, Long>();
		Calendar start = Calendar.getInstance();
		start.setTimeInMillis(checkIn);
		int startYear = start.get(Calendar.YEAR);
		Calendar end = Calendar.getInstance();
		end.setTimeInMillis(checkOut);
		int endYear = start.get(Calendar.YEAR);
		
		for (int i = startYear; i <= endYear; i++) {
			Calendar c = Calendar.getInstance();
			c.set(Calendar.MILLISECOND, 0);
			
			c.set(i, 0, 0, 0, 0, 0);
			Long k = c.getTimeInMillis();
			c.set(i, 1, 15, 23, 59, 59);
			Long v = c.getTimeInMillis();
			peakPeriods.put(k, v);
			
			c.set(i, 2, 25, 0, 0, 0);
			k = c.getTimeInMillis();
			c.set(i, 3, 15, 14, 59, 59);
			v = c.getTimeInMillis();
			peakPeriods.put(k, v);
			
			c.set(i, 6, 1, 0, 0, 0);
			k = c.getTimeInMillis();
			c.set(i, 6, 20, 23, 59, 59);
			v = c.getTimeInMillis();
			peakPeriods.put(k, v);
			
			c.set(i, 8, 20, 0, 0, 0);
			k = c.getTimeInMillis();
			c.set(i, 9, 10, 23, 59, 59);
			v = c.getTimeInMillis();
			peakPeriods.put(k, v);
			
			c.set(i, 11, 15, 0, 0, 0);
			k = c.getTimeInMillis();
			c.set(i, 11, 31, 23, 59, 59);
			v = c.getTimeInMillis();
			peakPeriods.put(k, v);
		}
		
		publicPeakPeriods = peakPeriods;
		
	}

	public void setRest(String roomToBook) {
		String[] roomToBookDetails = roomToBook.split(";");
		if (roomToBookDetails[1] == "Y") isExtraBed = true;
		String type = roomToBookDetails[0];
		
		numRooms = Integer.parseInt(roomToBookDetails[2]);
		
		String roomQry = "SELECT * FROM ROOMS "
				+ "WHERE TYPE = " + type + ";";
		Connection con = DatabaseHandle.GetDbConnection();
		try {
			PreparedStatement ps = con.prepareStatement(roomQry);
			ResultSet rs = ps.executeQuery();
			rs.next();
			price = Integer.parseInt(rs.getString("PRICE"));
			numBeds = Integer.parseInt(rs.getString("NUMBEDS"));
		} catch (SQLException e) {
			price = 0;
			numBeds = 0;
		}
		DatabaseHandle.CloseDbConnection(con);
		
		this.setNumRooms(numRooms);
		
		if (this.isExtraBed()) {
			numBeds++;
		}
		
	}
	
	public boolean isExtraBed() {
		return isExtraBed;
	}

	public Integer getNumBeds() {
		return numBeds;
	}

	public int getPricePerNight() {
		return price;
	}
	

	public void setNumBeds(int numBeds) {
		this.numBeds = numBeds;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public int getNumRooms() {
		return numRooms;
	}

	public void setNumRooms(int numRooms) {
		this.numRooms = numRooms;
	}

	public long getCheckIn() {
		return checkIn;
	}

	public void setCheckIn(long checkIn) {
		this.checkIn = checkIn;
	}

	public long getCheckOut() {
		return checkOut;
	}

	public void setCheckOut(long checkOut) {
		this.checkOut = checkOut;
	}
	
	public int getNumNights() {
		return (int)(checkOut - checkIn)/(1000*60*60*24);
		
	}

	public String getCheckInToString() {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(checkIn);
		
		return cal.get(Calendar.DATE) + "/" + cal.get(Calendar.MONTH)+1 + "/" + cal.get(Calendar.YEAR);
	}

	public String getCheckOutToString() {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(checkOut);
		
		return cal.get(Calendar.DATE) + "/" + cal.get(Calendar.MONTH) + "/" + cal.get(Calendar.YEAR);
	}
	
	public double getTotalPrice() {
		double ans = 0;
		
		for (long i = checkIn; i <= checkOut; i++) {
			for (long start : peakPeriods.values()) {
				if (start > i) {
					ans += price;
					if (isExtraBed)  ans += 35;
					break;
				} else {
					if (peakPeriods.get(start) > i) {
						ans += price*1.4;
						if (isExtraBed)  ans += 35;
						break;
					}
				}
			}
		}
		return ans;
	}
	
	public static double getTotalPrice(int p, long cin, long cout, int numR) {
		double ans = 0;
		for (long i = cin; i <= cout; i++) {
			for (long start : publicPeakPeriods.values()) {
				if (start > i) {
					ans += p;
					break;
				} else {
					if (publicPeakPeriods.get(start) > i) {
						ans += p*1.4;
						break;
					}
				}
			}
		}
		return ans;
	}

	public int getCity() {
		return city;
	}

	public void setCity(int city) {
		this.city = city;
	}

	public int getType() {
		return type;
	}
	
	public void setType(int type) {
		this.type = type;
	}
}