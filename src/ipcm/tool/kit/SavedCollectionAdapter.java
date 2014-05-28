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

public class SavedCollectionAdapter extends ArrayAdapter<Collection>{

	// Instance variables
	int resource;
	private ArrayList<Collection> cols;

	public SavedCollectionAdapter(Context _context, int _resource, ArrayList<Collection> _cols) {
		super(_context, _resource, _cols);
		resource = _resource;
		cols = _cols;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;		
		if(view == null)
		{
			LayoutInflater vi = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		    view = vi.inflate(resource, null);
		}

		Collection col = cols.get(position);		
		if(col != null){
			TextView top = (TextView)view.findViewById(R.id.top);
			if(top!=null)
				top.setText(col.getTitle());			
			TextView bottom = (TextView)view.findViewById(R.id.bottom);
			if(bottom!=null){
				String text = "";
				
				if(col.getCollOrSub().equals("collection"))
					text+="Collection #";
				else
					text+="Subject #";
				
				bottom.setText(text + col.getNumber());
			}
							
		}

		return view;
	}

}