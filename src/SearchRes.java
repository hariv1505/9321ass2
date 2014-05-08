import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javafx.scene.chart.PieChart.Data;

public class SearchRes {
	private Map<String,Integer> rooms;
	private Integer cindate;
	private Integer cinmonth;
	private Integer cinyear;
	private Integer coutdate;
	private Integer coutmonth;
	private Integer coutyear;
	private Integer city;
	private Integer numRooms;
	private Integer maxPrice;
	
	public SearchRes(int cindate, int cinmonth, int cinyear, int coutdate, int coutmonth, int coutyear,
			int city, int numRooms, Integer maxPrice) {
		rooms = new HashMap<String,Integer>();
		this.cindate = cindate;
		this.cinmonth = cinmonth;
		this.cinyear = cinyear;
		this.coutdate = coutdate;
		this.coutmonth = coutmonth;
		this.coutyear = coutyear;
		this.city = city;
		this.numRooms = numRooms;
		this.maxPrice = maxPrice;
	}
	
	public Map<String,Integer> getRes() {
		return rooms;
	}
	
	public void setRes(Map<String,Integer> r) {
		rooms = r;
	}

	public void getSearchResults() {
		rooms = this.search(cindate,cinmonth,cinyear,coutdate,coutmonth,coutyear,city,numRooms,maxPrice);
		
	}

	private Map<String, Integer> search(Integer cind, Integer cinm,
			Integer ciny, Integer coutd, Integer coutm,
			Integer couty, Integer ct, Integer numRoom, Integer maxP) {
		Map<String,Integer> res = new HashMap<String, Integer>();
		Calendar cIn = Calendar.getInstance();
		cIn.set(ciny, cinm, cind);
		long cInToMS = cIn.getTimeInMillis(); 
		Calendar cOut = Calendar.getInstance();
		cOut.set(couty, coutm, coutd);
		long cOutToMS = cOut.getTimeInMillis();
		
		String qry = "SELECT r.TYPE, COUNT(r.TYPE) FROM BOOKINGS b JOIN ROOMS r on (r.ID=b.ROOMID) " +
				"WHERE r.PRICE <=" + maxP + " AND " +
				"b.CHECKIN <" + cInToMS + " AND " +
				"b.CHECKOUT >" + cOutToMS + " AND " +
				"b.CITYID = " + ct + " " +
				"GROUP BY r.TYPE;";
		try{
			Connection con = DatabaseHandle.GetDbConnection();
			PreparedStatement ps = con.prepareStatement(qry);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				
			}
			//TODO: run qry and store as "Single" -> total-..., "Queen" -> total-..., etc etc in res
		} catch (Exception e) {
			
		}
		
		return res;
	}
}