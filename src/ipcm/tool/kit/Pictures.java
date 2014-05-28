package ipcm.tool.kit;

import java.util.ArrayList;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.*;

public class Pictures extends ListActivity{

	private TextView startsWith;
	private TextView contains;
	private TextView collection;
	private EditText searchBar;
	public static int searchOption = 0;
	private ListView list;
	private ArrayList<Collection> collections;
	DataHelper db;
	SearchManager searchManager;
	
	public int getSearchOption(){
		return this.searchOption;
	}
	
	// The action performed when an item in the list is clicked on
	protected void onListItemClick(ListView l, View v, int position, long id){

		Collection col = collections.get(position);
		Intent intent = new Intent(this, Result.class);
		intent.putExtra("title", col.getTitle());
		intent.putExtra("collOrSub", col.getCollOrSub());
		intent.putExtra("number", col.getNumber());
		startActivity(intent);
		
		Log.d("leaving", "Pictures");
		
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
	    AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
	    menu.setHeaderTitle(collections.get(info.position).getTitle());
	    menu.add(Menu.NONE, Menu.NONE, Menu.NONE, "Delete");
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
		Log.d("id", ((Integer)info.position).toString());

        if(collections.get(info.position).getNumber().equals("72071")){
            Toast toast = Toast.makeText(this, "Cannot delete this collection", 1500);
            toast.show();
        }else{
            db.removeCollection(collections.get(info.position).getId());
            collections = db.getCollectionList();
        }
	  	list.setAdapter(new SavedCollectionAdapter(this, R.layout.listitem_articleitem, collections));
	  	return true;
	}
	
	@Override
	public boolean onSearchRequested(){
		return super.onSearchRequested();
	}
	
	protected void onResume(){
		super.onResume();
		collections = db.getCollectionList();
		list.setAdapter(new SavedCollectionAdapter(this, R.layout.listitem_articleitem, collections));
		
	}
	
	public void onCreate(Bundle savedInstanceState)	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_pictures);
		
		final Resources res = getResources();
		
		startsWith = (TextView) findViewById(R.id.startsWith);
		contains = (TextView) findViewById(R.id.contains);
		collection = (TextView) findViewById(R.id.collection);
		searchBar = (EditText) findViewById(R.id.searchbar);
		list = getListView();
		db = new DataHelper(this);
		searchManager = (SearchManager)this.getSystemService(Context.SEARCH_SERVICE);
		this.registerForContextMenu(list);
		
		this.setDefaultKeyMode(DEFAULT_KEYS_SEARCH_LOCAL);
		
		if(db.getAllCollections().getCount() == 0)
			db.insertCollection("IPM Toolkit Featured Images", "collection", "72071");
		
		collections = db.getCollectionList();
		
		list.setAdapter(new SavedCollectionAdapter(this, R.layout.listitem_articleitem, collections));
		
		// The behavior of the crop manager button when clicked
		startsWith.setOnClickListener( new OnClickListener(){
			
			public void onClick(View v)
			{
				searchOption = 0;
				
				startsWith.setBackgroundColor(res.getColor(R.color.black));
				contains.setBackgroundColor(res.getColor(R.color.dark_grey));	
				collection.setBackgroundColor(res.getColor(R.color.dark_grey));	
			}
			
		});
		
		// The behavior of the crop manager button when clicked
		contains.setOnClickListener( new OnClickListener(){
			
			public void onClick(View v)
			{
				searchOption = 1;
				
				startsWith.setBackgroundColor(res.getColor(R.color.dark_grey));
				contains.setBackgroundColor(res.getColor(R.color.black));	
				collection.setBackgroundColor(res.getColor(R.color.dark_grey));	
			}
			
		});
		
		// The behavior of the crop manager button when clicked
		collection.setOnClickListener( new OnClickListener(){
			
			public void onClick(View v)
			{
				searchOption = 2;
				//SearchableInfo info = searchManager.getSearchableInfo(getComponentName());
				
				startsWith.setBackgroundColor(res.getColor(R.color.dark_grey));
				contains.setBackgroundColor(res.getColor(R.color.dark_grey));	
				collection.setBackgroundColor(res.getColor(R.color.black));	
			}
			
		});
		
		searchBar.setOnClickListener( new OnClickListener(){
			
			public void onClick(View v){
				
					onSearchRequested();
					Log.d("search", "searching");
				
			}
			
		});
		
		searchBar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
		    
		    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
		            return true;
		        }
		        return false;
		    }
		});
		
	}
	
}
