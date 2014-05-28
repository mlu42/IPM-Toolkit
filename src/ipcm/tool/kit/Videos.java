package ipcm.tool.kit;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.*;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView.OnItemSelectedListener;

/*
The Video activity
 */

public class Videos extends ListActivity{

	// Instance variables
	DataHelper db;                          // The database helper object
	final Context context = this;           // For use with methods that need a context
	static ArrayList<Playlist> playlists;   // The playlists
	static ArrayList<YoutubeVideo> videos;  // The videos currently loaded
	ArrayAdapter<String> adapter;           // The adapter currently being used
	static PullToRefreshListView list;      // A library I use to implement pull-to-refresh
    ProgressBar progBar;                    // The loading progress bar
    TextView emptyBox;                      // < figure out what this is >
	public static Spinner spinner;          // The selector for the playlists

    // A cache for video thumbnails in the list
    public static HashMap<URL, Bitmap> thumbnailCache;
	
	// The action performed when an item in the list is clicked on
	protected void onListItemClick(ListView l, View v, int position, long id){

        if(videos.get(position).isSectionHeader)
            return;

		Log.d("link", videos.get(position).getUrl());
		Uri uri = Uri.parse(videos.get(position).getUrl());
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		startActivity(intent);
	}

    // Determines if the device currently has a data connection
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    // Notifies the user that no network is available.
    private void notifyNoNetworkAvailable(){
        progBar.setVisibility(View.GONE);
        emptyBox.setText("No internet connection");
        Toast toast = Toast.makeText(this, "Internet connection unavailable", 1500);
        toast.show();
    }

