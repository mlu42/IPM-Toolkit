package ipcm.tool.kit;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/*
An adapter for the list of YouTubeVideos
 */

public class YoutubeAdapter extends ArrayAdapter<YoutubeVideo>{	
	// Instance variables
	int resource;
	private ArrayList<YoutubeVideo> videos;

    // Layout variables
    URL url;
    Bitmap bmp;
    ImageView thumbnail;

	public YoutubeAdapter(Context _context, int _resource, ArrayList<YoutubeVideo> _videos) {
		super(_context, _resource, _videos);
		resource = _resource;
		videos = _videos;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;		
		if(view == null)
		{
			LayoutInflater vi = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		    view = vi.inflate(resource, null);
		}

        if(position%2 == 1)
            view.setBackgroundColor(getContext().getResources().getColor(R.color.light_grey));
        else
            view.setBackgroundColor(getContext().getResources().getColor(R.color.white));

		YoutubeVideo video = videos.get(position);

        if(video.isSectionHeader){

            TextView top = (TextView)view.findViewById(R.id.top);
            if(top!=null){
                top.setText(video.sectionTitle);
                top.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                top.setTextColor(+getContext().getResources().getColor(R.color.white));
            }

            thumbnail = (ImageView)view.findViewById(R.id.thumbnail);
            if(thumbnail != null)
                thumbnail.setVisibility(View.GONE);

            view.setBackgroundColor(getContext().getResources().getColor(R.color.black));

            return view;
        }

		if(video != null){
			thumbnail = (ImageView)view.findViewById(R.id.thumbnail);
			TextView top = (TextView)view.findViewById(R.id.top);
			if(top!=null){
				top.setText(video.getName());
                top.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                top.setTextColor(+getContext().getResources().getColor(R.color.black));
            }

			if(thumbnail!=null)
			{
                thumbnail.setVisibility(View.VISIBLE);
				try{
					url = new URL(video.getThumbnailLink());
                    bmp = null;
                    // Check cache
                    if(Videos.thumbnailCache.containsKey(url)){
                        bmp = Videos.thumbnailCache.get(url);
                    }else{
                        new GetThumbnailImageTask().execute();
                    }
                    if(bmp!=null)
					    thumbnail.setImageBitmap(bmp);
				}catch(MalformedURLException ex){
					ex.printStackTrace();
				}catch(Exception e){
                    e.printStackTrace();
                }

                // SETTING ONLY THE FIRST LIST ITEM'S BITMAP DUE TO SOME SORT OF ISSUE WITH THREAD POOLS AND QUEUING
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
            a_iView = thumbnail;
        }

        @Override
        protected Bitmap doInBackground(Void... urls){
            try{
                if(a_url != null)
                    return BitmapFactory.decodeStream(a_url.openConnection().getInputStream());
            }catch(IOException e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Bitmap _bitmap){
            if(_bitmap != null){
                Videos.thumbnailCache.put(a_url, _bitmap);
                a_iView.setImageBitmap(_bitmap);
            }
        }

    }

}