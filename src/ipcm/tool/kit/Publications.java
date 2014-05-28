package ipcm.tool.kit;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
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
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

public class Publications extends ListActivity{
	
	private ArrayList<Publication> publications;
	DataHelper db;
	PullToRefreshListView list;
	Context context = this;
	SAXParser parser;
	DefaultHandler handler;
	InputStream in;
    ProgressBar progBar;
    TextView emptyBox;
	
	// The action performed when an item in the list is clicked on
	protected void onListItemClick(ListView l, View v, int position, long id){
		Log.d("link", publications.get(position).getLink());
		// Parse the descrip tag so that it immediately downloads the pdf
		Uri uri = Uri.parse(publications.get(position).getLink());
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		startActivity(intent);
	}

    /*
    Checks to see if the device is connected to the internet
    @return true if the device is connected to the internet, false otherwise
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /*
    Notifies the user via toast that the device is not connected to the internet
     */
    private void notifyNoNetworkAvailable(){
        progBar.setVisibility(View.GONE);
        emptyBox.setText("No internet connection");
        Toast toast = Toast.makeText(this, "Internet connection unavailable", 1500);
        toast.show();
    }

    public void reloadList(View v){
        if(isNetworkAvailable()){
            new FetchPublicationsTask().execute();
        }else{
            notifyNoNetworkAvailable();
            publications = db.getAllPublications();
            setListAdapter( new PubAdapter(this, R.layout.listitem_pubitem, publications));
        }
    }

	public void onCreate(Bundle savedInstanceState)	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_publications);
		
		publications = new ArrayList<Publication>();
		db = new DataHelper(this);
		list = (PullToRefreshListView) getListView();
        progBar = (ProgressBar) findViewById(R.id.progbar);
        emptyBox = (TextView) findViewById(R.id.empty_text);

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

        if(db.getTimeSince(DataHelper.ROW_PUBLICATIONS) > 3600000)
        {
            if(isNetworkAvailable()){
                new FetchPublicationsTask().execute();
            }else{
                notifyNoNetworkAvailable();
                publications = db.getAllPublications();
                setListAdapter( new PubAdapter(this, R.layout.listitem_pubitem, publications));
            }
        }
        // else, load the cached version of the list in memory
        else
        {
            publications = db.getAllPublications();
            setListAdapter( new PubAdapter(this, R.layout.listitem_pubitem, publications));
        }
    }

    @Override
     public void onResume(){
        super.onResume();

        if(db.getTimeSince(DataHelper.ROW_PUBLICATIONS) > 3600000)
        {
            if(isNetworkAvailable()){
                new FetchPublicationsTask().execute();
            }else{
                notifyNoNetworkAvailable();
                publications = db.getAllPublications();
                setListAdapter( new PubAdapter(this, R.layout.listitem_pubitem, publications));
            }
        }
        // else, load the cached version of the list in memory
        else
        {
            publications = db.getAllPublications();
            setListAdapter( new PubAdapter(this, R.layout.listitem_pubitem, publications));
        }
    }
	
	private class ParseTask extends AsyncTask<SAXParser, Void, Void>{
		
		protected void onPreExecute(){
			progBar.setVisibility(View.VISIBLE);
            emptyBox.setText("Loading content...");
		}
		
		protected Void doInBackground(SAXParser... params){
			try{
				if(db.getTimeSince(DataHelper.ROW_PUBLICATIONS) > 3600000){
					parser.parse(in, handler);
					Log.d("parsed", "parsed");

					db.updateTime(DataHelper.ROW_PUBLICATIONS);
				}else{
					publications = db.getAllPublications();
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			
			return null;
			
		}
		
		protected void onPostExecute(Void v){			
			Log.d("pubs", ((Integer) publications.size()).toString());
			setListAdapter(new PubAdapter(context, R.layout.listitem_pubitem, publications));
			db.savePublications(publications);
		}
	}
	
	private class FetchPublicationsTask extends AsyncTask<String, Void, ArrayList<Publication>>{
		
		@Override
		protected void onPreExecute(){
            if(!list.isRefreshing()){
                setListAdapter(null);
                progBar.setVisibility(View.VISIBLE);
                emptyBox.setText("Loading content...");
            }
		}
		
		@Override
		protected ArrayList<Publication> doInBackground(String... urls){
			publications = updatePublicationList();
			return publications;
		}
		
		@Override
		protected void onPostExecute(ArrayList<Publication> publications){
            db.savePublications(publications);
            db.updateTime(DataHelper.ROW_PUBLICATIONS);
			setListAdapter(new PubAdapter(context, R.layout.listitem_pubitem, publications));
            list.onRefreshComplete();
		}
		
	}
	
	// Get an input stream from a given URL
	public InputStream getInputStream(URL url) {
		try{
			return url.openConnection().getInputStream();
		}catch(IOException e){
			return null;
		}
	}
	
	// Return the minimum value in a set of two numbers
	private int min(int a, int b) {
	  if(a < b)
		  return a;
	  else
		  return b;
	}
	
	// Returns an updated list of CropManagerArticles read from the internet
	private ArrayList<Publication> updatePublicationList() {
		ArrayList<Publication> pubs = new ArrayList<Publication>();
		ArrayList<String> titles = new ArrayList<String>();
		ArrayList<String> links = new ArrayList<String>();
			
        try{

            URL url = new URL("http://ipcm.wisc.edu/blog/category/toolkit/feed/");

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
                    } else if(xpp.getName().equalsIgnoreCase("description")){
                        if(insideItem){
                            String link = xpp.nextText();
                            links.add(link);
                        }
                    }

                } else if(eventType == XmlPullParser.END_TAG &&
                                    xpp.getName().equalsIgnoreCase("item")){
                    insideItem = false;
                }

                eventType = xpp.next();
            }

            int min = min(titles.size(), links.size());
            for(int i = 0; i < min; i++)
            {
                pubs.add(new Publication(titles.get(i), links.get(i)));
            }

        }catch(MalformedURLException e){
            e.printStackTrace();
        }catch(XmlPullParserException e){
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }
		
		return pubs;
	}
	
}