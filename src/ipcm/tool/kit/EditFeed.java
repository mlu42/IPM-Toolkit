package ipcm.tool.kit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class EditFeed extends Activity {

    long id = -1;
    NewsFeed feed = new NewsFeed();
    TwitterFeed twitterFeed = new TwitterFeed();
    private TextView title;
    private TextView descripLabel;
    private TextView urlLabel;
    private EditText descripInput;
    private TextView urlValue;
    private Button updateButton;

    DataHelper db;

    boolean isNewsFeed = true;

    // Run when the activity is initialized
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_editfeed);

        title = (TextView)findViewById(R.id.editTitleBar);
        descripLabel = (TextView)findViewById(R.id.feed_title_label);
        urlLabel = (TextView)findViewById(R.id.feed_url_label);
        descripInput = (EditText)findViewById(R.id.feed_title_input);
        urlValue = (TextView)findViewById(R.id.feed_url);
        updateButton = (Button)findViewById(R.id.feedsEditButton);

        db = new DataHelper(this);

        Intent i = getIntent();

        if(i.hasExtra("feedType")){
            if(i.getStringExtra("feedType").equals("twitterFeed")){
                isNewsFeed = false;
            }else{
                isNewsFeed = true;
            }
        }

        if(isNewsFeed){

            if(i.hasExtra("description")){
                feed.descrip = i.getStringExtra("description");
            }

            if(i.hasExtra("url")){
                feed.url = i.getStringExtra("url");
            }

            if(i.hasExtra("id")){
                id = i.getLongExtra("id", 0);
            }

            if(i.hasExtra("feedType")){
                feed.feedType = i.getStringExtra("feedType");
            }

            if(feed != null){

                title.setText("Edit news feed");
                descripLabel.setText("Feed description");
                descripInput.setText(feed.descrip);
                urlLabel.setText("Feed URL");
                urlValue.setText(feed.url);

            }

        }else{

            if(i.hasExtra("user")){
                twitterFeed.user = i.getStringExtra("user");
            }

            if(i.hasExtra("list")){
                twitterFeed.listName = i.getStringExtra("list");
            }

            if(i.hasExtra("id")){
                twitterFeed.id = i.getLongExtra("id", 0);
                id = twitterFeed.id;
            }

            if(twitterFeed != null){

                title.setText("Edit Twitter feed");
                descripLabel.setText("Twitter user");
                descripInput.setText("@" + twitterFeed.user);

                if(twitterFeed.listName != null && !twitterFeed.listName.equals("")){
                    urlLabel.setText("User list");
                    urlValue.setText(twitterFeed.listName);
                }else{
                    urlLabel.setVisibility(View.GONE);
                    urlValue.setVisibility(View.GONE);
                }

            }

        }

    }

    public void update(View v){

        if(id == -1)
            return;

        if(isNewsFeed){

            if(feed != null){
                feed.descrip = descripInput.getText().toString();
                db.updateNewsFeed(id, feed);
            }

        }else{

            if(twitterFeed != null){
                twitterFeed.user = descripInput.getText().toString().substring(1);
                db.updateTwitterFeed(id, twitterFeed);
            }

        }

        finish();

    }

    public void delete(View v){

        if(id == -1)
            return;

        if(isNewsFeed){

            if( feed.url.equals("http://ipcm.wisc.edu/feed/") ){
                Toast toast = Toast.makeText(this, "Cannot remove this channel", Toast.LENGTH_SHORT);
                toast.show();
                return;
            }

            db.removeNewsFeed(id);
        }else{

            if( twitterFeed.user.equals("WisCropMan") && twitterFeed.listName.equals("WisAg") ){
                Toast toast = Toast.makeText(this, "Cannot remove this channel", Toast.LENGTH_SHORT);
                toast.show();
                return;
            }

            db.removeTwitterFeed(id);
        }

        finish();

    }

}
