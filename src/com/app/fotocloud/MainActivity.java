package com.app.fotocloud;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.app.fotocloud.common.ImageViewPagerFragment;
import com.app.fotocloud.facebook.AlbumListViewerFragment;
import com.app.fotocloud.facebook.DownloadPhotoDialog;
import com.app.fotocloud.facebook.PhotoGridViewerFragment;
import com.app.fotocloud.facebook.SplashFragment;
import com.app.fotocloud.facebook.UploadPhotoDialog;
import com.app.objects.Photo;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;

@SuppressLint("NewApi")
public class MainActivity extends SherlockFragmentActivity implements DownloadPhotoDialog.NoticeDialogListener{

	private static final int REQ_CODE_PICK_IMAGE = 100;
	private static final int CAMERA_REQUEST = 101;
	
	private Intent photoPickerIntent;
	
	private AlbumListViewerFragment albumfragment;
	private SplashFragment splashFragment;
	private PhotoGridViewerFragment photoGridViewerFragment;
	private ImageViewPagerFragment imageViewPagerFragment;
	
	private boolean isResumed = false;
	
	private UiLifecycleHelper uiHelper;
	private Session.StatusCallback callback;
	private FragmentManager fm;
	
	private MenuItem settings;
	private MenuItem clearUser;
	private MenuItem uploadPhoto;
	
	public int aux;
	public String albumid;
	private List<Bitmap> bitmapPhotoList;
	private Bitmap photoDownloadBitmap;
	
	private FragmentTransaction fragmentTransaction;
	
