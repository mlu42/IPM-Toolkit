package ipcm.tool.kit;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class Picture implements Parcelable {

    private String IMAGE_URL_PREFIX = "http://bugwoodcloud.org/images/";

	private String title;
	private String author;
	private URL imageURL;
    private URL hdURL;
	private URL thumbnailURL;
	private Bitmap thumbnail;
	private Bitmap picture;
	private int position;
	private Picture pair;
	
	private PictureAdapter adapter;
	
	public Picture(String _title, String _author, String _imgURL, String _thumbURL, int _position){

        IMAGE_URL_PREFIX = _imgURL.substring(0, _imgURL.indexOf("/images/") + 8);

		title = _title;
		author = _author.substring(_author.indexOf("(") + 1, _author.length() - 1);

        // Parse imgURL to HD URL
		String HDurl = IMAGE_URL_PREFIX + "1536x1024/";
        String imgNum = _imgURL.substring(_imgURL.lastIndexOf("/") + 1);
        HDurl += imgNum;

		try{
            hdURL = new URL(HDurl);
			imageURL = new URL(_imgURL);
			thumbnailURL = new URL(_thumbURL);
		}catch(MalformedURLException e){
			Log.d("badURL", "BADURL");
			Log.d("thumbURL", _thumbURL);
		}
		
		thumbnail = null;
		picture = null;
		position = _position;
		pair = null;
		
	}
	
	public Picture(String _title, String _author, String _imgURL, String _thumbURL, int _position, Picture _pair){

        IMAGE_URL_PREFIX = _imgURL.substring(0, _imgURL.indexOf("/images/") + 8);

		title = _title;
		author = _author.substring(_author.indexOf("(") + 1, _author.length() - 1);

        // Parse imgURL to HD URL
        String HDurl = IMAGE_URL_PREFIX + "1536x1024/";
        String imgNum = _imgURL.substring(_imgURL.lastIndexOf("/") + 1);
        HDurl += imgNum;

		try{
            hdURL = new URL(HDurl);
			imageURL = new URL(_imgURL);
			thumbnailURL = new URL(_thumbURL);
		}catch(MalformedURLException e){
			Log.d("badURL", "BADURL");
			Log.d("thumbURL", _thumbURL);
		}
		
		thumbnail = null;
		picture = null;
		position = _position;
		pair = _pair;
		
	}
	
	public Picture(Picture pic){

        IMAGE_URL_PREFIX = pic.getImageURL().toString().substring(0, pic.getImageURL().toString().indexOf("/images/") + 8);

		title = pic.getTitle();
		author = pic.getAuthor();
		imageURL = pic.getImageURL();
		thumbnailURL = pic.getThumbnailURL();
        hdURL = pic.getHDURL();
		position = pic.position;
		pair = null;
	}
	
	public Picture(Parcel source){
		
		title = source.readString();
		author = source.readString();
		
		try{
            hdURL = new URL(source.readString());
			imageURL = new URL(source.readString());
			thumbnailURL = new URL(source.readString());
		}catch(MalformedURLException e){

		}

        if(imageURL != null)
            IMAGE_URL_PREFIX = imageURL.toString().substring(0, imageURL.toString().indexOf("/images/") + 8);
		
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getAuthor() {
		return author;
	}
	
	public void setAuthor(String author) {
		this.author = author;
	}
	
	public URL getImageURL() {
		return imageURL;
	}
	
	public void setImageURL(URL imageURL) {
		this.imageURL = imageURL;
	}

    public URL getHDURL(){
       return hdURL;
    }

    public void setHDURL(URL hdurl){
        this.hdURL = hdurl;
    }
	
	public URL getThumbnailURL() {
		return thumbnailURL;
	}
	
	public void setThumbnailURL(URL thumbnailURL) {
		this.thumbnailURL = thumbnailURL;
	}
	
	public Picture getPair(){
		return pair;
	}
	
	public void setPair(Picture pic){
		pair = pic;
	}
	
	public int describeContents(){
		return 0;
	}
	
	public void writeToParcel(Parcel target, int flags){
		
		target.writeString(title);
		target.writeString(author);
        target.writeString(hdURL.toString());
		target.writeString(imageURL.toString());
		target.writeString(thumbnailURL.toString());
		
	}
	
	public Bitmap getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(Bitmap thumbnail) {
		this.thumbnail = thumbnail;
	}

	public Bitmap getPicture() {
		return picture;
	}

	public void setPicture(Bitmap picture) {
		this.picture = picture;
	}

	public PictureAdapter getAdapter() {
		return adapter;
	}

	public void setAdapter(PictureAdapter adapter) {
		this.adapter = adapter;
	}

	public static final Creator<Picture> CREATOR = new Creator<Picture>() {

        public Picture createFromParcel(Parcel source) {
            return new Picture(source);
        }

        public Picture[] newArray(int size) {
            return new Picture[size];
        }

    };
    
    public void loadThumbnail(PictureAdapter _adapter){
    	this.adapter = _adapter;
    	
    	if(imageURL != null && !imageURL.equals(""))
    		new ThumbnailLoadTask().execute(imageURL);
    }
    
    private class ThumbnailLoadTask extends AsyncTask<URL, String, Bitmap> {
    	
    	@Override
    	protected void onPreExecute(){
    	
    	}
    	
    	@Override
    	protected Bitmap doInBackground(URL... urls){
    		
    		try{
    			Bitmap b = getBitmapFromURL(urls[0]);
    			return b;
    		}catch(Exception e){
    			e.printStackTrace();
    			return null;
    		}
    		
    	}
    	
    	@Override
    	protected void onPostExecute(Bitmap ret){
    		
    		if(ret != null){

        		
        		Log.d("pu", "oop");
    			thumbnail = ret;
    			if(pair != null)
    				pair.setThumbnail(ret);
    			
    			if(adapter != null)
    				adapter.notifyDataSetChanged();
    		}
    		
    	}
    	
    }
    
    public static Bitmap getBitmapFromURL(URL url) {
    
        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();

            InputStream input = connection.getInputStream();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize=4;
            
            options.inDither = false;
            options.inPurgeable = true;
            options.inInputShareable = true;
    
            Bitmap myBitmap = BitmapFactory.decodeStream(input, null, options);
    
            return myBitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
    
        }
    
    }

  }
