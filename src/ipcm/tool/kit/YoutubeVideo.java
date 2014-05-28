package ipcm.tool.kit;

import android.util.Log;

public class YoutubeVideo {
	
	private String name;
	private String playlist;
	private String url;
	private String thumbnailLink;
    String sectionTitle;
    boolean isSectionHeader = false;

    public YoutubeVideo(String _name) {
		name = _name;
		url = "";
	}

    public YoutubeVideo(String _name, boolean b){
        sectionTitle = _name;
        isSectionHeader = true;
    }
	
	public YoutubeVideo(String _name, String _playlist, String _url, String _thumbnailLink)	{
		name = _name;
		playlist = _playlist;
		url = _url;
		thumbnailLink = _thumbnailLink;
		Log.d("thumb", _thumbnailLink);
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}

	public String getPlaylist() {
		return playlist;
	}

	public void setPlaylist(String playlist) {
		this.playlist = playlist;
	}

	public String getThumbnailLink() {
		return thumbnailLink;
	}

	public void setThumbnailLink(String thumbnailLink) {
		this.thumbnailLink = thumbnailLink;
	}
	
}