    // Reloads the list of videos
    public void reloadList(View v){
        if(isNetworkAvailable()){
            setupSpinner();
            new Feeds(context).getYoutubeVideos((String)spinner.getAdapter().getItem(spinner.getSelectedItemPosition()));
        }else{
            notifyNoNetworkAvailable();
        }
    }
	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_videos);
		
		// Initializing instance variables
		final Resources res = getResources();
		db = new DataHelper(this);
		playlists = new ArrayList<Playlist>();
		videos = new ArrayList<YoutubeVideo>();
		list = (PullToRefreshListView) getListView();
        progBar = (ProgressBar) findViewById(R.id.progbar);
        emptyBox = (TextView) findViewById(R.id.empty_text);
		spinner = (Spinner) findViewById(R.id.playlist_spinner);
        thumbnailCache = new HashMap<URL, Bitmap>();
		
		list.setCacheColorHint(Color.TRANSPARENT); // Prevent weird animations when a list item is clicked

        // Define what happens when you select an item in the list
		spinner.setOnItemSelectedListener( new OnItemSelectedListener(){			

			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {

                    if(isNetworkAvailable()){
					    new Feeds(context).getYoutubeVideos((String)spinner.getAdapter().getItem(spinner.getSelectedItemPosition()));
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

    @Override
    public void onStart(){
        super.onStart();

        // If it has been more than an hour since the list has been refreshed, reload the list from the internet
        if(db.getTimeSince(DataHelper.ROW_ALLPLAYLISTS) > 3600000)
        {
            if(isNetworkAvailable()){
                setupSpinner();
            }else{
                notifyNoNetworkAvailable();
                setupSpinner();
            }
        }
        // else, load the cached version of the list in memory
        else
        {
            // Set the spinner values to be the channels we have added
            setupSpinner();
        }
    }

    @Override
    public void onResume(){
        super.onResume();

        // Check to see if it has been an hour since last updating
        if(db.getTimeSince(DataHelper.ROW_CROPMANAGER) > 3600000){
            if(isNetworkAvailable()){
                setupSpinner();
                new Feeds(context).getYoutubeVideos((String)spinner.getAdapter().getItem(spinner.getSelectedItemPosition()));
            }else{
                notifyNoNetworkAvailable();
            }
        }

    }

    public void setupSpinner(){
        ArrayList<String> channelNames = new ArrayList<String>();
        ArrayList<YoutubeChannel> chs = db.getYoutubeChannels();
        for(YoutubeChannel chann : chs){
            channelNames.add(chann.url);
        }
        adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, channelNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(0);
    }

    // Called when the add channel button is clicked
    public void addChannelButton(View v){
        Intent i = new Intent(this, AddYoutubeChannel.class);
        startActivity(i);

    }

    // An asynchronous task that gets videos and updates the list accordingly
	private class FetchVideosTask extends AsyncTask<String, Void, ArrayList<YoutubeVideo>>{
		
		@Override
		protected void onPreExecute(){
            if(!list.isRefreshing()){
                setListAdapter(null);
                progBar.setVisibility(View.VISIBLE);
                emptyBox.setText("Loading content...");
            }
		}
		
		@Override
		protected ArrayList<YoutubeVideo> doInBackground(String... urls){
			
			// Get all the playlists and videos
			playlists = getPlaylists();
			videos = getAllVideos();
			
			return videos;
		}
		
		@Override
		protected void onPostExecute(ArrayList<YoutubeVideo> videos){

            ArrayList<String> playlistNames = getPlaylistNames();
            adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, playlistNames);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
            spinner.setSelection(1);

            db.saveAllYoutubeVids(videos);
            db.saveAllPlaylists(playlists);
            db.updateTime(DataHelper.ROW_ALLPLAYLISTS);

			setListAdapter(new YoutubeAdapter(context, R.layout.listitem_videoitem, videos));
            list.onRefreshComplete();

		}
		
	}

    // Get all videos associated with a certain playlist
	public ArrayList<YoutubeVideo> getAllVideosFromPlaylist(String playlist) {
		ArrayList<YoutubeVideo> vids = new ArrayList<YoutubeVideo>();
		
		for(YoutubeVideo video: videos)
		{
			if(video.getPlaylist().equals(playlist))
				vids.add(video);
		}
		
		return vids;
	}

    // Get an input stream from the given URL
	public InputStream getInputStream(URL url) {
		try{
			return url.openConnection().getInputStream();
		}catch(IOException e){
			return null;
		}
	}

    //  Get the playlists for our channel
	public ArrayList<Playlist> getPlaylists() {
		ArrayList<Playlist> playlistsTemp = new ArrayList<Playlist>();
		ArrayList<String> names = new ArrayList<String>();
		ArrayList<String> urls = new ArrayList<String>();

		
		names.add("All playlists");
		urls.add("");
		names.add("Featured playlists");
		urls.add("");
		
		try{
			
			URL url = new URL("https://gdata.youtube.com/feeds/api/users/uwipm/playlists?v=2");
			
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(false);
			XmlPullParser xpp = factory.newPullParser();
			
			xpp.setInput(getInputStream(url), "UTF_8");
			
			boolean insideItem = false;
			int eventType = xpp.getEventType();
			
			while(eventType != XmlPullParser.END_DOCUMENT){
				if(eventType == XmlPullParser.START_TAG){
					if(xpp.getName().equalsIgnoreCase("entry")){
						insideItem = true;
					} else if(xpp.getName().equalsIgnoreCase("title")){
						if(insideItem)
							names.add(xpp.nextText());
					} else if(xpp.getName().equalsIgnoreCase("content")){
						if(insideItem)
							urls.add(xpp.getAttributeValue(1));
					}
					
				} else if(eventType == XmlPullParser.END_TAG &&
									xpp.getName().equalsIgnoreCase("entry")){
					insideItem = false;
				}
				
				eventType = xpp.next();
			}
			
			int min = min(names.size(), urls.size());
			for(int i = 0; i < min; i++)
			{
				playlistsTemp.add(new Playlist(names.get(i), urls.get(i)));
			}
			
			
		}catch(MalformedURLException e){
			e.printStackTrace();
		}catch(XmlPullParserException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}

		return playlistsTemp;

	}

    // Get the minimum of two integers
	private int min(int a, int b) {
	  if (b < a) {
		  Log.d("min", "" + b);
          return b;
      }
	  else {
		  Log.d("min", "" + a);
		  return a;
	  }
	  
	}

    // Get the minimum of three integers
	private int min(int a, int b, int c) {
	  if (b < a) {
          a = b;
      }
      if (c < a) {
          a = c;
      }
      return a;
	}

    // Get the names from the current playlist
	public ArrayList<String> getPlaylistNames()	{
		ArrayList<String> names = new ArrayList<String>();
		
		for(Playlist list: playlists)
		{
			names.add(list.getTitle());
		}
		
		return names;
	}

    // Get every video in each playlist of the current list of playlist
	public ArrayList<YoutubeVideo> getAllVideos() {
		db.openForWrite();
		
		ArrayList<YoutubeVideo> videosTemp = new ArrayList<YoutubeVideo>();

        for(Playlist playlist: playlists)
        {
            ArrayList<String> titles = new ArrayList<String>();
            ArrayList<String> playlists = new ArrayList<String>();
            ArrayList<String> urls = new ArrayList<String>();
            ArrayList<String> thumbnailUrls = new ArrayList<String>();

            try{
                Log.d("playlist", playlist.getUrl());
                URL url = new URL(playlist.getUrl());

                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);
                XmlPullParser xpp = factory.newPullParser();

                xpp.setInput(getInputStream(url), "UTF_8");

                boolean insideItem = false;
                boolean foundThumbnail = false;
                int eventType = xpp.getEventType();

                while(eventType != XmlPullParser.END_DOCUMENT){
                    if(eventType == XmlPullParser.START_TAG){
                        if(xpp.getName().equalsIgnoreCase("entry")){
                            insideItem = true;
                        } else if(xpp.getName().equalsIgnoreCase("title")){
                            if(insideItem)
                            {
                                titles.add(xpp.nextText());
                                playlists.add(playlist.getTitle());
                            }
                        } else if(xpp.getName().equalsIgnoreCase("link")){
                            if(insideItem)
                                if(xpp.getAttributeValue(null, "rel").equals("alternate"))
                                    urls.add(xpp.getAttributeValue(null, "href"));
                        } else if(xpp.getName().equalsIgnoreCase("media:thumbnail"))
                            if(insideItem)
                                if(xpp.getAttributeValue(null, "height").equals("90"))
                                    if(!foundThumbnail){
                                        foundThumbnail = true;
                                        thumbnailUrls.add(xpp.getAttributeValue(null, "url"));
                                    }

                    } else if(eventType == XmlPullParser.END_TAG &&
                                        xpp.getName().equalsIgnoreCase("entry")){
                        insideItem = false;
                        foundThumbnail = false;
                    }

                    eventType = xpp.next();
                }

                Log.d("titles", ((Integer)titles.size()).toString());
                Log.d("playlists", ((Integer)playlists.size()).toString());
                Log.d("urls", ((Integer)urls.size()).toString());
                Log.d("thumbnailUrls", ((Integer)thumbnailUrls.size()).toString());

                int min = min(titles.size(), playlists.size(), thumbnailUrls.size());
                for(int i = 0; i < min; i++)
                {
                    videosTemp.add(new YoutubeVideo(titles.get(i), playlists.get(i), urls.get(i), thumbnailUrls.get(i)));
                }


            }catch(MalformedURLException e){
                e.printStackTrace();
            }catch(XmlPullParserException e){
                e.printStackTrace();
            }catch(IOException e){
                e.printStackTrace();
            }
        }

		return videosTemp;
	}

}
