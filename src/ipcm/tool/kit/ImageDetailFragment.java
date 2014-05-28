package ipcm.tool.kit;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import ipcm.tool.kit.ImageDetailActivity.ImagePagerAdapter;
import uk.co.senab.photoview.PhotoViewAttacher;

import java.io.IOException;

public class ImageDetailFragment extends Fragment {

    // Layout elements
    public LinearLayout detailsPane;
    public static ImageView mImageView;

    // Adapters, etc.

    // Data lists

    // Helper variables
    private static final String IMAGE_DATA_EXTRA = "resId";
    private static int mImageNum;
    private PhotoViewAttacher attacher;
    public Bitmap bitmap;

    static ImageDetailFragment newInstance(int imageNum, ImagePagerAdapter _adapter) {
        final ImageDetailFragment f = new ImageDetailFragment();
        final Bundle args = new Bundle();
        args.putInt(IMAGE_DATA_EXTRA, imageNum);
        f.setArguments(args);
        return f;
    }

    // Empty constructor, required as per Fragment docs
    public ImageDetailFragment() {}

    @Override
    public void onDestroy(){
        attacher.cleanup();
        super.onDestroy();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImageNum = getArguments() != null ? getArguments().getInt(IMAGE_DATA_EXTRA) : -1;
        Log.d("CREATION", "CREATED");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        // image_detail_fragment.xml contains just an ImageView
        final View v = inflater.inflate(R.layout.layout_image_detail_fragment, container, false);

        // Init the imageview
        mImageView = (ImageView) v.findViewById(R.id.imageView);

        // Check the cache
        if(ImageDetailActivity.cache.get(mImageNum) != null)
        	mImageView.setImageBitmap(ImageDetailActivity.cache.get(mImageNum));
        else{
                try{
                    Bitmap bmp = new LoadBitmapTask().execute(mImageNum).get();
                    bitmap = bmp;
                    mImageView.setImageBitmap(bmp);
                }catch(Exception e){
                    e.printStackTrace();
                }
        }

        // Attach a photoview to the image for interactivity
        attacher = new PhotoViewAttacher(mImageView);

        // Make taps toggle the details pane
        attacher.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float x, float y) {
                detailsPane = ImageDetailActivity.detailsPane;
                if(detailsPane.getVisibility() == View.GONE){
                    detailsPane.setVisibility(View.VISIBLE);
                }
                else{
                    detailsPane.setVisibility(View.GONE);
                }
            }
        });

        return mImageView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    // Loads an image
    public static class LoadBitmapTask extends AsyncTask<Integer, Void, Bitmap> {
    	
    	int pos = -1;
    	
    	protected void onPreExecute(){
    		
    	}
    	
		protected Bitmap doInBackground(Integer... params){			

			pos = params[0];
			
			try{
				return BitmapFactory.decodeStream(ImageDetailActivity.bitmaps.get(pos).getImageURL().openConnection().getInputStream());	
			}catch(IOException e){
				e.printStackTrace();
			}
			
			return null;
		}
		
		protected void onPostExecute(Bitmap _bmp){
			if(_bmp != null){
				ImageDetailActivity.currBitmap = _bmp;
				ImageDetailActivity.cache.put(pos, _bmp);
				mImageView.setImageBitmap(_bmp);
			}
		}
		
    }
    
}