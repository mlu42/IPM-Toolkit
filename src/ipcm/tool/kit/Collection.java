package ipcm.tool.kit;

/*
Defines a collection in the Bugwood Images database
 */

public class Collection {

	private long id; // Refers to the ID of the collection within the group of saved collections
	private String title; // The title of the collection e.g. "IPM Toolkit Featured Images
	private String collOrSub; // Whether or not this is a collection or a subject number
	private String number; // The number of the collection
	
	public Collection(long _id, String _title, String _collOrSub, String _number) {
		id = _id;
		setTitle(_title);
		setCollOrSub(_collOrSub);
		setNumber(_number);
	}

    // Basic getters/setters

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getCollOrSub() {
		return collOrSub;
	}

	public void setCollOrSub(String collOrSub) {
		this.collOrSub = collOrSub;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

}
