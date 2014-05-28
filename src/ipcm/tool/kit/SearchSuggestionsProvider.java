package ipcm.tool.kit;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.util.Log;

/*
Provides the search suggestions for image query to the Bugwood
Image database.
 */

public class SearchSuggestionsProvider extends ContentProvider {
	
	public boolean onCreate(){
		
		return true;
	}

	public Cursor query(Uri uri, String[] projection, String startsOrContains, String[] selectionArgs, String keyword){
		
		String query = "http://images.bugwood.org/addins/autosuggestlookups/subjectJPImages.cfm?callback=IPMToolkit&term=";
		
		if(Pictures.searchOption == 1)
			query+= '*';
		
		query += uri.getLastPathSegment().toLowerCase().replace(' ', '_');
		
		if(Pictures.searchOption == 2)
			query = "";
		
		Log.d("searchTerm", query);
		
		JSONArray array = new JSONArray();
		BufferedReader reader = null;
				
		try{
			URL url = new URL(query);
			reader = new BufferedReader(new InputStreamReader(url.openStream()));
	        StringBuffer buffer = new StringBuffer();
	        int read;
	        char[] chars = new char[8192];
	        while ((read = reader.read(chars)) != -1)
	            buffer.append(chars, 0, read);
	        
	        String json = buffer.toString();
	       // Log.d("json", json);
	        json = json.substring(json.indexOf("["), json.indexOf("]") + 1);
	        Log.d("json", json);
	        Log.d("buffer", ((Integer)json.length()).toString());
	        array = new JSONArray(json);
		}catch(Exception e){
			e.printStackTrace();
			Log.d("exception", "exception");
		}
		
		String[] colNames = new String[4];
		colNames[0] = "_id";
		colNames[1] = SearchManager.SUGGEST_COLUMN_TEXT_1;
		colNames[2] = SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID;
		colNames[3] = SearchManager.SUGGEST_COLUMN_INTENT_EXTRA_DATA;
		MatrixCursor c = new MatrixCursor(colNames);
		

		Log.d("length", ((Integer)array.length()).toString());
		
		try{
			for(int i = 0; i< array.length(); i++){
				JSONObject row = array.getJSONObject(i);
				String id = row.getString("id");
				String value = row.getString("value");
				String usename = row.getString("usename");
				
				c.addRow(new String[]{id, value, id, value});
			}
			
		}catch(Exception e){
			Log.d("secondexception", "except");
		}
		Log.d("size", ((Integer)c.getCount()).toString());
		return c;
		
		
		
	}
	
	public Uri insert(Uri uri, ContentValues values){
		return uri;
	}
	
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs){
		return 0;
	}
	
	public int delete(Uri uri, String selection, String[] selectionArgs){
		return 0;
	}
	
	public String getType(Uri url){
		return "";
	}

    public static ArrayList<SearchSuggestion> simpleSearchSuggestQuery(String keyword){

        String query = "http://images.bugwood.org/addins/autosuggestlookups/subjectJPImages.cfm?callback=IPMToolkit&term=";

        if(Pictures.searchOption == 1)
            query+= '*';

        query += keyword.replace(' ', '_');

        if(Pictures.searchOption == 2)
            query = "";

        Log.d("searchTerm", query);

        JSONArray array = new JSONArray();
        BufferedReader reader = null;

        try{
            URL url = new URL(query);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuffer buffer = new StringBuffer();
            int read;
            char[] chars = new char[8192];
            while ((read = reader.read(chars)) != -1)
                buffer.append(chars, 0, read);

            String json = buffer.toString();
            // Log.d("json", json);
            json = json.substring(json.indexOf("["), json.indexOf("]") + 1);
            Log.d("json", json);
            Log.d("buffer", ((Integer)json.length()).toString());
            array = new JSONArray(json);
        }catch(Exception e){
            e.printStackTrace();
            Log.d("exception", "exception");
        }

        String[] colNames = new String[4];
        colNames[0] = "_id";
        colNames[1] = SearchManager.SUGGEST_COLUMN_TEXT_1;
        colNames[2] = SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID;
        colNames[3] = SearchManager.SUGGEST_COLUMN_INTENT_EXTRA_DATA;
        MatrixCursor c = new MatrixCursor(colNames);


        Log.d("length", ((Integer)array.length()).toString());

        ArrayList<SearchSuggestion> suggestions = new ArrayList<SearchSuggestion>();

        try{
            for(int i = 0; i< array.length(); i++){
                JSONObject row = array.getJSONObject(i);
                String id = row.getString("id");
                String value = row.getString("value");
                String usename = row.getString("usename");

                suggestions.add(new SearchSuggestion(id, value, usename));

                c.addRow(new String[]{id, value, id, value});
            }

        }catch(Exception e){
            Log.d("secondexception", "except");
        }

        return suggestions;

    }
	
}
