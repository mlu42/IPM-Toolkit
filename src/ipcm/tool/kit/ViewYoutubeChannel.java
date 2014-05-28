package ipcm.tool.kit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class ViewYoutubeChannel extends Activity {

    TextView channelId;
    int id = 0;
    DataHelper db = new DataHelper(this);

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_viewyoutubechannel);

        channelId = (TextView)findViewById(R.id.edit_channelId);

        Intent i = getIntent();

        if(i.hasExtra("id")){
            id = i.getIntExtra("id", 0);
            channelId.setText(db.getYoutubeChannels().get(id).url);
        }

    }

    public void deleteYoutubeChannel(View v){

        if(channelId.getText().toString().toLowerCase().equals("uwipm")){
            Toast toast = Toast.makeText(this, "Cannot remove this channel", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        db.removeYoutubeChannel(channelId.getText().toString());
        finish();
    }

}
