import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import HelperClass.DatabaseHandler2;
import HelperClass.Init;


public class BookingRequest {
	private boolean isExtraBed = false;
	private int numBeds;
	private int numRooms;
	private int price;
	private long checkIn;
	private long checkOut;
	private int city;
	private String type;
	public static Map<Long, Long> publicPeakPeriods;
	private Map<Long, Long> peakPeriods;
	private double peakPremium;
	private double discount;
	
	public BookingRequest(boolean isExtraBed, int numBeds, int numRooms, int price, long checkIn,
			long checkOut, int city, String type) {
		this.isExtraBed = isExtraBed;
		this.numBeds = numBeds;
		this.numRooms = numRooms;
		this.price = price;
		this.checkIn = checkIn;
		this.checkOut = checkOut;
		this.city = city;
		this.type = type;
		this.setPeakPeriods();
	}
	
	public BookingRequest(long checkIn, long checkOut, int city) {
		this.checkIn = checkIn;
		this.checkOut = checkOut;
		this.city = city;
		this.setPeakPeriods();
	}
	
	public double getPeakPrem() {
		return peakPremium;
	}
	
	public double getDiscount() {
		return discount;
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
		this.type = roomToBookDetails[0];
		
		this.numRooms = Integer.parseInt(roomToBookDetails[2]);
		
		String roomQry = "SELECT * FROM ROOMS "
				+ "WHERE TYPE = '" + this.type + "'";
		DatabaseHandler2 dh = new DatabaseHandler2();
		Connection con;
		try {
			con = dh.GetDbConnection();
		
			try {
				PreparedStatement ps = con.prepareStatement(roomQry);
				ResultSet rs = ps.executeQuery();
				rs.next();
				price = Integer.parseInt(rs.getString("PRICE").trim());
				numBeds = Integer.parseInt(rs.getString("NUMBEDS").trim());
			} catch (SQLException e) {
				price = 0;
				numBeds = 0;
				System.err.println("Cannot set price and numbeds");
			}
			dh.CloseDbConnection(con);
		} catch (UnsupportedEncodingException | InstantiationException
				| IllegalAccessException e1) {
			e1.printStackTrace();
		}
		
		this.setNumRooms(numRooms);
		
	}
	
	public boolean isExtraBed() {
		return isExtraBed;
	}

	public Integer getNumBeds() {
		if (this.isExtraBed()) {
			return numBeds+1;
		}
		return numBeds;
	}

	public int getPricePerNight() {
		return price;
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
		
		return cal.get(Calendar.DATE) + "/" + (cal.get(Calendar.MONTH)+1) + "/" + cal.get(Calendar.YEAR);
	}

	public String getCheckOutToString() {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(checkOut);
		
		return cal.get(Calendar.DATE) + "/" + (cal.get(Calendar.MONTH)+1) + "/" + cal.get(Calendar.YEAR);
	}
	
