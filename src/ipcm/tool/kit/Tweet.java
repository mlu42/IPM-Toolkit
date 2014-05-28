package ipcm.tool.kit;

import java.net.URL;
import java.util.Date;
import java.util.GregorianCalendar;

public class Tweet implements Comparable<Tweet>{

	private String user;
    private String screenName;
	private String text;
	private String id;
	private URL link;
    private URL thumbnailURL;
    private GregorianCalendar date;

    public Tweet() {
		setUser("");
		setText("");
		setId("");
	}
	
	public Tweet(String _user, String _text, String _id) {
		setUser(_user);
		setText(_text);
		setId(_id);
		try{
			setLink(new URL("http://twitter.com/" + user + "/status/" + id));			
		}catch(Exception e){
			e.printStackTrace();
		}
	}

    public Tweet(String _user, String _text, String _id, String _thumb) {
        setUser(_user);
        setText(_text);
        setId(_id);
        try{
            setLink(new URL("http://twitter.com/" + user + "/status/" + id));
            setThumbnailURL(new URL(_thumb));
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public Tweet(String _user, String _text, String _id, String _thumb, Date _date) {
        setUser(_user);
        setText(_text);
        setId(_id);
        try{
            setLink(new URL("http://twitter.com/" + user + "/status/" + id));
            setThumbnailURL(new URL(_thumb));
        }catch(Exception e){
            e.printStackTrace();
        }
        setDate(new GregorianCalendar(_date.getYear(), _date.getMonth(), _date.getDate(), _date.getHours(), _date.getMinutes(), _date.getSeconds()));
    }

    public Tweet(String _user, String _screenName, String _text, String _id, String _thumb, Date _date) {
        setUser(_user);
        setScreenName(_screenName);
        setText(_text);
        setId(_id);
        try{
            setLink(new URL("http://twitter.com/" + user + "/status/" + id));
            setThumbnailURL(new URL(_thumb));
        }catch(Exception e){
            e.printStackTrace();
        }
        setDate(new GregorianCalendar(_date.getYear(), _date.getMonth(), _date.getDate(), _date.getHours(), _date.getMinutes(), _date.getSeconds()));
    }

	public String getUser()	{
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

    public String getScreenName(){
        return screenName;
    }

    public void setScreenName(String _sn){
        this.screenName = _sn;
    }

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public URL getLink() {
		return link;
	}

	public void setLink(URL link) {
		this.link = link;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

    public URL getThumbnailURL() {
        return thumbnailURL;
    }

    public void setThumbnailURL(URL thumbnailURL) {
        this.thumbnailURL = thumbnailURL;
    }

    public GregorianCalendar getDate(){
        return date;
    }

    public void setDate(GregorianCalendar cal){
        this.date = cal;
    }

    public int compareTo(Tweet other){
        return -this.date.compareTo(other.date);
    }
	
}
