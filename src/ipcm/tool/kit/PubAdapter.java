package ipcm.tool.kit;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PubAdapter extends ArrayAdapter<Publication>{	
	// Instance variables
	int resource;
	private ArrayList<Publication> pubs;

	public PubAdapter(Context _context, int _resource, ArrayList<Publication> _pubs) {
		super(_context, _resource, _pubs);
		resource = _resource;
		pubs = _pubs;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View view = convertView;		
		if(view == null)
		{
			LayoutInflater vi = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		    view = vi.inflate(resource, null);
		}

        // Alternate background colors
        if(position%2 == 1)
            view.setBackgroundColor(getContext().getResources().getColor(R.color.light_grey));
        else
            view.setBackgroundColor(getContext().getResources().getColor(R.color.white));

		Publication pub = pubs.get(position);		
		if(pub != null){
			TextView title = (TextView)view.findViewById(R.id.title);
			if(title!=null)
				title.setText(pub.getTitle());
		}		
		return view;
	}

}