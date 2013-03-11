package com.app.fotocloud.facebook;

import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.app.fotocloud.R;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;

public class PhotoGridViewerFragment extends SherlockFragment {
	
	private static final String TAG = "PHOTO_GRID_VIEWER";
	
	private ImageView imageView;
	
	private UiLifecycleHelper uiHelper;
	private Session.StatusCallback callback;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActivity().setContentView(R.layout.fb_photo_grid_viewer);
		
		uiHelper = new UiLifecycleHelper(getActivity(), callback);
	    uiHelper.onCreate(savedInstanceState);
		
		callback = new Session.StatusCallback() {
		    @Override
		    public void call(final Session session, final SessionState state, final Exception exception) {
		        onSessionStateChange(session, state, exception);
		    }
		};
		getPhotos();
	}

	

	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getActivity().getMenuInflater().inflate(R.menu.activity_photo_grid_viewer_fragment,
				menu);
		return true;
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	    View view = inflater.inflate(R.layout.fb_photo_grid_viewer, container, false);
	    return view;
	}
	
	private void onSessionStateChange(final Session session, SessionState state, Exception exception) {
	    if (session != null && session.isOpened()) {
	        // Get the user's data.
	        makeMeRequest(session);
	    }
	}
	private void makeMeRequest(final Session session) {
	    // Make an API call to get user data and define a 
	    // new callback to handle the response.
	    Request request = Request.newMeRequest(session, new Request.GraphUserCallback() {
	        @Override
	        public void onCompleted(GraphUser user, Response response) {
	            // If the response is successful
	            if (session == Session.getActiveSession()) {
	            	
	                if (user != null) {
	                    // Set the id for the ProfilePictureView
	                    // view that in turn displays the profile picture.
	                    //profilePictureView.setProfileId(user.getId());
	                    // Set the Textview's text to the user's name.
	                    //userNameView.setText(user.getName());
	                }
	            }
	            if (response.getError() != null) {
	                // Handle errors, will do so later.
	            }
	        }
	    });
	    request.executeAsync();
	}
	private void getPhotos() {
		Request request = Request.newGraphPathRequest(Session.getActiveSession(), "http://photos-d.ak.fbcdn.net/hphotos-ak-prn1/72643_111434992379451_1772268028_s.jpg", new Request.Callback() {
			
			@Override
			public void onCompleted(Response response) {
				// TODO Auto-generated method stub
				Object photo = response.getGraphObject();
				Toast.makeText(getActivity().getApplicationContext(), photo.toString(), Toast.LENGTH_LONG).show();
			}
		});
		request.executeAndWait();
		
	}

}
