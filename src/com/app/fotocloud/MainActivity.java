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
import android.view.View;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;

@SuppressLint("NewApi")
public class MainActivity extends SherlockFragmentActivity {
	
	private static final int SPLASH = 0;
	//private static final int SELECTION = 1;
	private static final int SETTINGS = 1;
	private static final int PHOTOVIEWER = 2;
	private static final int FRAGMENT_COUNT = PHOTOVIEWER + 1;

	private static final int REQ_CODE_PICK_IMAGE = 100;
	
	private Fragment[] fragments = new Fragment[FRAGMENT_COUNT];
	private Intent photoPickerIntent;
	
	private boolean isResumed = false;
	
	private UiLifecycleHelper uiHelper;
	private Session.StatusCallback callback;
	private FragmentManager fm;
	
	private MenuItem settings;
	private MenuItem clearUser;
	private MenuItem uploadPhoto;
	
	private LoginButton loginButton;
	private List<String> readPermissions;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);	
		
		loginButton = (LoginButton) findViewById(R.id.login_button);		
		Session session = Session.getActiveSession();
		session.close();
		
		readPermissions=new ArrayList<String>();
		readPermissions.add("user_photos");
		readPermissions.add("photo_upload");
		loginButton.setReadPermissions(readPermissions);
		
		fm = getSupportFragmentManager();
	    fragments[SPLASH] = (Fragment) fm.findFragmentById(R.id.splashFragment);
	    //fragments[SELECTION] = fm.findFragmentById(R.id.selectionFragment);
	    fragments[SETTINGS] = (Fragment) fm.findFragmentById(R.id.userSettingsFragment);
	    fragments[PHOTOVIEWER]= (Fragment) fm.findFragmentById(R.id.photoGridViewerFragment);

	    FragmentTransaction transaction = fm.beginTransaction();
	    for(int i = 0; i < fragments.length; i++) {
	        transaction.hide(fragments[i]);
	    }
	    transaction.commit();
	    
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
	    // only add the menu when the selection fragment is showing
	    if (fragments[PHOTOVIEWER].isVisible()) {
	        if (menu.size() == 0) {
	            settings = menu.add(R.string.settings);
	            clearUser = menu.add(R.string.clearuser);
	            uploadPhoto = menu.add(R.string.uploadPhoto);
	        }
	        return true;
	    } else {
	        menu.clear();
	        settings = null;
	    }
	    return false;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    if (item.equals(settings)) {
	        showFragment(SETTINGS, true);
	        return true;
	    }
	    if (item.equals(clearUser)){
	    	final Session session = Session.getActiveSession();
	    	new Thread(){
	            public void run() {
	                session.closeAndClearTokenInformation();
	            };
	            }.start();
		    if (session != null && session.isOpened()) {
		        Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {
					
					@Override
					public void onCompleted(GraphUser user, Response response) {
						if(user!=null){							
							session.closeAndClearTokenInformation();
						}
						
					}
				});
		    }
	    	return true;
	    }
	    if(item.equals(uploadPhoto)){
	    	photoPickerIntent = new Intent(Intent.ACTION_PICK);
	    	photoPickerIntent.setType("image/*");
	    	startActivityForResult(photoPickerIntent, REQ_CODE_PICK_IMAGE); 
	    }
	    return false;
	}
	
	private void showFragment(int fragmentIndex, boolean addToBackStack) {
	    FragmentManager fm = getSupportFragmentManager();
	    FragmentTransaction transaction = fm.beginTransaction();
	    for (int i = 0; i < fragments.length; i++) {
	        if (i == fragmentIndex) {
	            transaction.show(fragments[i]);
	        } else {
	            transaction.hide(fragments[i]);
	        }
	    }
	    if (addToBackStack) {
	        transaction.addToBackStack(null);
	    }
	    transaction.commit();
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
	            showFragment(PHOTOVIEWER, false);
	        } else if (state.isClosed()) {
	            // If the session state is closed:
	            // Show the login fragment
	            showFragment(SPLASH, false);
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
	        showFragment(PHOTOVIEWER, false);
	    } else {
	        // otherwise present the splash screen
	        // and ask the user to login.
	        showFragment(SPLASH, false);
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
	
	/*@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	  super.onActivityResult(requestCode, resultCode, data);
	  Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
	}*/

}
