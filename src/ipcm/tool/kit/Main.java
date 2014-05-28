package ipcm.tool.kit;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TabHost;

public class Main extends TabActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);
        
        TabHost tabHost = getTabHost();
        tabHost.setup();
        TabHost.TabSpec spec;
        Intent intent;

        // Set the tabs

        intent = new Intent().setClass(this, News.class);
        spec = tabHost.newTabSpec("news");
        spec.setIndicator("News", getResources().getDrawable(R.drawable.news));
        spec.setContent(intent);
        tabHost.addTab(spec);
        
        intent = new Intent().setClass(this, Videos.class);
        spec = tabHost.newTabSpec("videos");
        spec.setIndicator("Videos", getResources().getDrawable(R.drawable.videos));
        spec.setContent(intent);
        tabHost.addTab(spec);
        
        intent = new Intent().setClass(this, Publications.class);
        spec = tabHost.newTabSpec("publications");
        spec.setIndicator("Publications", getResources().getDrawable(R.drawable.pubs));
        spec.setContent(intent);
        tabHost.addTab(spec);
        
        intent = new Intent().setClass(this, Pictures.class);
        spec = tabHost.newTabSpec("pictures");
        spec.setIndicator("Pictures", getResources().getDrawable(R.drawable.pics));
        spec.setContent(intent);
        tabHost.addTab(spec);
    }

    /*
         * Creates the context menu
         */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.layout.menu_context_menu, menu);
        return true;
    }
    
    /*
     * Specifies the behavior of the options menu item(s)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.help:	Intent intent = new Intent(this, Info.class);
            				startActivity(intent);
            				return true;
            default:		return false;
        }
    }
    
}