	public double getTotalPrice() {
		double ans = 0;
		DatabaseHandler2 dh = new DatabaseHandler2();
		Connection con;
		try {
			con = dh.GetDbConnection();
			for (long i = checkIn; i <= checkOut; i += (24*60*60*1000)) {
				for (long start : peakPeriods.keySet()) {
					if (start > i) {
						ans += price;
						if (isExtraBed)  ans += 35;
						break;
					} else {
						if (peakPeriods.get(start) > i) {
							ans += price*1.4;
							this.peakPremium += (double)price*0.4;
							if (isExtraBed)  ans += 35;
							break;
						}
					}
				}
				
				String discQuery = "SELECT * FROM DISCOUNT";
				PreparedStatement ps;
				ResultSet rs;
				try {
					ps = con.prepareStatement(discQuery);
					rs = ps.executeQuery();
					
					while (rs.next()) {
						if (rs.getString("ROOMTYPE") == this.getType()) {
							if ((getMillis(Integer.parseInt(rs.getString("STARTDATE"))) < i) &&
							(getMillis(Integer.parseInt(rs.getString("ENDDATE"))) > i)) {
								this.discount += (double)Integer.parseInt(rs.getString("DISCOUNTRATE"))/100 * price;
							}
						}
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
				
			}
			dh.CloseDbConnection(con);
		} catch (UnsupportedEncodingException | InstantiationException
				| IllegalAccessException e1) {
			e1.printStackTrace();
		}
		
		ans -= this.discount;
		
		return ans;
	}
	
	public static double getTotalPrice(int p, long cin, long cout, int numR, String type) {
		double ans = 0;
		DatabaseHandler2 dh = new DatabaseHandler2();
		Connection con;
		try {
			con = dh.GetDbConnection();
			
			for (long i = cin; i <= cout; i += (24*60*60*1000)) {
				for (long start : publicPeakPeriods.values()) {
					if (i >= start) {
						if (publicPeakPeriods.get(start) <= i) {
							ans += p*1.4;
							break;
						} else {
							ans += p;
							break;
						}
					}
				}
				String discQuery = "SELECT * FROM DISCOUNT";
				PreparedStatement ps;
				ResultSet rs;
				try {
					ps = con.prepareStatement(discQuery);
					rs = ps.executeQuery();
					
					while (rs.next()) {
						if (rs.getString("ROOMTYPE") == type) {
							if ((getMillis(Integer.parseInt(rs.getString("STARTDATE"))) < i) &&
							(getMillis(Integer.parseInt(rs.getString("ENDDATE"))) > i)) {
								ans -= (double)Integer.parseInt(rs.getString("DISCOUNTRATE"))/100 * p;
							}
						}
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			dh.CloseDbConnection(con);
		} catch (UnsupportedEncodingException | InstantiationException
				| IllegalAccessException e1) {
			e1.printStackTrace();
		}
		return ans;
	}

	public int getCity() {
		return city;
	}

	public void setCity(int city) {
		this.city = city;
	}

	public String getType() {
		return this.type;
	}
	
	public static long getMillis(int date) {
		Calendar cal = Calendar.getInstance();
		cal.set(date % 100, (date % 1000)/100, date / 1000, 0, 0, 0);
		cal.set(Calendar.MILLISECOND,0);
		return cal.getTimeInMillis();
	}
	
	public static void LoadDbDriver() {
        // Load the driver
		String driver = "org.apache.derby.jdbc.EmbeddedDriver";
		
        try {
            Class.forName(driver).newInstance();
            System.out.println(driver + " loaded.");
        } catch (java.lang.ClassNotFoundException e) {
            System.err.print("ClassNotFoundException: ");
            System.err.println(e.getMessage());
            System.out.println("\n Make sure your CLASSPATH variable " +
                "contains %DERBY_HOME%\\lib\\derby.jar (${DERBY_HOME}/lib/derby.jar). \n");
        } catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	public static Connection GetDbConnection() {
		
		LoadDbDriver();
        Connection conn = null;

        // Start the database and set up users, then close database
        try {
        	String dbName=Init.getWebInfPath()+ "/9321ass2";
            String connectionURL = "jdbc:derby:"+dbName;
            System.out.println("Trying to connect to " + connectionURL);
            conn = DriverManager.getConnection(connectionURL);
            System.out.println("Connected to database " + connectionURL);
            return conn;
        }catch(Exception e){
        	System.out.println(e);
        }
        return null;
		//
	}
		
	public static void CloseDbConnection(Connection conn) {
		try {// shut down the database
            conn.close();
            System.out.println("Closed connection");

            /* In embedded mode, an application should shut down Derby.
               Shutdown throws the XJ015 exception to confirm success. */
            boolean gotSQLExc = false;
            try {
                DriverManager.getConnection("jdbc:derby:;shutdown=true");
                DriverManager.getConnection("exit");
            } catch (SQLException se) {
                if ( se.getSQLState().equals("XJ015") ) {
                    gotSQLExc = true;
                }
            }
            if (!gotSQLExc) {
                 System.out.println("Database did not shut down normally");
            } else {
                 System.out.println("Database shut down normally");
            }

            // force garbage collection to unload the EmbeddedDriver
            //  so Derby can be restarted
            System.gc();
        } catch (Throwable e) {
        	System.out.println(e);;
            System.exit(1);
        }
	}
}

