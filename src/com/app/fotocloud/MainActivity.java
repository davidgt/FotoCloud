package com.app.fotocloud;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.app.fotocloud.facebook.AlbumListViewerFragment;
import com.app.fotocloud.facebook.PhotoGridViewerFragment;
import com.app.fotocloud.facebook.SplashFragment;
import com.app.objects.FacebookData;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;

@SuppressLint("NewApi")
public class MainActivity extends SherlockFragmentActivity {

	private static final int REQ_CODE_PICK_IMAGE = 100;
	
	private Intent photoPickerIntent;
	
	private AlbumListViewerFragment albumfragment;
	private SplashFragment splashFragment;
	private PhotoGridViewerFragment photoGridViewerFragment;
	
	private boolean isResumed = false;
	
	private UiLifecycleHelper uiHelper;
	private Session.StatusCallback callback;
	private FragmentManager fm;
	
	private MenuItem settings;
	private MenuItem clearUser;
	private MenuItem uploadPhoto;
	
	public int aux;
	public String albumid;
	
	private FragmentTransaction fragmentTransaction;
	
	//0=splash, 1=AlbumsList, 2=PhotoGrid
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
	public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
		com.actionbarsherlock.view.MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.activity_main, menu);
		return true;
	}
	@Override
	public boolean onPrepareOptionsMenu(com.actionbarsherlock.view.Menu menu) {
		menu.clear();
	    settings = menu.add(R.string.settings);
	    clearUser = menu.add(R.string.clearuser);
	    uploadPhoto = menu.add(R.string.uploadPhoto);
		return true;		
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
			fragmentTransaction.add(R.id.splashFragment, splashFragment);			
			fragmentTransaction.commit();
			
			fragmentVisible=0;
	    	return true;    	
	    }
	    if(item.equals(uploadPhoto)){
	    	photoPickerIntent = new Intent(Intent.ACTION_PICK);
	    	photoPickerIntent.setType("image/*");
	    	startActivityForResult(photoPickerIntent, REQ_CODE_PICK_IMAGE); 
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
	        	Toast.makeText(getApplicationContext(), "LOG!", Toast.LENGTH_LONG).show();
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
	    Session session = Session.getActiveSession();
	    
	    if (session != null && session.isOpened()) {
	        // if the session is already open,
	        // try to show the selection fragment
	        //showFragment(ALBUMLIST, false);
	    } else {
	        // otherwise present the splash screen
	        // and ask the user to login.
	        //showFragment(SPLASH, false);
	    	
	    }
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
			default: super.onBackPressed();
			
		}
		Session session = Session.getActiveSession();
		if(session!=null && session.isOpened()){
			
		}
	    //super.onBackPressed();
		
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

	
	
	/*@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	  super.onActivityResult(requestCode, resultCode, data);
	  Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
	}*/

}
