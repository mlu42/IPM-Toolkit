package ipcm.tool.kit;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.*;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import twitter4j.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class News extends ListActivity{
	
	// The master lists of articles and tweets
	ArrayList<CropManagerArticle> articles = new ArrayList<CropManagerArticle>();
	ArrayList<Tweet> tweets =  new ArrayList<Tweet>();

    // Layout objects
    ProgressBar progBar;
    TextView emptyBox;
    View twitterFooter;
    PullToRefreshListView list;
    ProgressBar footerProgBar;
    TextView footerText;
    CropManagerAdapter articleAdapter;
    TwitterAdapter twitterAdapter;
    ImageView addButton;
    TextView cropManager;
    TextView twitter;
    EditText addFeedInput;
    ImageView xbutton;
    ImageView feedsButton;
    LinearLayout actionBar;
    Spinner newsPicker; ArrayAdapter<String> feedsAdapter;

    // Others
	final DataHelper db = new DataHelper(this);
	public final Context context = this;
    boolean articlesSelected;
    private int twitterPage = 1;
    private boolean hasFooter = false;
    private boolean gettingNextPage = false;

    private int currentTwitterFeed = 0;

    // A cache for Twitter thumbnails
    public static HashMap<URL, Bitmap> twitterThumbnailCache;
	
	// The action performed when an item in the list is clicked on
	protected void onListItemClick(ListView l, View v, int position, long id){

		if(!v.equals(twitterFooter)){
            if(articlesSelected){
                Uri uri = Uri.parse(((CropManagerArticle)getListAdapter().getItem(position)).getLink().toString());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }else{
                Uri uri = Uri.parse(((Tweet)getListAdapter().getItem(position)).getLink().toString());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        }else{
            // Get the next page of results
            twitterPage += 1;
            gettingNextPage = true;
            footerProgBar.setVisibility(View.VISIBLE);
            footerText.setText("Loading more tweets");
            new FetchTweetsTask().execute();
        }
		
	}

    /*
    Determines if the device has an internet connection
    @return true if the device has an internet connection, false otherwise
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /*
    Notifies the user via a toast that no internet connection is available
     */
    private void notifyNoNetworkAvailable(){
        progBar.setVisibility(View.GONE);
        emptyBox.setText("No internet connection");
        Toast toast = Toast.makeText(this, "Internet connection unavailable", 1500);
        toast.show();
    }

    // Reloads the list, called when pulled to refresh
    public void reloadList(View v){
        if(isNetworkAvailable()){
            if(articlesSelected){
                new FetchCropManagerArticlesTask().execute("null");
            }else{
                new FetchTweetsTask().execute("null");
            }
        }else{
            notifyNoNetworkAvailable();
        }
    }

    private void addFooter(){
        if(!hasFooter){
            list.addFooterView(twitterFooter);
            hasFooter = true;
        }
    }

    private void removeFooter(){
        if(hasFooter){
            list.removeFooterView(twitterFooter);
            hasFooter = false;
        }
    }

    // A helper method because Adapter.addAll is not available in API level 8
    private void addAllArticles(ArrayList<CropManagerArticle> _articles){
        articleAdapter.setNotifyOnChange(false);
        for(CropManagerArticle art : _articles)
            articleAdapter.add(art);
        articleAdapter.notifyDataSetChanged();
    }

    // A helper method because Adapter.addAll is not available in API level 8
    private void addAllTweets(ArrayList<Tweet> _tweets){
        twitterAdapter.setNotifyOnChange(false);
        for(Tweet tweet : _tweets)
            twitterAdapter.add(tweet);
        twitterAdapter.notifyDataSetChanged();
    }

	// Run when the activity is initialized
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_news);
		
		// Initializing instance variables
		final Resources res = getResources();
		cropManager = (TextView) findViewById(R.id.crop_manager_button);
		twitter = (TextView) findViewById(R.id.twitter_button);
		list = (PullToRefreshListView) getListView();
        progBar = (ProgressBar) findViewById(R.id.progbar);
        emptyBox = (TextView) findViewById(R.id.empty_text);
        articleAdapter = new CropManagerAdapter(this, R.layout.listitem_articleitem, articles);
        twitterAdapter = new TwitterAdapter(this, R.layout.listitem_twitteritem, tweets);
        addButton = (ImageView)findViewById(R.id.addButton);
        addFeedInput = (EditText)findViewById(R.id.addFeedInput);
        xbutton = (ImageView)findViewById(R.id.Xbutton);
        feedsButton = (ImageView)findViewById(R.id.feedsEditButton);
        actionBar = (LinearLayout)findViewById(R.id.actionBar);
        newsPicker = (Spinner)findViewById(R.id.newsfeed_picker);

        LayoutInflater inflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        twitterFooter = inflater.inflate(R.layout.listitem_footer, null);
        footerProgBar = (ProgressBar)twitterFooter.findViewById(R.id.tweetprogbar);
        footerText = (TextView)twitterFooter.findViewById(R.id.twitter_footer_text);

		articlesSelected = true;
        twitterThumbnailCache = new HashMap<URL, Bitmap>();

		// Set the toggle buttons' background colors appropriately
		cropManager.setBackgroundColor(res.getColor(R.color.toggle_selected));
		twitter.setBackgroundColor(res.getColor(R.color.toggle_unselected));

        // Initialize all values in the database
        Cursor c = db.getAllTimes();
        int co = c.getCount();
        c.close();

        if(co == 0){
            db.init();
        }
		
		list.setCacheColorHint(Color.TRANSPARENT); // Avoid a weird animation when a list item is clicked

        setNewsPickerAdapter();
		
		// The behavior of the crop manager button when clicked
		cropManager.setOnClickListener( new OnClickListener(){
			
			public void onClick(View v)
			{
				articlesSelected = true;
                setListAdapter(articleAdapter);

				cropManager.setBackgroundColor(res.getColor(R.color.toggle_selected));
				twitter.setBackgroundColor(res.getColor(R.color.toggle_unselected));

				// If it has been more than an hour since the list has been refreshed, reload the list from the internet
				if(db.getTimeSince(DataHelper.ROW_CROPMANAGER) > 3600000)
				{
                    if(isNetworkAvailable()){
                        new FetchCropManagerArticlesTask().execute("null");
                    }else{
                        notifyNoNetworkAvailable();
                        articles = db.getCropManagerArticles();
                        Collections.sort(articles);
                        removeFooter();

                        articleAdapter.setNotifyOnChange(false);
                        articleAdapter.clear();
                        addAllArticles(articles);
                    }
				}
				// else, load the cached version of the list in memory
				else
				{
					articles = db.getCropManagerArticles();
                    Collections.sort(articles);
                    removeFooter();

                    articleAdapter.setNotifyOnChange(false);
                    articleAdapter.clear();
                    addAllArticles(articles);
				}

                newsPicker.setVisibility(View.VISIBLE);
			}
			
		});
		
		// The behavior of the twitter button when clicked
		twitter.setOnClickListener( new OnClickListener(){
			
			public void onClick(View v)
			{
				articlesSelected = false;
                setListAdapter(twitterAdapter);

                //addButton.setText("+");
                addFeedInput.setVisibility(View.GONE);
                addFeedInput.setText("");
                xbutton.setVisibility(View.GONE);
                feedsButton.setVisibility(View.VISIBLE);

				cropManager.setBackgroundColor(res.getColor(R.color.toggle_unselected));
				twitter.setBackgroundColor(res.getColor(R.color.toggle_selected));
                twitterPage = 1;
				
				if(db.getTimeSince(DataHelper.ROW_TWITTER) > 3600000)
				{
                    if(isNetworkAvailable()){
                        new FetchTweetsTask().execute("null");
                    }else{
                        notifyNoNetworkAvailable();
                        tweets = db.getTweets();

                        if(tweets.size() > 40){
                            List<Tweet> temp = tweets.subList(0, 40);
                            tweets = new ArrayList<Tweet>(temp);
                        }

                        addFooter();

                        twitterAdapter.setNotifyOnChange(false);
                        twitterAdapter.clear();
                        addAllTweets(tweets);
                    }
				}
				else
				{
					tweets = db.getTweets();

                    if(tweets.size() > 40){
                        List<Tweet> temp = tweets.subList(0, 40);
                        tweets = new ArrayList<Tweet>(temp);
                    }

                    addFooter();

                    twitterAdapter.setNotifyOnChange(false);
                    twitterAdapter.clear();
                    addAllTweets(tweets);
				}

                newsPicker.setVisibility(View.GONE);

			}
			
		});

        // Define what happens when you select an item in the list
        newsPicker.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener(){

            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {

                if(isNetworkAvailable()){
                    new FetchCropManagerArticlesTask().execute("null");
                }

            }

            public void onNothingSelected(AdapterView<?> arg0) {

            }

        });

        list.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                reloadList(list);
            }
        });
		
	}

    public void setNewsPickerAdapter(){

        ArrayList<String> newsFeeds = new ArrayList<String>();
        ArrayList<NewsFeed> feeds = db.getNewsFeeds();
        for(NewsFeed newsFeed : feeds){
            if(newsFeed.descrip == null || newsFeed.descrip.equals(""))
                newsFeeds.add(newsFeed.url);
            else
                newsFeeds.add(newsFeed.descrip);
        }

        feedsAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, newsFeeds);
        feedsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        newsPicker.setAdapter(feedsAdapter);
        newsPicker.setSelection(0);

    }

    // Called when the add button is clicked
    public void addButtonOnClick(View v){

        Log.d("CLICKED", "CLICKED");

        // If the button is currently in the "clicked" state
            if(xbutton.getVisibility() == View.GONE){

            if(!articlesSelected){
                startActivity(new Intent(this, AddTwitterFeed.class));
            }else{

                //cropManager.setVisibility(View.GONE);
                //twitter.setVisibility(View.GONE);
                //addButton.setText("Add feed");
                addFeedInput.setVisibility(View.VISIBLE);
                addFeedInput.setHint("Enter RSS feed");
                xbutton.setVisibility(View.VISIBLE);
                feedsButton.setVisibility(View.GONE);

            }
        }
        // Else we need to attempt to add this to the feed
        else{
            new Feeds(this).testNewsFeed(addFeedInput.getText().toString());
        }

    }

    // Called when the X button is clicked
    public void XbuttonOnClick(View v){
        //cropManager.setVisibility(View.VISIBLE);
        //twitter.setVisibility(View.VISIBLE);
        //addButton.setText("+");
        addFeedInput.setVisibility(View.GONE);
        addFeedInput.setText("");
        xbutton.setVisibility(View.GONE);
        feedsButton.setVisibility(View.VISIBLE);
    }

    public void feedsEditButtonOnClick(View v){
        Intent intent = new Intent(this, FeedList.class);

        if(articlesSelected)
            intent.putExtra("type", "newsfeeds");
        else
            intent.putExtra("type", "twitter");

        startActivityForResult(intent, 1);
    }

    @Override
    public void onStart(){
        super.onStart();

        setListAdapter(articleAdapter);

        // If it has been more than an hour since the list has been refreshed, reload the list from the internet
        if(db.getTimeSince(DataHelper.ROW_CROPMANAGER) > 3600000)
        {
            if(isNetworkAvailable()){
                new FetchCropManagerArticlesTask().execute("null");
            }else{
                notifyNoNetworkAvailable();
                articles = db.getCropManagerArticles();
                removeFooter();

                articleAdapter.setNotifyOnChange(false);
                articleAdapter.clear();
                addAllArticles(articles);
            }
        }
        // else, load the cached version of the list in memory
        else
        {
            articles = db.getCropManagerArticles();
            removeFooter();

            articleAdapter.setNotifyOnChange(false);
            articleAdapter.clear();
            addAllArticles(articles);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent i){

        if(i == null || !i.hasExtra("tab"))
            return;

        currentTwitterFeed = i.getIntExtra("feed", 0);

        new FetchTweetsTask().execute("null");

    }

    @Override
    public void onResume(){
        super.onResume();
        twitterPage = 1;

        // Check if we're coming from the FeedsList activity
        /*Intent i = getIntent();
        if(i.hasExtra("tab")){

            Log.d("positive", "positive");

            articlesSelected = false;
            addFeedInput.setVisibility(View.GONE);
            addFeedInput.setText("");
            xbutton.setVisibility(View.GONE);
            feedsButton.setVisibility(View.VISIBLE);

            cropManager.setBackgroundColor(getResources().getColor(R.color.toggle_unselected));
            twitter.setBackgroundColor(getResources().getColor(R.color.toggle_selected));
            twitterPage = 1;

            currentTwitterFeed = i.getIntExtra("feed", 0);

            new FetchTweetsTask().execute("null");

        }*/

        if(db.getTimeSince(DataHelper.ROW_CROPMANAGER) > 3600000){
            if(isNetworkAvailable()){
                if(articlesSelected){
                    new FetchCropManagerArticlesTask().execute("null");
                }else{
                    new FetchTweetsTask().execute("null");
                }
            }else{
                notifyNoNetworkAvailable();

                if(articlesSelected){
                    articles = db.getCropManagerArticles();
                    Collections.sort(articles);
                    removeFooter();

                    articleAdapter.setNotifyOnChange(false);
                    articleAdapter.clear();
                    addAllArticles(articles);
                }else{
                    tweets = db.getTweets();

                    if(tweets.size() > 40){
                        List<Tweet> temp = tweets.subList(0, 40);
                        tweets = new ArrayList<Tweet>(temp);
                    }

                    addFooter();

                    twitterAdapter.setNotifyOnChange(false);
                    twitterAdapter.clear();
                    addAllTweets(tweets);
                }
            }
        }else{
            if(articlesSelected){
                setListAdapter(articleAdapter);
                articles = db.getCropManagerArticles();
                Collections.sort(articles);
                removeFooter();

                articleAdapter.setNotifyOnChange(false);
                articleAdapter.clear();
                addAllArticles(articles);
            }else{
                setListAdapter(twitterAdapter);
                tweets = db.getTweets();

                Log.d("tweetsSize", ((Integer)tweets.size()).toString());

                if(tweets.size() > 40){
                    List<Tweet> temp = tweets.subList(0, 40);
                    tweets = new ArrayList<Tweet>(temp);
                }

                addFooter();

                articleAdapter.setNotifyOnChange(false);
                articleAdapter.clear();
                addAllArticles(articles);
            }
        }

        if(!articlesSelected && (twitterAdapter == null || tweets.size() == 0)){
            new FetchTweetsTask().execute("null");
        }

        setNewsPickerAdapter();

    }
	
	private class FetchCropManagerArticlesTask extends AsyncTask<String, Void, ArrayList<CropManagerArticle>>{
		
		@Override
		protected void onPreExecute(){
            if(!list.isRefreshing()){
                setListAdapter(null);
                progBar.setVisibility(View.VISIBLE);
                emptyBox.setText("Loading content...");
            }
		}
		
		@Override
		protected ArrayList<CropManagerArticle> doInBackground(String... urls){
			articles = updateCropManagerList();
			return articles;
		}
		
		@Override
		protected void onPostExecute(ArrayList<CropManagerArticle> articles){
            db.saveCropManagerArticles(articles);
            db.updateTime(DataHelper.ROW_CROPMANAGER);
            removeFooter();

            Collections.sort(articles);

            if(getListAdapter() == null){
			    setListAdapter(new CropManagerAdapter(context, R.layout.listitem_articleitem, articles));
            }else{
                articleAdapter.setNotifyOnChange(false);
                articleAdapter.clear();
                addAllArticles(articles);
            }

            list.onRefreshComplete();
        }
		
	}
	
	private class FetchTweetsTask extends AsyncTask<String, Void, ArrayList<Tweet>>{
		
		@Override
		protected void onPreExecute(){
            if(!list.isRefreshing() && !gettingNextPage){
                setListAdapter(null);
                progBar.setVisibility(View.VISIBLE);
                emptyBox.setText("Loading content...");
            }
		}
		
		@Override
		protected ArrayList<Tweet> doInBackground(String... urls){
            // If we are getting the next page of tweets
            if(gettingNextPage){
                tweets.addAll(twitterLoadNextPage());
            }else{
                // If the list has been pulled to refresh
                if(list.isRefreshing()){
                    tweets = twitterPullToRefresh();
                }else{
                    tweets = twitterListInitialLoad();
                }
            }

			return tweets;
		}
		
		@Override
		protected void onPostExecute(ArrayList<Tweet> tweets){

            if(tweets.size() == 0){

                AlertDialog alertDialog = new AlertDialog.Builder(
                        News.this).create();

                // Setting Dialog Title
                //alertDialog.setTitle("");

                // Setting Dialog Message
                alertDialog.setMessage("This list is either empty or does not exist. Please check the help screen for tips on how to add Twitter lists.");

                // Showing Alert Message
                alertDialog.show();

            }

            Log.d("tweetsSize", ((Integer)db.getTweets().size()).toString());
            db.updateTime(DataHelper.ROW_TWITTER);
            addFooter();

            Collections.sort(tweets);
            db.saveTweets(tweets);

            if(getListAdapter() == null){
			    setListAdapter(new TwitterAdapter(context, R.layout.listitem_twitteritem, tweets));
            }else{
                twitterAdapter.setNotifyOnChange(false);
                twitterAdapter.clear();
                addAllTweets(tweets);
            }

            if(list.isRefreshing())
                list.onRefreshComplete();

            gettingNextPage = false;
            footerProgBar.setVisibility(View.GONE);
            footerText.setText("Load more tweets");
		}
		
	}
	
	// Return the minimum value in a set of three numbers
	private int min(int a, int b, int c) {

        if (b < a) {
          a = b;
        }
        if (c < a) {
          a = c;
        }
        return a;

	}
	
	// Return the minimum value in a set of two numbers
	private int min(int a, int b) {

        if(a < b)
          return a;
        else
          return b;

	}
	
	// Get an input stream from a given URL
	public InputStream getInputStream(URL url) {
		try{
			return url.openConnection().getInputStream();
		}catch(IOException e){
			return null;
		}
	}
	
	// Returns an updated list of CropManagerArticles read from the internet
	private ArrayList<CropManagerArticle> updateCropManagerList() {

        ArrayList<NewsFeed> feeds = db.getNewsFeeds();
        ArrayList<CropManagerArticle> articles = new ArrayList<CropManagerArticle>();

        NewsFeed feed = db.getNewsFeeds().get(newsPicker.getSelectedItemPosition());
        articles.addAll(Feeds.parseNewsFeed(feed));

        return articles;

        /*ArrayList<CropManagerArticle> articlesTemp = new ArrayList<CropManagerArticle>();
		ArrayList<String> titles = new ArrayList<String>();
		ArrayList<String> dates = new ArrayList<String>();
		ArrayList<String> links = new ArrayList<String>();
		
		try{
			
			URL url = new URL("http://ipcm.wisc.edu/feed/");
			
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(false);
			XmlPullParser xpp = factory.newPullParser();
			
			xpp.setInput(getInputStream(url), "UTF_8");
			
			boolean insideItem = false;
			int eventType = xpp.getEventType();
			
			while(eventType != XmlPullParser.END_DOCUMENT){
				if(eventType == XmlPullParser.START_TAG){
					if(xpp.getName().equalsIgnoreCase("item")){
						insideItem = true;
					} else if(xpp.getName().equalsIgnoreCase("title")){
						if(insideItem)
							titles.add(xpp.nextText());
					} else if(xpp.getName().equalsIgnoreCase("pubdate")){
						if(insideItem)
							dates.add(xpp.nextText());
					} else if(xpp.getName().equalsIgnoreCase("link")){
						if(insideItem)
							links.add(xpp.nextText());
					}
					
				} else if(eventType == XmlPullParser.END_TAG &&
									xpp.getName().equalsIgnoreCase("item")){
					insideItem = false;
				}
				
				eventType = xpp.next();
			}
			
			int min = min(titles.size(), dates.size(), links.size());
			for(int i = 0; i < min; i++)
			{
				articlesTemp.add(new CropManagerArticle(titles.get(i), parseDate(dates.get(i)), links.get(i)));
			}			
			
		}catch(MalformedURLException e){
			e.printStackTrace();
		}catch(XmlPullParserException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}
		
		return articlesTemp;*/
	}
	
	// Returns an updated list of Tweets read from the internet
	private ArrayList<Tweet> twitterPullToRefresh() {

        ArrayList<TwitterFeed> feeds = db.getTwitterFeeds();
        ArrayList<Tweet> tweets = new ArrayList<Tweet>();

        //for(TwitterFeed feed : feeds){
        //    tweets.addAll(Feeds.parseTwitterFeed(feed, new Paging(1, twitterPage*40)));
        //}

        try{
            tweets.addAll(Feeds.parseTwitterFeed(db.getTwitterFeeds().get(currentTwitterFeed), new Paging(1, twitterPage*40)));
        }catch(Exception e){
            Toast t = Toast.makeText(this, "An error occurred loading the feed", Toast.LENGTH_SHORT);
            t.show();
        }

        return tweets;

        /*

        Twitter twitter = new TwitterFactory().getInstance();
        ResponseList<Status> statuses;
        ArrayList<Tweet> twees = new ArrayList<Tweet>();

        try{
            if(twitterPage < 1) twitterPage = 1;

            statuses = twitter.getUserListStatuses("WisCropMan", "WisAg", new Paging(1, twitterPage*40));

            for(Status status : statuses){
                Tweet t = new Tweet(status.getUser().getScreenName(),
                                     status.getText(),
                                     ((Long)status.getId()).toString(),
                                     status.getUser().getBiggerProfileImageURL());
                twees.add(t);
            }

        }catch(TwitterException e){
            e.printStackTrace();
        }

        return twees;

        */

	}

    // Returns an updated list of Tweets read from the internet
    private ArrayList<Tweet> twitterListInitialLoad() {

        ArrayList<TwitterFeed> feeds = db.getTwitterFeeds();
        ArrayList<Tweet> tweets = new ArrayList<Tweet>();

        //for(TwitterFeed feed : feeds){
        //    tweets.addAll(Feeds.parseTwitterFeed(feed, new Paging(1, 40)));
        //}

        try{
            tweets.addAll(Feeds.parseTwitterFeed(db.getTwitterFeeds().get(currentTwitterFeed), new Paging(1, 40)));
        }catch(Exception e){
            Toast t = Toast.makeText(this, "An error occurred loading the feed", Toast.LENGTH_SHORT);
            t.show();
        }

        return tweets;

        /*

        Twitter twitter = new TwitterFactory().getInstance();
        ResponseList<Status> statuses;
        ArrayList<Tweet> twees = new ArrayList<Tweet>();

        try{
            if(twitterPage < 1) twitterPage = 1;

            statuses = twitter.getUserListStatuses("WisCropMan", "WisAg", new Paging(1, 40));

            for(Status status : statuses){
                Tweet t = new Tweet(status.getUser().getScreenName(),
                        status.getText(),
                        ((Long)status.getId()).toString(),
                        status.getUser().getBiggerProfileImageURL());
                twees.add(t);
            }

        }catch(TwitterException e){
            e.printStackTrace();
        }

        return twees;

        */

    }

    // Returns an updated list of Tweets read from the internet
    private ArrayList<Tweet> twitterLoadNextPage() {

        ArrayList<TwitterFeed> feeds = db.getTwitterFeeds();
        ArrayList<Tweet> tweets = new ArrayList<Tweet>();

        //for(TwitterFeed feed : feeds){
        //    tweets.addAll(Feeds.parseTwitterFeed(feed, new Paging(twitterPage, 40)));
        //}


        try{
            tweets.addAll(Feeds.parseTwitterFeed(db.getTwitterFeeds().get(currentTwitterFeed), new Paging(twitterPage, 40)));
        }catch(Exception e){
            Toast t = Toast.makeText(this, "An error occurred loading the feed", Toast.LENGTH_SHORT);
            t.show();
        }

        return tweets;

        /*

        Twitter twitter = new TwitterFactory().getInstance();
        ResponseList<Status> statuses;
        ArrayList<Tweet> twees = new ArrayList<Tweet>();

        try{
            if(twitterPage < 1) twitterPage = 1;

            statuses = twitter.getUserListStatuses("WisCropMan", "WisAg", new Paging(twitterPage, 40));

            for(Status status : statuses){
                Tweet t = new Tweet(status.getUser().getScreenName(),
                        status.getText(),
                        ((Long)status.getId()).toString(),
                        status.getUser().getBiggerProfileImageURL());
                twees.add(t);
            }

        }catch(TwitterException e){
            e.printStackTrace();
        }

        return twees;

        */

    }
	
	private String parseDate(String date) {
		return date.substring(0, date.indexOf("+") - 1);
	}
	
}
