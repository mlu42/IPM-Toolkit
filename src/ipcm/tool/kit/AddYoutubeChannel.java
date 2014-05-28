package ipcm.tool.kit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class AddYoutubeChannel extends Activity {

    EditText channelInput;
    DataHelper db = new DataHelper(this);
    public static ListView list;
    ArrayList<YoutubeChannel> channels;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_addyoutubechannel);

        channelInput = (EditText)findViewById(R.id.channelIdInput);
        db = new DataHelper(this);
        list = (ListView)findViewById(R.id.channelList);

        channels = db.getYoutubeChannels();
        Log.d("sizeOFCHANNELS", ((Integer) channels.size()).toString());
        list.setAdapter(new YoutubeChannelAdapter(this, R.layout.listitem_youtubechannel, channels));

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(AddYoutubeChannel.this, ViewYoutubeChannel.class);
                intent.putExtra("id", position);
                startActivity(intent);
            }
        });

    }

    public void onResume(){
        super.onResume();

        channels = db.getYoutubeChannels();
        Log.d("sizeOFCHANNELS", ((Integer) channels.size()).toString());
        list.setAdapter(new YoutubeChannelAdapter(this, R.layout.listitem_youtubechannel, channels));
    }

    public void addChannelClicked(View v){

        ArrayList<YoutubeChannel> chans = db.getYoutubeChannels();
        String newChan = channelInput.getText().toString();

        for(YoutubeChannel chan : chans){
            if(chan.url.equals(newChan)){
                Toast toast = Toast.makeText(this, "Channel already saved", Toast.LENGTH_SHORT);
                toast.show();
                return;
            }
        }

        new Feeds(this).testYoutubeChannel(newChan);

    }

}
