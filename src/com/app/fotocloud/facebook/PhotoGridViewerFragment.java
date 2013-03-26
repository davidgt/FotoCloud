package com.app.fotocloud.facebook;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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
import com.app.objects.Album;
import com.app.objects.Photo;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;

public class PhotoGridViewerFragment extends SherlockFragment implements OnClickListener {
	
	private static final String TAG = "PHOTO_GRID_VIEWER";
	
	//---------OLD STUFF-----------------------//
	private ImageView imageView;
	List<String> albumsUrlList=new ArrayList<String>();
	//---------------------------------------------//
	private List<Album> albums; 
	Photo photo;
	
	private UiLifecycleHelper uiHelper;
	private Session.StatusCallback callback;
	
	private String userId;
	private Boolean filled;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActivity().setContentView(R.layout.fb_photo_grid_viewer);
		
		filled=false;
		albums=new ArrayList<Album>();
		photo=new Photo();
		uiHelper = new UiLifecycleHelper(getActivity(), callback);
	    uiHelper.onCreate(savedInstanceState);
		
		callback = new Session.StatusCallback() {
		    @Override
		    public void call(final Session session, final SessionState state, final Exception exception) {
		        onSessionStateChange(session, state, exception);
		    }
		};
		//-------------OLD-------------------------//
		Session session = Session.getActiveSession();
	    if (session != null && session.isOpened()) {
	        // Get the user's data
	        makeMeRequest(session);
	    }
	    //--------------------------------------------//
	  
		//Toast.makeText(getSherlockActivity().getApplicationContext(), albums.get(0).getTitle(), Toast.LENGTH_LONG).show();
		Toast.makeText(getSherlockActivity().getApplicationContext(), "OnCreate", Toast.LENGTH_LONG).show();
	}
	
	@Override
	public void onResume() {
	    super.onResume();
	    uiHelper.onResume();
	    if(!filled)
	    	fillAlbums();
	    else{
	    	imageView.setImageBitmap(albums.get(0).getCover_photo().getPhoto());
	    	Toast.makeText(getSherlockActivity().getApplicationContext(), albums.get(0).getTitle(), Toast.LENGTH_LONG).show();
	    }
	    
	    Toast.makeText(getSherlockActivity().getApplicationContext(), "OnResume", Toast.LENGTH_LONG).show();
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getActivity().getMenuInflater().inflate(R.menu.activity_photo_grid_viewer_fragment,
				menu);
		Toast.makeText(getSherlockActivity().getApplicationContext(), "OnCreateOptionsMenu", Toast.LENGTH_LONG).show();
		return true;
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	    View view = inflater.inflate(R.layout.fb_photo_grid_viewer, container, false);
	    ((Button)view.findViewById(R.id.button1)).setOnClickListener(this);
	    ((Button)view.findViewById(R.id.button2)).setOnClickListener(this);
	    imageView = (ImageView) getActivity().findViewById(R.id.imageView1);
	    
	    Toast.makeText(getSherlockActivity().getApplicationContext(), "OnCreateView", Toast.LENGTH_LONG).show();
		
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
							
								String cover_photos[]=new String[jsonArray.length()];
								for(int i = 0; i < jsonArray.length(); i++){				                
										JSONObject c = jsonArray.getJSONObject(i);
										cover_photos[i]=c.getString("cover_photo");
					            }
								getPictures(cover_photos);
							} catch (JSONException e1) {
								// TODO Auto-generated catch block
								Toast.makeText(getSherlockActivity().getApplicationContext(), "JSON_EXCEPTION", Toast.LENGTH_LONG).show();
								e1.printStackTrace();
							}
							//paintPicture();
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
	    Toast.makeText(getSherlockActivity().getApplicationContext(), "OnSesion", Toast.LENGTH_LONG).show();
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
	private void getPictures(String[] pictures){
		Session session = Session.getActiveSession();
		
	
			for(int i=0;i<pictures.length;i++){
				Request request = Request.newGraphPathRequest(session, pictures[i], new Request.Callback() {
				
					@Override
					public void onCompleted(Response response) {
						JSONObject graphResposte=response.getGraphObject().getInnerJSONObject();
						try {
							albumsUrlList.add(graphResposte.getString("picture"));
							Toast.makeText(getSherlockActivity().getApplicationContext(), graphResposte.getString("picture"), Toast.LENGTH_SHORT).show();
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
				});
				request.executeAndWait();
				
		}
	}
	private void paintPicture() {
		URL img_value = null;
		try {
			//img_value = new URL("http://graph.facebook.com/"+userId+"/picture");
			img_value = new URL(albumsUrlList.get(0));
			
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
	private void fillAlbums() {		
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
							
							for(int i = 0; i < jsonArray.length(); i++){				                
									JSONObject c = jsonArray.getJSONObject(i);
									
									String name = c.getString("name");
									String cover_photo = c.getString("cover_photo");
									Toast.makeText(getSherlockActivity().getApplicationContext(), cover_photo, Toast.LENGTH_LONG).show();
									Photo photo = getPhoto(cover_photo);
									albums.add(new Album(name,photo));
				            }
							
						} catch (JSONException e1) {
							// TODO Auto-generated catch block
							Toast.makeText(getSherlockActivity().getApplicationContext(), "JSON_EXCEPTION", Toast.LENGTH_LONG).show();
							e1.printStackTrace();
						}
						imageView.setImageBitmap(albums.get(2).getCover_photo().getPhoto());
				    	
					}
					else
						Toast.makeText(getSherlockActivity().getApplicationContext(), "NULL", Toast.LENGTH_LONG).show();
					
				}
				
				
			});
			request.executeAndWait();
		}	
		
	}
	private Photo getPhoto(String cover_photo) {		
		Session session = Session.getActiveSession();		
		Request request = Request.newGraphPathRequest(session, cover_photo, new Request.Callback() {
			
				@Override
				public void onCompleted(Response response) {
					JSONObject graphResposte=response.getGraphObject().getInnerJSONObject();
					try {					
						String title = graphResposte.getString("name");
						String url=graphResposte.getString("picture");
						Bitmap bitmap=getBitmap(url);
						photo.setPhoto(bitmap);
						photo.setTitle(title);
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}

				
			});
			request.executeAndWait();
			
	
		return photo;
	}
	private Bitmap getBitmap(String url) {
		URL img_value = null;
		try {
			//img_value = new URL("http://graph.facebook.com/"+userId+"/picture");
			img_value = new URL(url);
			
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
		
		return mIcon1;
	}

}
