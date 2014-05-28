package ipcm.tool.kit;

import android.util.Log;

import java.util.ArrayList;

public class Playlist {
	
	private String title;
	private String url;
    public ArrayList<YoutubeVideo> videos;
	
	public Playlist(String _title, String _url) {
		Log.d("url", _url);
		setTitle(_title);		
		setUrl(_url);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	

}
