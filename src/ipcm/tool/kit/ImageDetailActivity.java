package ipcm.tool.kit;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.SparseArray;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import uk.co.senab.photoview.HackyViewPager;
import uk.co.senab.photoview.PhotoViewAttacher;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class ImageDetailActivity extends FragmentActivity {

    // Layout elements
    private HackyViewPager mPager;  // The view pager
    public static LinearLayout detailsPane; // The details pane that shows up on tap events
    private static ImageView HDimage;
    private Button HDbutton;
    private TextView picDescription;
    private TextView picAuthor;
    //private LinearLayout arrowsLayout;
    private static ProgressBar progressBar;

    // Adapters, etc.
    private ImagePagerAdapter mAdapter; // The adapter that binds to the view pager

    // Data lists
    public static ArrayList<Picture> bitmaps;   // The list of picture objects associated with the view pager
    public static SparseArray<Bitmap> cache;    // A cache for the bitmaps so we don't have to reload

    // Helper variables
    private static int position;    // The starting position of the view pager
    private Intent intent;  // The intent that is passed in containing info
    private Context context = this; // The context for this activity
    private static Bitmap loadedBitmap;
    public static Bitmap currBitmap;
    private static PhotoViewAttacher attacher;

    /*
    Determines if the device has an internet connection
    @return true if the device has an internet connection, false otherwise
     */
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /*
    Notifies the user via a toast that no internet connection is available
     */
    public void notifyNoNetworkAvailable(){
        Toast toast = Toast.makeText(this, "Internet connection unavailable", 1500);
        toast.show();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_viewimage); // Contains just a ViewPager

        // Parse the intent
        intent = getIntent();
        position = intent.getIntExtra("position", 0);
		bitmaps = intent.getParcelableArrayListExtra("pictures");
		cache = new SparseArray<Bitmap>();

        // Get layout elements
        detailsPane = (LinearLayout)findViewById(R.id.detailspane);
        mPager = (HackyViewPager) findViewById(R.id.pager);
        HDimage = (ImageView) findViewById(R.id.hd_image);
        HDbutton = (Button) findViewById(R.id.HDbutton);
        picDescription = (TextView) findViewById(R.id.picDescription);
        picAuthor = (TextView) findViewById(R.id.picAuthor);
        //arrowsLayout = (LinearLayout) findViewById(R.id.arrowsLayout);
        progressBar = (ProgressBar) findViewById(R.id.progbar);

        // Init the view pager
        mAdapter = new ImagePagerAdapter(getSupportFragmentManager(), bitmaps.size());
        mPager.setAdapter(mAdapter);
        mPager.setCurrentItem(position);

        picDescription.setText(bitmaps.get(mPager.getCurrentItem()).getTitle());
        picAuthor.setText(bitmaps.get(mPager.getCurrentItem()).getAuthor());

        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){

            public void onPageSelected(int position){
                picDescription.setText(bitmaps.get(mPager.getCurrentItem()).getTitle());
                picAuthor.setText(bitmaps.get(mPager.getCurrentItem()).getAuthor());
            }

        });

        if(!isNetworkAvailable())
            notifyNoNetworkAvailable();
              
    }

    // Called when the share button is clicked in the details pane
    public void shareButtonClicked(View v){
        registerForContextMenu(v);
        openContextMenu(v);
        unregisterForContextMenu(v);
    }

    // Called when the HD button in clicked in the details pane
    public void HDButtonClicked(View v){
        if(HDbutton.getText().equals("HD")){

            // Get the image
            try{
                URL url = bitmaps.get(mPager.getCurrentItem()).getHDURL();
                new LoadBitmapTask().execute(url);
            }catch(Exception e){
                e.printStackTrace();
            }
        }else{
            mPager.setVisibility(View.VISIBLE);
            HDimage.setVisibility(View.GONE);

            // Fix the layout
            HDbutton.setText("HD");

            // Remove image
            HDimage.setImageBitmap(null);
        }

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Share this image");

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.layout.menu_image_share_options, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.save:

                // Get the info about the picture
                Bitmap bitmap = cache.get(mPager.getCurrentItem());
                String title = bitmaps.get(mPager.getCurrentItem()).getThumbnailURL().toString();
                String description = bitmaps.get(mPager.getCurrentItem()).getTitle();

                // Try storing the image to the phone and report back the result
                if(MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, title, description) != null){
                    Toast toast = Toast.makeText(this, "Image saved to gallery", 1000);
                    toast.show();
                }else{
                    Toast toast = Toast.makeText(this, "Image failed to save", 1000);
                    toast.show();
                }

                break;
            case R.id.email:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("plain/text");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[] { "" });
                intent.putExtra(Intent.EXTRA_SUBJECT, "Picture of " + bitmaps.get(mPager.getCurrentItem()).getTitle());

                // Attach image
                bitmap = cache.get(mPager.getCurrentItem());
                FileOutputStream fos;
                String FILENAME = "image.jpg";
                File file;
                if(bitmap != null){
                    try{
                        fos = openFileOutput(FILENAME, Context.MODE_WORLD_READABLE);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                        fos.close();
                        file = new File(context.getFilesDir(), FILENAME);
                        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                    }catch(FileNotFoundException e){
                        Toast toast = Toast.makeText(this, "FILE NOT FOUND", 1000);
                        toast.show();
                    }catch(IOException e){
                        Toast toast = Toast.makeText(this, "IO EXCEPTION", 1000);
                        toast.show();
                    }
                    startActivity(Intent.createChooser(intent, ""));
                }else{
                    Toast toast = Toast.makeText(this, "An error occurred sending the image", 1500);
                    toast.show();
                }
                break;
        }
        return true;
    }

    // The adapter for the view pager
    public static class ImagePagerAdapter extends FragmentStatePagerAdapter {
        private final int mSize;

        public ImagePagerAdapter(FragmentManager fm, int size) {
            super(fm);
            mSize = size;
        }

        @Override
        public int getCount() {
            return mSize;
        }

        @Override
        public Fragment getItem(int _position) {
        	
            ImageDetailFragment frag = ImageDetailFragment.newInstance(_position, this);
            frag.detailsPane = detailsPane;
            return frag;
            
        }
        
    }

    // Loads an image
    public class LoadBitmapTask extends AsyncTask<URL, Void, Bitmap> {

        int pos = -1;

        protected void onPreExecute(){
            progressBar.setVisibility(View.VISIBLE);
        }

        protected Bitmap doInBackground(URL... params){

            try{
                Log.e("url", params[0].toString());
                return BitmapFactory.decodeStream(params[0].openConnection().getInputStream());
            }catch(IOException e){
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(Bitmap _bmp){
            if(_bmp != null){
                progressBar.setVisibility(View.GONE);
                ImageDetailActivity.currBitmap = _bmp;
                HDimage.setImageBitmap(_bmp);
                attacher = new PhotoViewAttacher(HDimage);
                mPager.setVisibility(View.GONE);
                HDimage.setVisibility(View.VISIBLE);

                // Fix the layout
                HDbutton.setText("Back");   // Change the button text
            }else{
                Log.e("bitmap", "bitmap was null");
                Toast toast = Toast.makeText(context, "No HD image available", 1000);
                toast.show();
                progressBar.setVisibility(View.GONE);
                mPager.setVisibility(View.VISIBLE);
                HDimage.setVisibility(View.GONE);

                // Fix the layout
                HDbutton.setText("HD");   // Change the button text
            }
        }

    }
}