package com.app.fotocloud.facebook;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListFragment;
import com.app.debug.Debug;
import com.app.fotocloud.MainActivity;
import com.app.fotocloud.R;
import com.app.objects.Album;
import com.app.objects.FacebookData;
import com.app.objects.Photo;
import com.app.objects.User;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;

public class AlbumListViewerFragment extends SherlockListFragment {

	private String[] albumArray;
	
	private FacebookData facebookData;
	private List<String> photoIdList;
	
	private UiLifecycleHelper uiHelper;
	private Session.StatusCallback callback;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getActivity().setContentView(R.layout.list_view_fragment);
		
		facebookData=new FacebookData();
		photoIdList = new ArrayList<String>();
		
		uiHelper = new UiLifecycleHelper(getActivity(), callback);
	    uiHelper.onCreate(savedInstanceState);
	    
		callback = new Session.StatusCallback() {
		    @Override
		    public void call(final Session session, final SessionState state, final Exception exception) {
		        onSessionStateChange(session, state, exception);
		    }

			private void onSessionStateChange(Session session,
					SessionState state, Exception exception) {
				// TODO Auto-generated method stub
				
			}
		}; 
		//albumArray=((MainActivity)getSherlockActivity()).getAlbumNames();
		Session session = Session.getActiveSession();
	    if (session != null && session.isOpened()) {
	        // Get the user's data
	    	fillFacebookData(session);
	    	
	        if(facebookData.getAlbums().size()>0){
	        	albumArray=new String[facebookData.getAlbums().size()];
	        	for(int i=0;i<facebookData.getAlbums().size();i++){
	        		albumArray[i]=facebookData.getAlbums().get(i).getTitle();
	        	}
	        }
	    } 
		
	}
	
	
	private void fillFacebookData(Session session) {
		makeMeRequest(session);
		fillAlbums(session);		
	}
	
	private void fillAlbums(final Session session) {					
		Request request = Request.newGraphPathRequest(session, ""+facebookData.getUser().getUid()+"/albums", new Request.Callback() {
			JSONObject graphResposte=null;
			JSONArray jsonArray = null;
			Photo photo=new Photo();
			@Override
			public void onCompleted(Response response) {
				if(response.getGraphObject()!=null){
					graphResposte = response.getGraphObject().getInnerJSONObject();
					try {
						jsonArray = graphResposte.getJSONArray("data");			
						
						for(int i = 0; i < jsonArray.length(); i++){				                
								JSONObject c = jsonArray.getJSONObject(i);
								
								String id = c.getString("id");
								String name = c.getString("name");
								String cover_photo = c.getString("cover_photo");
								
								//Toast.makeText(getSherlockActivity().getApplicationContext(), cover_photo, Toast.LENGTH_LONG).show();
								
								Photo photo = getPhoto(cover_photo);
								Debug.out(photo.getPhoto().toString());
								photoIdList.add(id);
								facebookData.addAlbum(new Album(name,new Photo("title",photo.getPhoto())));
			            }
						
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						Toast.makeText(getSherlockActivity().getApplicationContext(), "JSON_EXCEPTION", Toast.LENGTH_LONG).show();
						e1.printStackTrace();
					}
					//imageView.setImageBitmap(albums.get(0).getCover_photo().getPhoto());
			    	
				}
				else
					Toast.makeText(getSherlockActivity().getApplicationContext(), "NULL", Toast.LENGTH_LONG).show();
				for(int i=0;i<facebookData.getAlbums().size();i++){
					Debug.out(facebookData.getAlbums().get(i).getCover_photo().getPhoto().toString());
				}
			}
			private Photo getPhoto(String cover_photo) {
				Session session = Session.getActiveSession();
				//Debug.out(cover_photo);
				Request request = Request.newGraphPathRequest(session, cover_photo, new Request.Callback() {
						@Override
						public void onCompleted(Response response) {
							JSONObject graphResposte=response.getGraphObject().getInnerJSONObject();
							try {					
								String title = "Cover";
								String url=graphResposte.getString("picture");
								
								Bitmap bitmap=getBitmap(url);
								photo.setPhoto(bitmap);
								photo.setTitle(title);
								
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
						}

						private Bitmap getBitmap(String url) {
							URL img_value = null;
							try {
								img_value = new URL(url);
								
							} catch (MalformedURLException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							Bitmap mIcon1 = null;
							try {
								mIcon1 = BitmapFactory.decodeStream(img_value.openConnection().getInputStream());
							} catch (IOException e) {
								Toast.makeText(getSherlockActivity().getApplicationContext(), "GET_BITMAP_ERROR", Toast.LENGTH_LONG).show();
								e.printStackTrace();
							}
							
							return mIcon1;
						}				
					});
					request.executeAndWait();
					
				return photo;
			}
			
			
		});
		request.executeAndWait();		
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
	                    //set userid and username
	                	User userobj = new User(user.getId(),user.getName());
	                	facebookData.setUser(userobj);
	                	//Toast.makeText(getApplicationContext(), "NAme: "+facebookData.getUser().getName(), Toast.LENGTH_LONG).show();
	                }
	            }
	            if (response.getError() != null) {
	                // Handle errors, will do so later.
	            }
	          
	        }
	    });
	    request.executeAndWait();
	}
	
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	@Override
	public void onResume() {
	    super.onResume();
	    //uiHelper.onResume();
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		//Toast.makeText(getSherlockActivity().getApplicationContext(), "NAme: "+facebookData.getUser().getName(), Toast.LENGTH_LONG).show();
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {	
		super.onActivityCreated(savedInstanceState);
		if(albumArray!=null){
			setListAdapter(new ArrayAdapter<String>(getSherlockActivity().getApplicationContext(),
	                					android.R.layout.simple_list_item_1, albumArray));
		}
		getView().setBackgroundColor(Color.BLACK);
		
		
	}		
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		((MainActivity)getSherlockActivity()).callGridView(photoIdList.get(position));
		
	}

}
