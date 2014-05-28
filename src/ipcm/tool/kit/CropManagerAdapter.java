package ipcm.tool.kit;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/*
An adapter for the list of crop manager articles
 */

public class CropManagerAdapter extends ArrayAdapter<CropManagerArticle>{

	// Instance variables
	int resource;
	private ArrayList<CropManagerArticle> articles;
	
	/*
	Constructor
	 */
	public CropManagerAdapter(Context _context, int _resource, ArrayList<CropManagerArticle> _articles) {
		super(_context, _resource, _articles);
		resource = _resource;
		articles = _articles;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        // Get the view
		View view = convertView;		
		if(view == null){
			LayoutInflater vi = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		    view = vi.inflate(resource, null);
		}

        // Alternate between white and soft grey background (solely for the sake of appearance)
        if(position%2 == 1)
            view.setBackgroundColor(getContext().getResources().getColor(R.color.light_grey));
        else
            view.setBackgroundColor(getContext().getResources().getColor(R.color.white));

        // Use the article to create the view
		CropManagerArticle article = articles.get(position);		
		if(article != null){
			TextView top = (TextView)view.findViewById(R.id.top);
			if(top!=null)
				top.setText(article.getTitle());			
			TextView bottom = (TextView)view.findViewById(R.id.bottom);
			if(bottom!=null && article.getDate() != null){
				bottom.setText(new Dates().formatDate(article.getDate()));
                bottom.setVisibility(View.VISIBLE);
            }else{
                bottom.setText("");
                bottom.setVisibility(View.GONE);
            }
		}

        // Return the view
		return view;
	}

}