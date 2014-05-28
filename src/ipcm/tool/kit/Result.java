package ipcm.tool.kit;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class Result extends ListActivity {

    // Layout elements
    LinearLayout mainLayout;
    LinearLayout titleBar;
    LinearLayout titleBarLeft;
    LinearLayout titleBarRight;
    Button saveColl;
    TextView titleText;
    TextView imageCount;
    ListView list;
    ProgressBar progbar;
    TextView emptyText;
    TextView noPics;

    // Instance variables
    URL url;
    String _url;
    String collOrSub;
    String number;
    Context context = this;
    Intent intent;
    int count = 0;
    boolean loaded = false;
    SAXParser parser;
    DefaultHandler handler;
    InputStream in;
    DataHelper db;
    String query;
    String title;
    boolean searchSuggestions = false;

    // Adapters
    PictureAdapter adapter;

    // Helper lists
    private ArrayList<Picture> pictures;
	private ArrayList<Picture> tempPics = new ArrayList<Picture>();
	
	public void onCreate(Bundle savedInstanceState){
		
		Log.d("starting", "Result");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_result);
		
		pictures = new ArrayList<Picture>();
		adapter = new PictureAdapter(context, R.layout.listitem_pictureitem, pictures);
		mainLayout = (LinearLayout)findViewById(R.id.main_layout);
		list = getListView();
		noPics = (TextView)findViewById(R.id.no_pics);
		progbar = (ProgressBar)findViewById(R.id.progbar);
		emptyText = (TextView)findViewById(R.id.empty_text);
		titleBar = (LinearLayout)findViewById(R.id.titlebar);
		titleBarLeft = (LinearLayout)findViewById(R.id.titlebar_left);
		titleBarRight = (LinearLayout)findViewById(R.id.titlebar_right);
		titleText = (TextView)findViewById(R.id.title_text);
		imageCount = (TextView)findViewById(R.id.image_count);
		saveColl = (Button)findViewById(R.id.save_collection_button);
        noPics = (TextView)findViewById(R.id.no_pics);
		db = new DataHelper(this);
		query = "";
		title = "";
		intent = getIntent();
		
		setListAdapter(adapter);
		
		saveColl.setOnClickListener(new OnClickListener(){
			
			public void onClick(View v){
				
				Log.d("testing", title + " " + collOrSub + " " + query);

                if(!query.equals("72071")){
				    db.insertCollection(title, collOrSub, query);
				
				    Log.d("inserted", "inserted");
				
				    Toast toast = Toast.makeText(context, "Collection bookmarked", Toast.LENGTH_SHORT);
				    toast.setGravity(Gravity.TOP|Gravity.RIGHT, 0, 10);
				    toast.show();
                }else{
                    Toast toast = Toast.makeText(context, "Collection already saved", 1500);
                    toast.show();
                }
				
			}
			
		});
		
		///////// FORM TITLE BAR /////////////
		if(intent.hasExtra(SearchManager.EXTRA_DATA_KEY)){
			title = intent.getStringExtra(SearchManager.EXTRA_DATA_KEY);
			titleText.setText("Search results for " + title);
		}
		else{
			title = intent.getStringExtra("title");
			titleText.setText(title);
		}
		
		/////// FORM URL /////////
		if(Intent.ACTION_VIEW.equals(intent.getAction())){			

			Uri queryUri = intent.getData();
			query = queryUri.getLastPathSegment().toLowerCase().replace(' ', '_');
			
			if(Global.searchOption == 2){
				collOrSub = "collection";
				_url = "http://images.bugwood.org/slideshows/slideshowfeed.cfm?coll=" + query + "&out=rss";
			}else{
				
				collOrSub="subject";
				
				Log.d("query", query + " nnn");
				
				_url = "http://images.bugwood.org/slideshows/slideshowfeed.cfm?sub=" + query + "&out=rss";
			}
			
		}else if(Intent.ACTION_SEARCH.equals(intent.getAction())){

            Log.d("SEARCHING", "SEARCHING");
			
			query = intent.getStringExtra(SearchManager.QUERY);
			query = query.toLowerCase().replace(' ', '_');
			
			collOrSub = "collection";
			
			_url = "http://images.bugwood.org/slideshows/slideshowfeed.cfm?coll=" + query + "&out=rss";
			
		}else{
			collOrSub = intent.getStringExtra("collOrSub");
			number = intent.getStringExtra("number");
			
			query = number;
			
			_url = "http://images.bugwood.org/slideshows/slideshowfeed.cfm";
			
			if(collOrSub.equals("collection"))
				_url += "?coll=";
			else
				_url += "?sub=";
			
			_url += number + "&out=rss";
		}
		
		///////////////////////////////
		
		try{
			url = new URL(_url);
		}catch(MalformedURLException e){
			Log.d("exception", "bade URL");
		}

	}
	
	protected void onStart(){
		super.onStart();
		
		try{
			parser = SAXParserFactory.newInstance().newSAXParser();
			
			handler = new DefaultHandler(){
				
				boolean btitle = false;
				boolean bauthor = false;
				boolean bimageURL = false;
				boolean bthumbnailURL = false;
				
				String title = null;
				String author = null;
				String imageURL = null;
				String thumbnailURL = null;
				
				public void startElement(String url, String localName, String qName, Attributes attributes) throws SAXException{
					
					if(qName.equalsIgnoreCase("title"))
						btitle = true;
					
					if(qName.equalsIgnoreCase("author"))
						bauthor = true;
					
					if(qName.equalsIgnoreCase("media:content")){
						imageURL = attributes.getValue("url");
						bimageURL = true;
					}
					
					if(qName.equalsIgnoreCase("media:thumbnail")){
						thumbnailURL = attributes.getValue("url");
						bthumbnailURL = true;
					}
					
				}
				
				public void endElement(String uri, String localName, String qName) throws SAXException{
					
				}
				
				public void characters(char ch[], int start, int length) throws SAXException{
					
					if(btitle){
						title = new String(ch, start, length);
						btitle = false;
					}
					
					if(bauthor){
						author = new String(ch, start, length);
						bauthor = false;
					}
					
					if(bimageURL){
						bimageURL = false;
					}
					
					if(bthumbnailURL){
						bthumbnailURL = false;
					}
					
					if(title != null && author != null && imageURL != null && thumbnailURL != null){
						Picture newPic = new Picture(title, author, imageURL, thumbnailURL, count);
						tempPics.add(newPic);
						newPic.loadThumbnail(adapter);
						count++;
						
						title = null; author = null; imageURL = null; thumbnailURL = null;
					}
					
				}
				
			};

            if(isNetworkAvailable()){

                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet(url.toString());
                HttpResponse response = client.execute(request);
                in = response.getEntity().getContent();

                new ParseTask().execute(parser);
            }else{
                notifyNoNetworkAvailable();
            }
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void notifyNoNetworkAvailable(){
        progbar.setVisibility(View.GONE);
        emptyText.setText("No internet connection");
        Toast toast = Toast.makeText(this, "Internet connection unavailable", 1500);
        toast.show();
    }
	
	private class ParseTask extends AsyncTask<SAXParser, Void, Void>{
		
		protected void onPreExecute(){
			progbar.setVisibility(View.VISIBLE);
            emptyText.setText("Loading content...");
		}
		
		protected Void doInBackground(SAXParser... params){
			try{
				parser.parse(in, handler);
			}catch(Exception e){
				e.printStackTrace();
			}
			
			return null;
			
		}
		
		protected void onPostExecute(Void v){
			
			if(pictures.isEmpty()){
				for(Picture pic : tempPics){
					Picture newPic = new Picture(pic);
					pic.setPair(newPic);
					pictures.add(pic);
				}
			}

            adapter.notifyDataSetChanged();
            setListAdapter(adapter);
			
			if(pictures.size() == 0){
                // Get search suggestions for query text
                new SuggestionsTask().execute();
			}else{
				
				imageCount.setText("(" + ((Integer)pictures.size()).toString() + " images)");
                titleText.setText("Search results for " + title);
				
				ViewGroup.LayoutParams params = titleBarLeft.getLayoutParams();
				params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
				titleBarLeft.setLayoutParams(params);
				params = titleBarRight.getLayoutParams();
				params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
				titleBarRight.setLayoutParams(params);

                titleBar.setVisibility(View.VISIBLE);
                titleBarLeft.setVisibility(View.VISIBLE);
                titleBarRight.setVisibility(View.VISIBLE);
                imageCount.setVisibility(View.VISIBLE);
				
				mainLayout.removeView(noPics);

                if(searchSuggestions)
                    searchSuggestions = false;
			}
		}

	}
	
	// The action performed when an item in the list is clicked on
	protected void onListItemClick(ListView l, View v, int position, long id){

        if(!searchSuggestions){
            Intent intent = new Intent(this, ImageDetailActivity.class);

            intent.putExtra("position", position);
            intent.putParcelableArrayListExtra("pictures", pictures);

            startActivity(intent);
        }else{
            // Create the layout to be loading
            list.setAdapter(null);
            titleBar.setVisibility(View.GONE);
            title = ((SearchSuggestion)getListAdapter().getItem(position)).value;

            // Get the pictures
            try{
                if(isNetworkAvailable()){

                    // Form URL
                    String tempURL = "http://images.bugwood.org/slideshows/slideshowfeed.cfm?sub=";
                    tempURL += ((SearchSuggestion)getListAdapter().getItem(position)).id;
                    tempURL += "&out=rss";

                    url = new URL(tempURL);

                    HttpClient client = new DefaultHttpClient();
                    HttpGet request = new HttpGet(url.toString());
                    HttpResponse response = client.execute(request);
                    in = response.getEntity().getContent();

                    new ParseTask().execute(parser);
                }else{
                    notifyNoNetworkAvailable();
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
		
	}

    private class SuggestionsTask extends AsyncTask<Void, Void, ArrayList<SearchSuggestion>>{

        protected void onPreExecute(){
        }

        protected ArrayList<SearchSuggestion> doInBackground(Void... v){
            ArrayList<SearchSuggestion> suggestions = SearchSuggestionsProvider.simpleSearchSuggestQuery(query);
            return suggestions;
        }

        protected void onPostExecute(ArrayList<SearchSuggestion> suggestions){
            if(suggestions.size() == 0){
                mainLayout.removeView(list.getEmptyView());
                titleText.setHeight(0);
                imageCount.setHeight(0);
            }else{
                // LEFT OFF HERE. Need to take the list and put it into an adapter to display and then implement
                // the click action.
                noPics.setVisibility(View.GONE);
                titleBarRight.setVisibility(View.GONE);
                imageCount.setVisibility(View.GONE);
                titleText.setText("Search suggestions for \"" + query.replace(' ',' ') + "\"");
                setListAdapter(new SearchSuggestionAdapter(context, R.layout.listitem_searchsuggestion, suggestions));
                searchSuggestions = true;
            }

        }

    }


}