	//0=splash, 1=AlbumsList, 2=PhotoGrid, 3=ImageView
	private int fragmentVisible;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);	
		
		fm = getSupportFragmentManager();
		fragmentTransaction = fm.beginTransaction();
		splashFragment = new SplashFragment();
		fragmentTransaction.add(R.id.splashFragment, splashFragment);
		
		fragmentTransaction.commit();
		fragmentVisible=0;
		
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle("Fotocloud");
		
		bitmapPhotoList = new ArrayList<Bitmap>();	
	    
	    callback = new Session.StatusCallback() {
	        @Override
	        public void call(Session session, SessionState state, Exception exception) {
	            onSessionStateChange(session, state, exception);
	            
	        }
	    };
	    
	    uiHelper = new UiLifecycleHelper(this, callback);
	    uiHelper.onCreate(savedInstanceState);
	    
		// Add code to print out the key hash
	    /*try {
	        PackageInfo info = getPackageManager().getPackageInfo(
	                "com.facebook.samples.hellofacebook", 
	                PackageManager.GET_SIGNATURES);
	        for (Signature signature : info.signatures) {
	            MessageDigest md = MessageDigest.getInstance("SHA");
	            md.update(signature.toByteArray());
	            Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
	            }
	    } catch (NameNotFoundException e) {

	    } catch (NoSuchAlgorithmException e) {

	    }*/
		
	}
	@Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub

        super.onConfigurationChanged(newConfig);

          if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {

          } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {

          }

    }

	

	@Override
	public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
		com.actionbarsherlock.view.MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.activity_main, menu);
		return true;
	}
	@Override
	public boolean onPrepareOptionsMenu(com.actionbarsherlock.view.Menu menu) {
		if(isNetworkAvailable()){
			menu.clear();
		    settings = menu.add(R.string.settings);
		    clearUser = menu.add(R.string.clearuser);
		    uploadPhoto = menu.add(R.string.uploadPhoto);
		    return true;
		}
		return false;		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    if (item.equals(settings)) {
	    	Toast.makeText(getApplicationContext(), "OLA", Toast.LENGTH_SHORT ).show();	    	
	        return true;
	    }
	    if (item.equals(clearUser)){
	    	final Session session = Session.getActiveSession();
	    	session.closeAndClearTokenInformation();
		    splashFragment = new SplashFragment();
			fragmentTransaction = fm.beginTransaction(); 
			fragmentTransaction.remove(photoGridViewerFragment);
			fragmentTransaction.remove(albumfragment);
			fragmentTransaction.remove(imageViewPagerFragment);
			fragmentTransaction.add(R.id.splashFragment, splashFragment);			
			fragmentTransaction.commit();
			
			fragmentVisible=0;
	    	return true;    	
	    }
	    if(item.equals(uploadPhoto)){
	    	DialogFragment newFragment = new UploadPhotoDialog();
		    newFragment.show(getSupportFragmentManager(), "photoUpload");
	    }
	    if(item.getItemId()==android.R.id.home){
	    	onUpPressed();	
	    }
	    return false;
	}
	
	
	@Override
	public void onResume() {
	    super.onResume();
	    uiHelper.onResume();
	    isResumed = true;
	    
	}

	@Override
	public void onPause() {
	    super.onPause();
	    uiHelper.onPause();
	    isResumed = false;
	}
	
	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
	    // Only make changes if the activity is visible
	    if (isResumed) {
	        FragmentManager manager = getSupportFragmentManager();
	        // Get the number of entries in the back stack
	        int backStackSize = manager.getBackStackEntryCount();
	        // Clear the back stack
	        for (int i = 0; i < backStackSize; i++) {
	            manager.popBackStack();
	        }
	        if (state.isOpened()) {
	            // If the session state is open:
	            // Show the authenticated fragment
	        	     
	        	fragmentTransaction = fm.beginTransaction(); 
	        	albumfragment = new AlbumListViewerFragment();
				fragmentTransaction.add(R.id.albumListViewerFragment, albumfragment);
				fragmentTransaction.commit();
				fragmentVisible=1;
	        } else if (state.isClosed()) {
	        	
			    fragmentTransaction = fm.beginTransaction();
		
			    splashFragment = new SplashFragment();
				fragmentTransaction.add(R.id.splashFragment, splashFragment);
				fragmentTransaction.commit();
				fragmentVisible=0;
	        }
	    }
	}
	
	@Override
	protected void onResumeFragments() {
	    super.onResumeFragments();
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    uiHelper.onActivityResult(requestCode, resultCode, data);
	    
	    switch(requestCode) { 
		    case REQ_CODE_PICK_IMAGE:
		        if(resultCode == RESULT_OK){ 		        	
		            
		            Uri selectedImage = data.getData();
		            InputStream imageStream = null;
					try {
						imageStream = getContentResolver().openInputStream(selectedImage);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		            Bitmap yourSelectedImage = BitmapFactory.decodeStream(imageStream);		            
		            
		            Session session=Session.getActiveSession();
		            Request request = Request.newUploadPhotoRequest(session, yourSelectedImage, new Request.Callback() {
		            
						@Override
						public void onCompleted(Response response) {
							// TODO Auto-generated method stub
							Toast.makeText(getApplicationContext(), "Photo Uploaded", Toast.LENGTH_SHORT ).show();
						}
						
					});
		            // Get the current parameters for the request
		            Bundle params = request.getParameters();
		            // Add the parameters you want, the caption in this case
		            params.putString("name", "My Caption String");
		            // Update the request parameters
		            request.setParameters(params);

		            // Execute the request		            
		            Request.executeAndWait(request);
		            
		        }
		        break;
		    case CAMERA_REQUEST:
		    	Bitmap yourPhoto = (Bitmap) data.getExtras().get("data");
		    	Session session=Session.getActiveSession();
	            Request request = Request.newUploadPhotoRequest(session, yourPhoto, new Request.Callback() {
	            
					@Override
					public void onCompleted(Response response) {
						// TODO Auto-generated method stub
						Toast.makeText(getApplicationContext(), "Photo Uploaded", Toast.LENGTH_SHORT ).show();
					}
					
				});
	            // Get the current parameters for the request
	            Bundle params = request.getParameters();
	            // Add the parameters you want, the caption in this case
	            params.putString("name", "My Caption String");
	            // Update the request parameters
	            request.setParameters(params);

	            // Execute the request		            
	            Request.executeAndWait(request);
		   
	    }
	}
	
	@Override
	public void onDestroy() {
	    super.onDestroy();
	    uiHelper.onDestroy();	    
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    uiHelper.onSaveInstanceState(outState);
	}
	
	@Override
	public void onBackPressed() {
	    // TODO Auto-generated method stub
		switch(fragmentVisible){
			case 0: super.onBackPressed();
					break;
			case 1: Session.getActiveSession().closeAndClearTokenInformation();
	    			splashFragment = new SplashFragment();
	    			fragmentTransaction = fm.beginTransaction(); 
	    			fragmentTransaction.remove(albumfragment);
	    			fragmentTransaction.add(R.id.splashFragment, splashFragment);			
	    			fragmentTransaction.commit();			
	    			fragmentVisible=0;
		    		break;
			case 2: fragmentTransaction = fm.beginTransaction(); 
					fragmentTransaction.remove(photoGridViewerFragment);
        			albumfragment = new AlbumListViewerFragment();
        			fragmentTransaction.add(R.id.albumListViewerFragment, albumfragment);
        			fragmentTransaction.commit();
        			fragmentVisible=1;
        			break;
			case 3: fragmentTransaction = fm.beginTransaction(); 
					fragmentTransaction.remove(imageViewPagerFragment);
					fragmentTransaction.remove(photoGridViewerFragment);
					albumfragment = new AlbumListViewerFragment();
					fragmentTransaction.add(R.id.albumListViewerFragment, albumfragment);
					fragmentTransaction.commit();
					fragmentVisible=2;
					break;
			default: super.onBackPressed();
			
		}
		
	}
	public void onUpPressed() {
	    // TODO Auto-generated method stub
		switch(fragmentVisible){
			case 0: 
					break;
			case 1: Session.getActiveSession().closeAndClearTokenInformation();
	    			splashFragment = new SplashFragment();
	    			fragmentTransaction = fm.beginTransaction(); 
	    			fragmentTransaction.remove(albumfragment);
	    			fragmentTransaction.add(R.id.splashFragment, splashFragment);			
	    			fragmentTransaction.commit();			
	    			fragmentVisible=0;
		    		break;
			case 2: fragmentTransaction = fm.beginTransaction(); 
					fragmentTransaction.remove(photoGridViewerFragment);
        			albumfragment = new AlbumListViewerFragment();
        			fragmentTransaction.add(R.id.albumListViewerFragment, albumfragment);
        			fragmentTransaction.commit();
        			fragmentVisible=1;
        			break;
			case 3: fragmentTransaction = fm.beginTransaction(); 
					fragmentTransaction.remove(imageViewPagerFragment);
					fragmentTransaction.remove(photoGridViewerFragment);
					albumfragment = new AlbumListViewerFragment();
					fragmentTransaction.add(R.id.albumListViewerFragment, albumfragment);
					fragmentTransaction.commit();
					fragmentVisible=2;
					break;
			default: super.onBackPressed();
			
		}
		
	}
	
	
	public void callGridView(String albumid){
		fragmentTransaction = fm.beginTransaction(); 
		Bundle args = new Bundle();
		args.putString("albumId", albumid);
		    
		photoGridViewerFragment = new PhotoGridViewerFragment();
		photoGridViewerFragment.setArguments(args);
		fragmentTransaction.add(R.id.gridViewGroup, photoGridViewerFragment);
		fragmentTransaction.commit(); 
		fragmentVisible=2;
		
	}
	
	public boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager 
	         = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null;
	}



	public void callImageView(List<Photo> photoList, int position) {
		// TODO Auto-generated method stub7
		setPhotoBitmapList(photoList);
		
		fragmentTransaction = fm.beginTransaction(); 
		imageViewPagerFragment = new ImageViewPagerFragment();
		fragmentTransaction.add(R.id.imageViewFragment, imageViewPagerFragment);
		fragmentTransaction.commit();
		fragmentVisible=3;
	}



	private void setPhotoBitmapList(List<Photo> photoList) {
		// TODO Auto-generated method stub
		bitmapPhotoList.clear();
		for(int i=0;i<photoList.size();i++){
			bitmapPhotoList.add(photoList.get(i).getPhoto());
		}
		
	}
	
	public List<Bitmap> getPhotoBitmapList() {
		// TODO Auto-generated method stub
		return bitmapPhotoList;
	}
	
	public void showDownloadPhotoDialog(Bitmap photo){
		DialogFragment newFragment = new DownloadPhotoDialog();
	    newFragment.show(getSupportFragmentManager(), "photoDownload");
	    photoDownloadBitmap = photo;
	}



	@Override
	public void onDialogPositiveClick(DialogFragment dialog) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void onDialogNegativeClick(DialogFragment dialog) {
		// TODO Auto-generated method stub
		
	}



	public void downloadPhoto() {
		// TODO Auto-generated method stub
		try{
	        File _sdCard = Environment.getExternalStorageDirectory();
	        File _picDir  = new File(_sdCard, "fotoCloud");
	        _picDir.mkdirs();

	        File _picFile = new File(_picDir,  "MyImage.jpg");
	        FileOutputStream _fos = new FileOutputStream(_picFile);
	        photoDownloadBitmap.compress(Bitmap.CompressFormat.JPEG, 100, _fos);
	        _fos.flush();
	        _fos.close();
	        Toast.makeText(getApplicationContext(), "Photo Downloaded", Toast.LENGTH_SHORT ).show();
	    }catch (Exception ex){
	        ex.printStackTrace();
	       
	    }
	}

	public void uploadUsingDisk() {
		photoPickerIntent = new Intent(Intent.ACTION_PICK);
    	photoPickerIntent.setType("image/*");
    	startActivityForResult(photoPickerIntent, REQ_CODE_PICK_IMAGE); 
		
	}

	public void uploadUsingCamera() {
		 Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
         // request code
         startActivityForResult(cameraIntent, CAMERA_REQUEST);		
	}

}
