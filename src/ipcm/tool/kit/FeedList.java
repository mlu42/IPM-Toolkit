package ipcm.tool.kit;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class FeedList extends ListActivity {

    // LEFT OFF HERE
    // The list of newsfeeds that the user can edit.

    TextView title;

    DataHelper db = new DataHelper(this);
    ArrayList<NewsFeed> newsFeeds;
    ArrayList<TwitterFeed> twitterFeeds;
    boolean news = true;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_feedslist);

        title = (TextView)findViewById(R.id.feedslist_title);

        String type = "newsfeeds";

        if(getIntent().hasExtra("type"))
            type = getIntent().getStringExtra("type");

        if(type.equals("newsfeeds")){
            news = true;
            newsFeeds = db.getNewsFeeds();
            setListAdapter(new NewsFeedsAdapter(this, R.layout.listitem_newsfeed, newsFeeds));
            title.setText("News Feeds");
        }else{
            news = false;
            twitterFeeds = db.getTwitterFeeds();
            setListAdapter(new TwitterFeedAdapter(this, R.layout.listitem_twitterfeed, twitterFeeds));
            title.setText("Twitter Feeds");
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        if(news){
            news = true;
            newsFeeds = db.getNewsFeeds();
            setListAdapter(new NewsFeedsAdapter(this, R.layout.listitem_newsfeed, newsFeeds));
        }else{
            news = false;
            twitterFeeds = db.getTwitterFeeds();
            setListAdapter(new TwitterFeedAdapter(this, R.layout.listitem_twitterfeed, twitterFeeds));
        }

    }

    protected void onListItemClick(ListView l, View v, int position, long id){
        super.onListItemClick(l, v, position, id);

        if(news){

            NewsFeed f = newsFeeds.get(position);

            Intent i = new Intent(this, EditFeed.class);

            i.putExtra("description", f.descrip);
            i.putExtra("url", f.url);
            i.putExtra("feedType", f.feedType);
            i.putExtra("id", f.id);
            i.putExtra("position", position);

            startActivity(i);

        }else{

            Intent i = new Intent(this, Main.class);
            i.putExtra("tab", "twitter");
            i.putExtra("feed", position);
            setResult(RESULT_OK, i);
            finish();

            /*TwitterFeed f = twitterFeeds.get(position);

            Intent i = new Intent(this, EditFeed.class);

            i.putExtra("user", f.user);
            i.putExtra("list", f.listName);
            i.putExtra("id", f.id);
            i.putExtra("feedType", "twitterFeed");

            startActivity(i);*/

        }

    }

}
