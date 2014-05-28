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
An adapter for the list of YouTubeVideos
 */

public class YoutubeChannelAdapter extends ArrayAdapter<YoutubeChannel>{
    // Instance variables
    int resource;
    private ArrayList<YoutubeChannel> channels;

    // Layout variables
    URL url;
    Bitmap bmp;
    ImageView thumbnail;

    public YoutubeChannelAdapter(Context _context, int _resource, ArrayList<YoutubeChannel> _channels) {
        super(_context, _resource, _channels);
        resource = _resource;
        channels = _channels;
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

        YoutubeChannel channel = channels.get(position);
        if(channel != null){
            TextView title = (TextView)view.findViewById(R.id.channelTitle);
            title.setText(channel.url);
        }
        return view;
    }

}