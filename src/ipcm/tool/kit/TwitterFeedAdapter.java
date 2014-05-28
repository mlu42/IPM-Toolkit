package ipcm.tool.kit;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class TwitterFeedAdapter extends ArrayAdapter<TwitterFeed>{

    // Instance variables
    int resource;
    private ArrayList<TwitterFeed> feeds;

    public TwitterFeedAdapter(Context _context, int _resource, ArrayList<TwitterFeed> _feeds) {
        super(_context, _resource, _feeds);
        resource = _resource;
        feeds = _feeds;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
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

        TwitterFeed feed = feeds.get(position);
        if(feed != null){

            if(feed.listName != null){
                TextView listName = (TextView)view.findViewById(R.id.twitter_list_name);
                listName.setText(feed.listName);

                TextView user = (TextView)view.findViewById(R.id.twitter_user_name);
                user.setText("A list from @" + feed.user);
            }else{
                TextView user = (TextView)view.findViewById(R.id.twitter_user_name);
                user.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                user.setText("@" + feed.user);
                user.setTypeface(null, Typeface.BOLD);

                TextView listName = (TextView)view.findViewById(R.id.twitter_list_name);
                listName.setVisibility(View.GONE);
            }

        }

        ImageView editB = (ImageView)view.findViewById(R.id.edit_button);
        editB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TwitterFeed f = feeds.get(position);

                Intent i = new Intent(getContext(), EditFeed.class);

                i.putExtra("user", f.user);
                i.putExtra("list", f.listName);
                i.putExtra("id", f.id);
                i.putExtra("feedType", "twitterFeed");

                getContext().startActivity(i);
            }
        });

        return view;
    }

}