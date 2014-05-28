package ipcm.tool.kit;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.*;
import twitter4j.*;

import java.util.ArrayList;
import java.util.HashMap;

public class AddTwitterFeed extends Activity {

    private EditText userInput;
    private EditText userListInput;
    private Button addButton;

    DataHelper db = new DataHelper(this);

    HashMap<String, String> slugs = new HashMap<String, String>();

    Context context = this;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_addtwitterfeed);

        userInput = (EditText)findViewById(R.id.twitter_user_input);
        userListInput = (EditText)findViewById(R.id.twitter_list_input);
        addButton = (Button)findViewById(R.id.add_twitter_feed_button);

        userInput.addTextChangedListener(new TextWatcher(){

            public void beforeTextChanged(CharSequence s, int start, int before, int count){

            }

            public void onTextChanged(CharSequence s, int start, int before, int count){

                if(s.length() == 0){
                    return;
                }

                if(s.charAt(0) != '@'){
                    userInput.setText("@" + userInput.getText());
                }

            }

            public void afterTextChanged(Editable s){

                if(userInput.getText().length() < 1)
                    return;

                userInput.setSelection(userInput.getText().length());

            }

        });

    }

    public void addTwitterFeed(View v){

        String user = userInput.getText().toString();
        String list = userListInput.getText().toString();

        list = list.replace(' ', '-');
        list = list.replace('.', '-');
        list = list.toLowerCase();


        if(user.equals("@WisCropMan") && list.equals("wisag")){
            Toast t = Toast.makeText(this, "Feed already exists", Toast.LENGTH_SHORT);
            t.show();
            return;
        }

        if(user.length() > 1){
            if(user.charAt(0) == '@'){
                user = user.substring(1);
            }
        }


        if(!list.equals(""))
            user += "$" + list;

        db.insertTwitterFeed(user);

        finish();

    }

}
