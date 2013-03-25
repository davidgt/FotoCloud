package com.app.fotocloud.facebook;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.app.fotocloud.R;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.android.Util;
import com.facebook.model.GraphUser;

public class PhotoGridViewerFragment extends SherlockFragment implements OnClickListener {
	
	private static final String TAG = "PHOTO_GRID_VIEWER";
	
	private ImageView imageView;
	
	private UiLifecycleHelper uiHelper;
	private Session.StatusCallback callback;
	
	private String userId;
	
	
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
		
		Session session = Session.getActiveSession();
	    if (session != null && session.isOpened()) {
	        // Get the user's data
	        makeMeRequest(session);
	    }
		
		
	}

	

	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getActivity().getMenuInflater().inflate(R.menu.activity_photo_grid_viewer_fragment,
				menu);
		return true;
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	    View view = inflater.inflate(R.layout.fb_photo_grid_viewer, container, false);
	    ((Button)view.findViewById(R.id.button1)).setOnClickListener(this);
	    ((Button)view.findViewById(R.id.button2)).setOnClickListener(this);
	    imageView = (ImageView) getActivity().findViewById(R.id.imageView1);
	    return view;
	}
	
	public void onClick(View v) {
		final int viewId = v.getId();
		if(viewId==R.id.button1){
			Toast.makeText(getSherlockActivity().getApplicationContext(), "PAÑUM", Toast.LENGTH_LONG).show();
			if(Session.getActiveSession().isOpened()){
			URL img_value = null;
				try {
					//img_value = new URL("http://graph.facebook.com/"+userId+"/picture");
					img_value = new URL("http://photos-c.ak.fbcdn.net/hphotos-ak-frc1/734570_111467615709522_2116195527_s.jpg");
					
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Bitmap mIcon1 = null;
				try {
					mIcon1 = BitmapFactory.decodeStream(img_value.openConnection().getInputStream());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				imageView.setImageBitmap(mIcon1);
			}
		}
		else if(viewId==R.id.button2){
			if(Session.getActiveSession().isOpened()){
				//Toast.makeText(getSherlockActivity().getApplicationContext(), "OLA Q ASHE", Toast.LENGTH_LONG).show();
				Session session = Session.getActiveSession();
				Request request = Request.newGraphPathRequest(session, ""+userId+"/albums", new Request.Callback() {
					JSONObject graphResposte=null;
					JSONArray jsonArray = null;	
					//http://graph.facebook.com/
					@Override
					public void onCompleted(Response response) {
						if(response.getGraphObject()!=null){
							graphResposte = response.getGraphObject().getInnerJSONObject();
							try {
								jsonArray = graphResposte.getJSONArray("data");
							
								String cover_photo[]=new String[jsonArray.length()];
								for(int i = 0; i < jsonArray.length(); i++){				                
										JSONObject c = jsonArray.getJSONObject(i);
										cover_photo[i]=c.getString("cover_photo");
					            }
								Toast.makeText(getSherlockActivity().getApplicationContext(), cover_photo[0], Toast.LENGTH_LONG).show();
							} catch (JSONException e1) {
								// TODO Auto-generated catch block
								Toast.makeText(getSherlockActivity().getApplicationContext(), "JSON_EXCEPTION", Toast.LENGTH_LONG).show();
								e1.printStackTrace();
							}
						}
						else
							Toast.makeText(getSherlockActivity().getApplicationContext(), "NULL", Toast.LENGTH_LONG).show();
						
					}
				});
				request.executeAndWait();
			}			
		}
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
	                	userId=user.getId();
	                }
	            }
	            if (response.getError() != null) {
	                // Handle errors, will do so later.
	            }
	        }
	    });
	    request.executeAndWait();
	}
}
