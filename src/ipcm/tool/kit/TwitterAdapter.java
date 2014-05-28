package ipcm.tool.kit;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/*
An adapter for the list of tweets
 */

public class TwitterAdapter extends ArrayAdapter<Tweet>{

	// Instance variables
	int resource;
	private ArrayList<Tweet> tweets;

    // Layout variables
    URL url;
    Bitmap bmp;
    ImageView thumb;

	public TwitterAdapter(Context _context, int _resource, ArrayList<Tweet> _articles) {
		super(_context, _resource, _articles);
		resource = _resource;
		tweets = _articles;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;		
		if(view == null)
		{
			LayoutInflater vi = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		    view = vi.inflate(resource, null);
		}

        // Odd/even background change
        if(position%2 == 1)
            view.setBackgroundColor(getContext().getResources().getColor(R.color.light_grey));
        else
            view.setBackgroundColor(getContext().getResources().getColor(R.color.white));

		Tweet tweet = tweets.get(position);		
		if(tweet != null){

            TextView screenName = (TextView)view.findViewById(R.id.screen_name);
            if(screenName!=null)
                screenName.setText(tweet.getScreenName());

			TextView handle = (TextView)view.findViewById(R.id.twitter_handle);
			if(handle!=null)
				handle.setText("@" + tweet.getUser());

			TextView text = (TextView)view.findViewById(R.id.tweetText);
			if(text!=null)
				text.setText(tweet.getText());

            thumb = (ImageView)view.findViewById(R.id.thumbnail);
            if(thumb!=null){
                url = tweet.getThumbnailURL();
                bmp = null;
                // Check cache
                if(News.twitterThumbnailCache.containsKey(url)){
                    bmp = News.twitterThumbnailCache.get(url);
                }else{
                    new GetThumbnailImageTask().execute();
                }
                if(bmp!=null)
                    thumb.setImageBitmap(bmp);
            }
		}		
		return view;
	}

    private class GetThumbnailImageTask extends AsyncTask<Void, Void, Bitmap> {

        URL a_url;
        ImageView a_iView;

        @Override
        protected void onPreExecute(){
            a_url = url;
            a_iView = thumb;
        }

        @Override
        protected Bitmap doInBackground(Void... urls){
            try{
                return BitmapFactory.decodeStream(a_url.openConnection().getInputStream());
            }catch(IOException e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Bitmap _bitmap){
            if(_bitmap != null){
                News.twitterThumbnailCache.put(a_url, _bitmap);
                a_iView.setImageBitmap(_bitmap);
            }
        }

    }

}