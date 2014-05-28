package ipcm.tool.kit;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NewsFeedsAdapter extends ArrayAdapter<NewsFeed>{

    // Instance variables
    int resource;
    private ArrayList<NewsFeed> feeds;

    public NewsFeedsAdapter(Context _context, int _resource, ArrayList<NewsFeed> _feeds) {
        super(_context, _resource, _feeds);
        resource = _resource;
        feeds = _feeds;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View view = convertView;
        if(view == null)
        {
            LayoutInflater vi = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = vi.inflate(resource, null);
        }

        // Alternate background colors
        if(position%2 == 1)
            view.setBackgroundColor(getContext().getResources().getColor(R.color.light_grey));
        else
            view.setBackgroundColor(getContext().getResources().getColor(R.color.white));

        NewsFeed feed = feeds.get(position);
        if(feed != null){
            TextView descrip = (TextView)view.findViewById(R.id.newsfeed_descrip);
            descrip.setText(feed.descrip);

            if(descrip.getText().equals(""))
                descrip.setVisibility(View.GONE);
            else
                descrip.setVisibility(View.VISIBLE);

            TextView title = (TextView)view.findViewById(R.id.newsfeed_title);
            title.setText(feed.url);
        }

        return view;
    }

}