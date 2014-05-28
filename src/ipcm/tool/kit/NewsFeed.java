package ipcm.tool.kit;

/*
An object to hold information about a News Feed that the user has saved.
 */
public class NewsFeed {

    // Constants for types of sources
    public static final String WP_RSS = "wp_rss"; // A standard Wordpress RSS feed. ex: http://ipcm.wisc.edu/feed

    // I opt for public fields/constructors rather than use getters and setters
    public String url;              // The URL for the feed
    public String feedType;         // The type of feed (WP_RSS, etc.)
    public String descrip = "";     // A description of the feed to be displayed for the user
    public long id;                 // <find out why this is useful>

    //////////////////////////////////////////////////////////////////////////////
    // Constructors
    public NewsFeed(){
        url = "";
        feedType = "";
        descrip = "";
    }

    public NewsFeed(String url, String feedType){
        this.url = url;
        this.feedType = feedType;
    }

    public NewsFeed(String url, String feedType, String feedDescrip){
        this.url = url;
        this.feedType = feedType;
        this.descrip = feedDescrip;
    }

    public NewsFeed(long id, String url, String feedType, String feedDescrip){
        this.url = url;
        this.feedType = feedType;
        this.descrip = feedDescrip;
        this.id = id;
    }
    ///////////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////////
    // Methods

    // < none yet >

    ///////////////////////////////////////////////////////////////////////////////

}
