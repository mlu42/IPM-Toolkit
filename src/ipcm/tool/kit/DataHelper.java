package ipcm.tool.kit;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataHelper {
	//Declaring constants for the database name, table name,
	//and the version of the database
	private static final String DATABASE_NAME = "myDatabase.db";
	private static final String TIMES_TABLE = "times";
	private static final String CROP_MANAGER_TABLE = "articles";
	private static final String TWEET_TABLE = "tweets";
	private static final String VIDEO_TABLE = "videos";
	private static final String PLAYLIST_TABLE = "playlists";
	private static final String PUBLICATION_TABLE = "publications";
	private static final String COLLECTION_TABLE = "collections";
    private static final String NEWS_FEEDS_TABLE = "news_feeds";
    private static final String TWITTER_FEEDS_TABLE = "twitter_feeds";
    private static final String YOUTUBE_CHANNELS_TABLE = "youtube_channels";
	private static final int DATABASE_VERSION = 1;

	//The name and column index of each column in the times table
	public static final String KEY_ID = "_id";
	public static final int ID_COLUMN = 0;
	public static final String KEY_TIME = "time";
	public static final int TIME_COLUMN = 1;
	
	//The name and column index of each column in the article table
	//public static final String KEY_ID = "_id";
	//public static final int ID_COLUMN = 0;
	public static final String KEY_TITLE = "title";
	public static final int TITLE_COLUMN = 1;
	public static final String KEY_DATE = "date";
	public static final int DATE_COLUMN = 2;
	public static final String KEY_ARTICLE_TIME = "time";
	public static final int ARTICLE_TIME_COLUMN = 3;
	public static final String KEY_LINK = "link";
	public static final int LINK_COLUMN = 4;
	
	//The name and column index of each column in the tweet table
	//public static final String KEY_ID = "_id";
	//public static final int ID_COLUMN = 0;
	public static final String KEY_USER = "user";
	public static final int USER_COLUMN = 1;
    public static final String KEY_SCREENNAME = "screenname";
    public static final int SCREENNAME_COLUMN = 2;
	public static final String KEY_TEXT = "text";
	public static final int TEXT_COLUMN = 3;
	public static final String KEY_TWEETLINK = "tweetlink";
	public static final int TWEETLINK_COLUMN = 4;
    public static final String KEY_THUMBURL = "thumb";
    public static final int THUMB_COLUMN = 5;
	
	//The name and column index of each column in the video table
	//public static final String KEY_ID = "_id";
	//public static final int ID_COLUMN = 0;
	//public static final String KEY_TITLE = "title";
	//public static final int TITLE_COLUMN = 1;
	public static final String KEY_PLAYLIST = "playlist";
	public static final int PLAYLIST_COLUMN = 2;
	public static final String KEY_URL = "url";
	public static final int URL_COLUMN = 3;
	public static final String KEY_THUMBNAILURL = "thumbnailurl";
	public static final int THUMBNAILURL_COLUMN = 4;
	
	//The name and column index of each column in the playlist table
	//public static final String KEY_ID = "_id";
	//public static final int ID_COLUMN = 0;
	//public static final String KEY_TITLE = "title";
	//public static final int TITLE_COLUMN = 1;
	public static final String KEY_FEEDURL = "feedurl";
	public static final int FEEDURL_COLUMN = 2;
	
	//The name and column index of each column in the publication table
	//public static final String KEY_ID = "_id";
	//public static final int ID_COLUMN = 0;
	//public static final String KEY_TITLE = "title";
	//public static final int TITLE_COLUMN = 1;
	public static final String KEY_PUBURL = "feedurl";
	public static final int PUBURL_COLUMN = 2;
	
	//The name and column index of each column in the saved collections/subjects table
	//public static final String KEY_ID = "_id";
	//public static final int ID_COLUMN = 0;
	//public static final String KEY_TITLE = "title";
	//public static final int TITLE_COLUMN = 1;
	public static final String KEY_COLLORSUB = "collorsub";
	public static final int COLLORSUB_COLUMN = 2;
	public static final String KEY_NUMBER = "number";
	public static final int NUMBER_COLUMN = 3;

    //The name and column index of each column in the news feeds table
    //public static final String KEY_ID = "_id";
    //public static final int ID_COLUMN = 0;
    public static final String KEY_FEED_URL = "feed_url";
    public static final int FEED_URL_COLUMN = 1;
    public static final String KEY_FEED_TYPE = "feed_type";
    public static final int FEED_TYPE_COLUMN = 2;
    public static final String KEY_FEED_DESCRIP = "feed_descrip";
    public static final int FEED_DESCRIP_COLUMN = 3;

    //The name and column index of each column in the Twitter feeds table
    //public static final String KEY_ID = "_id";
    //public static final int ID_COLUMN = 0;
    //public static final String KEY_FEED_URL = "feed_url";
    //public static final int FEED_URL_COLUMN = 1;

    //The name and column index of each column in the YouTube channels table
    //public static final String KEY_ID = "_id";
    //public static final int ID_COLUMN = 0;
    public static final String KEY_FEED_ID = "feed_url";
    public static final int FEED_ID_COLUMN = 1;
	
	//Rows
	public static final int ROW_CROPMANAGER = 1;
	public static final int ROW_TWITTER = 2;
	public static final int ROW_ALLPLAYLISTS = 3;
	public static final int ROW_PESTS = 4;
	public static final int ROW_CROPHEALTH = 5;
	public static final int ROW_INVASIVEWEEDS = 6;	
	public static final int ROW_PUBLICATIONS = 7;

	//SQL statement to create a new database
	private static final String PUBLICATION_TABLE_CREATE =
		"CREATE TABLE " + PUBLICATION_TABLE + "(" + 
		KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
		KEY_TITLE + " TEXT, " + 
		KEY_PUBURL + " TEXT" +
		");";
	
	private static final String PLAYLIST_TABLE_CREATE =
		"CREATE TABLE " + PLAYLIST_TABLE + "(" +
		KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
		KEY_TITLE + " TEXT, " +
		KEY_FEEDURL + " TEXT" +
		");";
	
	private static final String VIDEO_TABLE_CREATE = 
		"CREATE TABLE " + VIDEO_TABLE + "(" +
		KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
		KEY_TITLE + " TEXT, " +
		KEY_PLAYLIST + " TEXT, " +
		KEY_URL + " TEXT, " +
		KEY_THUMBNAILURL + " TEXT" +
		");";
	
	private static final String TIMES_TABLE_CREATE = 
		"CREATE TABLE " + TIMES_TABLE + "(" +
		KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
		KEY_TIME + " INTEGER" +
		");";
	
	private static final String CROP_MANAGER_TABLE_CREATE = 
		"CREATE TABLE " + CROP_MANAGER_TABLE + "(" +
		KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
		KEY_TITLE + " TEXT, " +
		KEY_DATE + " TEXT, " +
		KEY_ARTICLE_TIME + " TEXT, " +
		KEY_LINK + " TEXT" +
		");";
	
	private static final String TWEET_TABLE_CREATE =
		"CREATE TABLE " + TWEET_TABLE + "(" +
		KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
		KEY_USER + " TEXT, " +
        KEY_SCREENNAME + " TEXT, " +
		KEY_TEXT + " TEXT, " +
		KEY_TWEETLINK + " TEXT, " +
        KEY_THUMBURL + " TEXT" +
		");";
	
	private static final String COLLECTION_TABLE_CREATE =
		"CREATE TABLE " + COLLECTION_TABLE + "(" +
		KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
		KEY_TITLE + " TEXT, " +
		KEY_COLLORSUB + " TEXT, " +
		KEY_NUMBER + " TEXT" +
		");";

    private static final String NEWS_FEED_TABLE_CREATE =
        "CREATE TABLE " + NEWS_FEEDS_TABLE + "(" +
        KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
        KEY_FEED_URL + " TEXT, " +
        KEY_FEED_TYPE + " TEXT," +
        KEY_FEED_DESCRIP + " TEXT" +
        ");";

    private static final String TWITTER_FEEDS_TABLE_CREATE =
        "CREATE TABLE " + TWITTER_FEEDS_TABLE + "(" +
        KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
        KEY_FEED_URL + " TEXT" +
        ");";

    private static final String YOUTUBE_CHANNELS_TABLE_CREATE =
        "CREATE TABLE " + YOUTUBE_CHANNELS_TABLE + "(" +
        KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
        KEY_FEED_ID + " TEXT" +
        ");";

	//Variable to hold the database instance
	private static SQLiteDatabase db;
	//Context of the application using the database
	private final Context context;
	//Database open/upgrade helper;
	private static DBHelper dbHelper;
    
	// Constructor for a DataHelper
	// @param _context: the context in which the DataHelper will operate
	public DataHelper(Context _context){
		context = _context;
		dbHelper = new DBHelper(context);
	}
	
	//The following two methods are interchangeable.
	//I just use them separately because it helps keep
	//things in order a little better.
	
	//Opens a DataHelper that is writable
	public DataHelper openForWrite() throws SQLException{
		db = dbHelper.getWritableDatabase();
		return this;
	}
	
	//Opens a DataHelper that is readable
	public DataHelper openForRead() throws SQLException{
		db = dbHelper.getReadableDatabase();
		return this;
	}
    
	//Closes the database.
	public void close(){
		//db.close();
	}

    public void init(){
        insertTime();
        insertTime();
        insertTime();
        insertTime();
        insertTime();
        insertTime();
        insertTime();

        // Insert default feeds
        insertNewsFeed("http://ipcm.wisc.edu/feed/", "wp_rss");
        insertTwitterFeed("WisCropMan$WisAg");
        insertYoutubeChannel("uwipm");

    }

	/*
	 * Inserts a quote into the database.
	 * @param n: the name of the person who said the quote.
	 * @param q: the quote.
	 */
	public long insertTime(){
		openForWrite();
		
		ContentValues price = new ContentValues();		
		price.put(KEY_TIME, 0);
		
		long a = db.insert(TIMES_TABLE, null, price);
		close();
		return a;
	}
	
	public long insertCollection(String _title, String _collOrSub, String _number){
		openForWrite();
		
		ContentValues coll = new ContentValues();		
		coll.put(KEY_TITLE, _title);
		coll.put(KEY_COLLORSUB, _collOrSub);
		coll.put(KEY_NUMBER, _number);
		
		long a = db.insert(COLLECTION_TABLE, null, coll);
		Log.d("_id", ((Long)a).toString());
		close();
		return a;
	}

    public long insertNewsFeed(String url, String type){
        openForWrite();

        ContentValues coll = new ContentValues();
        coll.put(KEY_FEED_URL, url);
        coll.put(KEY_FEED_TYPE, type);
        coll.put(KEY_FEED_DESCRIP, "");

        long a = db.insert(NEWS_FEEDS_TABLE, null, coll);

        close();

        return a;
    }

    public long insertTwitterFeed(String url){
        openForWrite();

        ContentValues coll = new ContentValues();
        coll.put(KEY_FEED_URL, url);

        long a = db.insert(TWITTER_FEEDS_TABLE, null, coll);

        close();

        return a;
    }

    public long insertYoutubeChannel(String url){
        openForWrite();

        ContentValues coll = new ContentValues();
        coll.put(KEY_FEED_ID, url);

        long a = db.insert(YOUTUBE_CHANNELS_TABLE, null, coll);

        close();

        return a;
    }
	
	public void removeCollection(long _rowIndex){
		openForWrite();		
		db.delete(COLLECTION_TABLE, KEY_ID + "=" + _rowIndex, null);
		close();
	}

    public void removeNewsFeed(long _rowIndex){
        openForWrite();
        db.delete(NEWS_FEEDS_TABLE, KEY_ID + "=" + _rowIndex, null);
        close();
    }

    public void removeTwitterFeed(long _rowIndex){
        openForWrite();
        db.delete(TWITTER_FEEDS_TABLE, KEY_ID + "=" + _rowIndex, null);
        close();
    }

    public void removeYoutubeChannel(String _name){
        openForWrite();
        db.delete(YOUTUBE_CHANNELS_TABLE, KEY_FEED_ID + "='" + _name + "'", null);
        close();
    }
    
	/*
	 * Removes the selected quote from the database
	 * @param _rowIndex: the rowIndex of the quote to be removed.
	 */
	public void removeTime(long _rowIndex){
		openForWrite();
		db.delete(TIMES_TABLE, KEY_ID + "=" + _rowIndex, null);
		close();
	}
	
	//Removes all of the quotes from the database.
	public void removeAllTimes(){
		openForWrite();
		db.delete(TIMES_TABLE, null, null);
		close();
	}
    
	//Returns a cursor over the entire database.
	public Cursor getAllTimes(){
        openForWrite();
		Cursor q = db.query(TIMES_TABLE,
				null, null, null, null, null, null);		
		q.moveToFirst();
		//close();
		return q;
	}
    
	//Returns a cursor over the selected quote.
	//@param _rowIndex: the rowIndex of the quote to be returned.
	public Cursor getCursorTime(int _rowIndex){
        openForWrite();
		
		Cursor c = db.query(TIMES_TABLE, null, KEY_ID + "=" + _rowIndex, 
				null, null, null, null);
		
		//c.close();
        close();
		return c;	
	}	

	public long getTime(int _rowIndex){
        openForWrite();
		
		Cursor c = getAllTimes();
		c.moveToPosition(_rowIndex - 1);
		long time = c.getLong(TIME_COLUMN);
		
		//c.close();
		close();
		return time;
	}
	
	public long getTimeSince(int _rowIndex){
        openForWrite();
		long time = System.currentTimeMillis();
		long timeSince = getTime(_rowIndex);
		close();
		
		return time - timeSince;
	}
	
	public boolean updateTime(long _rowIndex){
		openForWrite();
		ContentValues time = new ContentValues();
		time.put(KEY_TIME,  System.currentTimeMillis());
		db.update(TIMES_TABLE, time, KEY_ID + "=" + _rowIndex, null);
		close();
		return true;
	}

    public boolean updateNewsFeed(long _rowIndex, NewsFeed f){
        openForWrite();

        ContentValues feed = new ContentValues();

        feed.put(KEY_FEED_URL, f.url);
        feed.put(KEY_FEED_TYPE, f.feedType);
        feed.put(KEY_FEED_DESCRIP, f.descrip);

        db.update(NEWS_FEEDS_TABLE, feed, KEY_ID + "=" + _rowIndex, null);

        close();
        return true;
    }

    public boolean updateTwitterFeed(long _rowIndex, TwitterFeed f){
        openForWrite();

        ContentValues feed = new ContentValues();

        String url = f.user;

        if(f.listName != null && !f.listName.equals(""))
            url += "$" + f.listName;

        feed.put(KEY_FEED_URL, url);

        db.update(TWITTER_FEEDS_TABLE, feed, KEY_ID + "=" + _rowIndex, null);

        close();
        return true;
    }
	
	public void saveCropManagerArticles(ArrayList<CropManagerArticle> _articles){
		openForWrite();
		
		db.delete(CROP_MANAGER_TABLE, null, null);
		
		for(CropManagerArticle article: _articles)
		{
			ContentValues art = new ContentValues();
			
			art.put(KEY_TITLE, article.getTitle());
			art.put(KEY_DATE, article.rawDate);
			art.put(KEY_LINK, article.getLink());
			
			db.insert(CROP_MANAGER_TABLE, null, art);
		}
		
		close();
	}
	
	public void saveTweets(ArrayList<Tweet> _tweets){
		openForWrite();
		
		db.delete(TWEET_TABLE, null, null);
		
		for(Tweet tweet: _tweets)
		{
			ContentValues twee = new ContentValues();
			
			twee.put(KEY_USER, tweet.getUser());
            twee.put(KEY_SCREENNAME, tweet.getScreenName());
			twee.put(KEY_TEXT, tweet.getText());
			twee.put(KEY_TWEETLINK, tweet.getId());
            twee.put(KEY_THUMBURL, tweet.getThumbnailURL().toString());
			
			db.insert(TWEET_TABLE, null, twee);
		}
		
		close();
	}
	
	public void saveAllYoutubeVids(ArrayList<YoutubeVideo> _videos){
		openForWrite();
		
		db.delete(VIDEO_TABLE, null, null);
		
		for(YoutubeVideo video: _videos)
		{
			ContentValues vid = new ContentValues();
			
			vid.put(KEY_TITLE, video.getName());
			vid.put(KEY_PLAYLIST, video.getPlaylist());
			vid.put(KEY_URL, video.getUrl());
			vid.put(KEY_THUMBNAILURL, video.getThumbnailLink());
			
			db.insert(VIDEO_TABLE, null, vid);
		}
		
		close();
	}
	
	public void saveAllPlaylists(ArrayList<Playlist> _playlists){
		openForWrite();
		
		db.delete(PLAYLIST_TABLE, null, null);
		
		for(Playlist playlist: _playlists)
		{
			ContentValues list = new ContentValues();
			
			list.put(KEY_TITLE, playlist.getTitle());
			list.put(KEY_FEEDURL, playlist.getUrl());
			
			db.insert(PLAYLIST_TABLE, null, list);
		}
		
		close();
	}
	
	public void savePublications(ArrayList<Publication> _publications){
		openForWrite();
		
		db.delete(PUBLICATION_TABLE, null, null);
		
		for(Publication pub: _publications)
		{
			ContentValues publication = new ContentValues();
			
			publication.put(KEY_TITLE, pub.getTitle());
			publication.put(KEY_PUBURL, pub.getLink());
			
			db.insert(PUBLICATION_TABLE, null, publication);
		}
		
		close();
	}
	
	public ArrayList<Playlist> getAllPlaylists(){
        openForWrite();
		
		ArrayList<Playlist> playlists = new ArrayList<Playlist>();
		
		Cursor c = db.query(PLAYLIST_TABLE, null, null, null, null, null, null);
		c.moveToFirst();
		
		while(!c.isAfterLast())
		{
			Playlist list = new Playlist(c.getString(TITLE_COLUMN), c.getString(FEEDURL_COLUMN));
			playlists.add(list);
			c.moveToNext();
		}
		
		c.close();
		close();
		return playlists;
	}
	
	public ArrayList<Publication> getAllPublications(){
        openForWrite();
		
		ArrayList<Publication> publications = new ArrayList<Publication>();
		
		Cursor c = db.query(PUBLICATION_TABLE, null, null, null, null, null, null);
		c.moveToFirst();
		
		while(!c.isAfterLast())
		{
			Publication pub = new Publication(c.getString(TITLE_COLUMN), c.getString(PUBURL_COLUMN));
			publications.add(pub);
			c.moveToNext();
		}
		
		c.close();
		close();
		return publications;
	}
	
	public ArrayList<YoutubeVideo> getAllYoutubeVideos(){
        openForWrite();
		
		Cursor c = db.query(VIDEO_TABLE, null, null, null, null, null, null);
		ArrayList<YoutubeVideo> videos = new ArrayList<YoutubeVideo>();
		c.moveToFirst();
		
		while(!c.isAfterLast())
		{
			YoutubeVideo vid = new YoutubeVideo(c.getString(TITLE_COLUMN), c.getString(PLAYLIST_COLUMN), c.getString(URL_COLUMN), c.getString(THUMBNAILURL_COLUMN));
			videos.add(vid);
			c.moveToNext();
		}
		
		c.close();
		close();
		return videos;
	}
	
	public ArrayList<CropManagerArticle> getCropManagerArticles(){
        openForWrite();
		
		Cursor c = db.query(CROP_MANAGER_TABLE, null, null, null, null, null, null);
		ArrayList<CropManagerArticle> articles = new ArrayList<CropManagerArticle>();
		c.moveToFirst();
		
		while(!c.isAfterLast())
		{
			CropManagerArticle art = new CropManagerArticle(c.getString(TITLE_COLUMN), c.getString(DATE_COLUMN), c.getString(LINK_COLUMN));
			articles.add(art);
			c.moveToNext();
		}
		
		c.close();
		close();
		return articles;
	}
	
	public ArrayList<Tweet> getTweets(){
        openForWrite();
		
		Cursor c = db.query(TWEET_TABLE, null, null, null, null, null, null);
		ArrayList<Tweet> tweets = new ArrayList<Tweet>();
		c.moveToFirst();
		
		while(!c.isAfterLast())
		{
			Tweet twee = new Tweet(c.getString(USER_COLUMN), c.getString(SCREENNAME_COLUMN), c.getString(TEXT_COLUMN), c.getString(TWEETLINK_COLUMN), c.getString(THUMB_COLUMN), new Date());
			tweets.add(twee);
			c.moveToNext();
		}
		
		c.close();
		close();
		return tweets;
	}
	
	//Returns a cursor over the entire database.
	public Cursor getAllCollections(){
        openForWrite();
		Cursor q = db.query(COLLECTION_TABLE,
				null, null, null, null, null, null);		
		q.moveToFirst();
		q.close();
		close();
		return q;
	}
	
	public ArrayList<Collection> getCollectionList(){
        openForWrite();
		Cursor c = db.query(COLLECTION_TABLE,
				null, null, null, null, null, null);
		c.moveToFirst();
		
		ArrayList<Collection> cols = new ArrayList<Collection>();
		
		while(!c.isAfterLast())
		{
			Collection newCol = new Collection(c.getLong(ID_COLUMN), c.getString(TITLE_COLUMN), c.getString(COLLORSUB_COLUMN), c.getString(NUMBER_COLUMN));
			cols.add(newCol);
			c.moveToNext();
		}
		
		c.close();
		close();
		
		return cols;
	}

    public ArrayList<NewsFeed> getNewsFeeds(){
        openForWrite();
        Cursor c = db.query(NEWS_FEEDS_TABLE,
                null, null, null, null, null, null);
        c.moveToFirst();

        ArrayList<NewsFeed> newsFeeds = new ArrayList<NewsFeed>();

        while(!c.isAfterLast()){
            newsFeeds.add(new NewsFeed(c.getLong(ID_COLUMN), c.getString(FEED_URL_COLUMN), c.getString(FEED_TYPE_COLUMN), c.getString(FEED_DESCRIP_COLUMN)));
            c.moveToNext();
        }

        c.close();
        close();

        return newsFeeds;
    }

    public ArrayList<TwitterFeed> getTwitterFeeds(){
        openForWrite();
        Cursor c = db.query(TWITTER_FEEDS_TABLE,
                null, null, null, null, null, null);
        c.moveToFirst();

        ArrayList<TwitterFeed> twitterFeeds = new ArrayList<TwitterFeed>();

        while(!c.isAfterLast()){
            twitterFeeds.add(new TwitterFeed(c.getLong(ID_COLUMN), c.getString(FEED_URL_COLUMN)));
            c.moveToNext();
        }

        c.close();
        close();

        return twitterFeeds;
    }

    public ArrayList<YoutubeChannel> getYoutubeChannels(){
        openForWrite();
        Cursor c = db.query(YOUTUBE_CHANNELS_TABLE,
                null, null, null, null, null, null);
        c.moveToFirst();

        ArrayList<YoutubeChannel> youtubeChannels = new ArrayList<YoutubeChannel>();

        while(!c.isAfterLast()){
            youtubeChannels.add(new YoutubeChannel(c.getString(FEED_ID_COLUMN)));
            c.moveToNext();
        }

        c.close();
        close();

        return youtubeChannels;
    }

	private static class DBHelper extends SQLiteOpenHelper{
		// Constructor for a DBHelper
		// @param _context: the context in which the DataHelper will operate
		public DBHelper(Context context)
		{
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}
		
		//Called when no database exists on disk and that helper class
		//needs to create a new one.
		@Override
		public void onCreate(SQLiteDatabase _db)
		{
			_db.execSQL(TIMES_TABLE_CREATE);
			_db.execSQL(CROP_MANAGER_TABLE_CREATE);
			_db.execSQL(TWEET_TABLE_CREATE);
			_db.execSQL(VIDEO_TABLE_CREATE);
			_db.execSQL(PLAYLIST_TABLE_CREATE);
			_db.execSQL(PUBLICATION_TABLE_CREATE);
			_db.execSQL(COLLECTION_TABLE_CREATE);
            _db.execSQL(NEWS_FEED_TABLE_CREATE);
            _db.execSQL(TWITTER_FEEDS_TABLE_CREATE);
            _db.execSQL(YOUTUBE_CHANNELS_TABLE_CREATE);

		}

		//Called when there is a database version mismatch meaning that the version
		//of the database on disk needs to be upgraded to the current version.
		@Override
		public void onUpgrade(SQLiteDatabase _db, int _oldVersion, int _newVersion)
		{
			//Log the version upgrade
			Log.w("TaskDataHelper", "Upgrading from version " + _oldVersion 
					+ " to " +
					+ _newVersion + ", which will destroy all old data");

			//Upgrade the existing database to conform to the new version.
			//Multiple previous versions can be handled by comparing _oldVersion
			//and _newVersion values.
			_db.execSQL("DROP TABLE IF EXISTS" + TIMES_TABLE);
			//Create a new one.
			onCreate(_db);
		}
	}
}