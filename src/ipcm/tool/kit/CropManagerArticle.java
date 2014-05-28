package ipcm.tool.kit;

import java.util.GregorianCalendar;

public class CropManagerArticle implements Comparable<CropManagerArticle>{

	private String title;
	private GregorianCalendar date;
	private String link;
    public String rawDate;
	
	public CropManagerArticle(String _title, String _date, String _link) {
		title = _title;
		date = parseDate(_date);
		link = _link;
        rawDate = _date;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public GregorianCalendar getDate() {
		return date;
	}
	
	public void setDate(String date) {
		this.date = parseDate(date);
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

    // Converts dates from Strings to more workable Date objects
    // input: dates in the format: "<DoW>, <DoM> <M> <Y> HH:MM:SS"
    // output: a Java date object corresponding to the string
    public GregorianCalendar parseDate(String _date){

        if(_date == null)
            return null;

        String[] tokens = _date.split(" ");

        try{
            Dates d = new Dates();
            //int dayOfWeek = convertDayOfWeek(tokens[0].substring(0, tokens[0].indexOf(",")));
            int dayOfMonth = d.StoIDayOfMonth(tokens[1]);
            int month = d.StoIMonth(tokens[2]);
            int year = d.StoIYear(tokens[3]);
            int[] time = d.StoITime(tokens[4]);
            return new GregorianCalendar(year, month, dayOfMonth, time[0], time[1], time[2]);
        }catch(ArrayIndexOutOfBoundsException e){
            return new GregorianCalendar(2013, 1, 1);
        }
    }

    public int compareTo(CropManagerArticle other){

        if(other.date == null || this.date == null)
            return 0;

        return -this.date.compareTo(other.date);
    }
	
}
