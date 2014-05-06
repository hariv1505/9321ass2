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
	private Integer numBeds;
	
	public SearchRes(int cindate, int cinmonth, int cinyear, int coutdate, int coutmonth, int coutyear,
			int city, int numBeds) {
		rooms = new HashMap<String,Integer>();
		this.cindate = cindate;
		this.cinmonth = cinmonth;
		this.cinyear = cinyear;
		this.coutdate = coutdate;
		this.coutmonth = coutmonth;
		this.coutyear = coutyear;
		this.city = city;
		this.numBeds = numBeds;
	}
	
	public Map<String,Integer> getRes() {
		return rooms;
	}
	
	public void setRes(Map<String,Integer> r) {
		rooms = r;
	}

	public void getSearchResults() {
		rooms = this.search(cindate,cinmonth,cinyear,coutdate,coutmonth,coutyear,city,numBeds);
		
	}

	private Map<String, Integer> search(Integer cind, Integer cinm,
			Integer ciny, Integer coutd, Integer coutm,
			Integer couty, Integer ct, Integer numBed) {
		Map<String,Integer> res = new HashMap<String, Integer>();
		// TODO Auto-generated method stub
		// do via db queries
		return res;
	}
}