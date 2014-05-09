import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

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
		
		this.search(cindate,cinmonth,cinyear,coutdate,coutmonth,coutyear,city,numRooms,maxPrice);
		
	}

	private void search(Integer cind, Integer cinm,
			Integer ciny, Integer coutd, Integer coutm,
			Integer couty, Integer ct, Integer numRoom, Integer maxP) {
		Calendar cIn = Calendar.getInstance();
		cIn.set(ciny, cinm, cind);
		long cInToMS = cIn.getTimeInMillis(); 
		Calendar cOut = Calendar.getInstance();
		cOut.set(couty, coutm, coutd);
		long cOutToMS = cOut.getTimeInMillis();
		
		rooms.put("Single", 0);
		rooms.put("Twin Bed", 0);
		rooms.put("Suite", 0);
		rooms.put("Queen", 0);
		rooms.put("Executive", 0);
		
		String qry = "SELECT r.TYPE, COUNT(r.TYPE) AS COUNT FROM BOOKINGS b JOIN ROOMS r on (r.ID=b.ROOMID) " +
				"WHERE r.PRICE <=" + maxP + " AND " +
				"b.CHECKIN <" + cInToMS + " AND " +
				"b.CHECKOUT >" + cOutToMS + " AND " +
				"b.CITYID = " + ct + " GROUP BY r.TYPE";
		String allRoomsQry = "SELECT r.TYPE, COUNT(*) AS COUNT FROM ROOMS r"
				+ " GROUP BY r.TYPE";
		
		LoadDbDriver();		
		Connection con = GetDbConnection();
		
		try{
			PreparedStatement ps = con.prepareStatement(qry);
			ResultSet rs = ps.executeQuery();
			ps = con.prepareStatement(allRoomsQry);
			ResultSet rsAll = ps.executeQuery();
			while (rs.next()) {
				rooms.put(rs.getString("TYPE").trim(), (Integer.parseInt(rsAll.getString("COUNT"))
						- Integer.parseInt(rs.getString("COUNT"))));
			}
			while (rsAll.next()) {
				rooms.put(rsAll.getString("TYPE").trim(), (Integer.parseInt(rsAll.getString("COUNT"))));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println(rooms.size());
		System.out.println(rooms.keySet());
		
		CloseDbConnection(con);
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static Connection GetDbConnection() {
		
        String connectionURL = "jdbc:derby:/home/hari/University/7thYear/COMP9321/Labs/Assignment2/WebContent/WEB-INF/9321ass2";
        Connection conn = null;

        // Start the database and set up users, then close database
        try {
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