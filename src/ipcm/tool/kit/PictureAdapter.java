package ipcm.tool.kit;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PictureAdapter extends ArrayAdapter<Picture>{

	// Instance variables
	int resource;
	private ArrayList<Picture> pics;
	private ImageView thumb;
	HashMap<Integer, Bitmap> bitmaps = new HashMap<Integer, Bitmap>();

	public PictureAdapter(Context _context, int _resource, ArrayList<Picture> _pics) {
		super(_context, _resource, _pics);
		resource = _resource;
		pics = _pics;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent){
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

		Picture pic = pics.get(position);
		if(pic != null){
			thumb = (ImageView)view.findViewById(R.id.thumbnail);
			if(thumb != null){
				Bitmap bmp = pic.getThumbnail();
				if(bmp != null)
					thumb.setImageBitmap(bmp);
				else
					thumb.setImageResource(R.drawable.loading_leaf);
			}
			TextView title = (TextView)view.findViewById(R.id.picturetitle);
			if(title!=null)
				title.setText(pic.getTitle());
			TextView author = (TextView)view.findViewById(R.id.author);
			if(author!=null)
				author.setText(pic.getAuthor());
		}
		
		return view;
	}
	
	public void setBitmap(int position, Bitmap pic){
		bitmaps.put(position, pic);
	}

}