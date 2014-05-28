package ipcm.tool.kit;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/*
An adapter for the list that shows the collections that the user has saved.
 */

public class SearchSuggestionAdapter extends ArrayAdapter<SearchSuggestion>{

    // Instance variables
    int resource;
    private ArrayList<SearchSuggestion> suggestions;

    public SearchSuggestionAdapter(Context _context, int _resource, ArrayList<SearchSuggestion> _suggestions) {
        super(_context, _resource, _suggestions);
        resource = _resource;
        suggestions = _suggestions;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if(view == null)
        {
            LayoutInflater vi = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = vi.inflate(resource, null);
        }

        SearchSuggestion suggestion = suggestions.get(position);

        // Create view
        if(suggestion != null){
            TextView text = (TextView)view.findViewById(R.id.suggestion_text);
            text.setText(suggestions.get(position).value);
        }

        return view;
    }

}