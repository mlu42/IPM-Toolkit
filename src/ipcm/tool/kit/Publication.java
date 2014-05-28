package ipcm.tool.kit;

public class Publication {

	private String title;
	private String link;
	
	public Publication(String _title, String _link) {
		title = _title;
		link = _link;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}
	
}
