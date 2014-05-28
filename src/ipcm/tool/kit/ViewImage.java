package ipcm.tool.kit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.*;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout.LayoutParams;
import uk.co.senab.photoview.HackyViewPager;

import java.io.IOException;
import java.util.ArrayList;

/*
The activity for viewing a collection of images
 */

public class ViewImage extends Activity{
	
	private ViewFlipper flipper;
	private int _screenWidth;
    private int _screenHeight;
    private int position;
	Intent intent;
	Picture currpic;
	Picture prevpic;
	Picture nextpic;
	//PhotoGalleryLinearLayout gallery;
	public static HackyViewPager gallery;
    private LinearLayout detailsPane;
    private RelativeLayout mainLayout;
	private ImageView zoomedImage;
	LinearLayout picLayout;
	
	private ArrayList<Picture> pictures;
	private ArrayList<ImageView> images;
	//private GestureDetector gdt;
	private Context context = this;

	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_viewimage);
		
		// LAYOUT CODE
		
		Display display = getWindowManager().getDefaultDisplay();
        _screenWidth = display.getWidth();
        _screenHeight = display.getHeight();
        gallery = (HackyViewPager)findViewById(R.id.pager);
        
        // GET INITIAL PICTURES
		
		intent = getIntent();
		
		position = intent.getIntExtra("position", 0);
		pictures = intent.getParcelableArrayListExtra("pictures");
        detailsPane = (LinearLayout)findViewById(R.id.detailspane);
        mainLayout = (RelativeLayout)findViewById(R.id.mainLayout);
		
	}
	
	protected void onStart(){
		super.onStart();
	}

}
