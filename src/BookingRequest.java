import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class BookingRequest {
	private boolean isExtraRoom = false;
	private int numBeds;
	private int numRooms;
	private int price;
	private long checkIn;
	private long checkOut;
	private int city;
	private int type;
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
		
	}

	public void setRest(String roomToBook) {
		String[] roomToBookDetails = roomToBook.split(";");
		if (roomToBookDetails[1] == "Y") isExtraRoom = true;
		String type = roomToBookDetails[0];
		
		numRooms = Integer.parseInt(roomToBookDetails[2]);
		
		String roomQry = "SELECT * FROM ROOMS "
				+ "WHERE TYPE = " + type + ";";
		
		price = 0; //TODO: determine from roomQry
		numBeds = 0; //TODO: determine from roomQry
		this.setNumRooms(numRooms);
		
		if (this.isExtraRoom()) {
			price += 35;
			numBeds++;
		}
		
	}
	
	public boolean isExtraRoom() {
		return isExtraRoom;
	}

	public Integer getNumBeds() {
		return numBeds;
	}

	public Double getPrice() {
		Double ans = (double) price;
		if (this.isAllInPeak())
			ans *= 1.4;
		return ans;
	}
	
	public static Double pricePerNight(int price, long checkin, long checkout) {
		Double ans = (double) price;
		if (BookingRequest.isAllInPeak(checkin, checkout))
			ans *= 1.4;
		return ans;
	}

	private boolean isAllInPeak() {
		String peakDateQry = "SELECT * FROM DISCOUNTS;";
		// TODO are all dates in peak?
		return false;
	}
	
	private static boolean isAllInPeak(long checkin, long checkout) {
		String peakDateQry = "SELECT * FROM DISCOUNTS;";
		// TODO are all dates in peak?
		return false;
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
	
	public int getTotalPrice() {
		return numRooms * price * this.getNumNights(); //TODO: account for number of peak hours, etc
	}
	
	public static int getTotalPrice(int p, long cin, long cout, int numR) {
		String peakQry = "SELECT * FROM DISCOUNT;";
		return numR * p * (int)(cout - cin)/(24*60*60*1000);//TODO: account for number of peak hours, etc
		
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