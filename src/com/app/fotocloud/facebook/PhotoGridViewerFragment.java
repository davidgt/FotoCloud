package com.app.fotocloud.facebook;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.app.debug.Debug;
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

public class PhotoGridViewerFragment extends SherlockFragment implements OnClickListener {
	
	private static final String TAG = "PHOTO_GRID_VIEWER";
	
	Integer[] imageIDs = {
	        R.drawable.img1,
	        R.drawable.img2,
	        R.drawable.img3,
	        R.drawable.img4,
	        R.drawable.img5,
	        R.drawable.img6,
	        R.drawable.img7,
	        R.drawable.img8,
	        R.drawable.img9,
	        R.drawable.img10,
	};
	private ImageView imageView;
	//---------OLD STUFF-----------------------//
	//private ImageView imageView;
	List<String> albumsUrlList=new ArrayList<String>();
	//---------------------------------------------//
	
	//private List<Album> albums; 
	
	//------NEW WAY--------------//
	private FacebookData facebookData;
	
	private UiLifecycleHelper uiHelper;
	private Session.StatusCallback callback;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActivity().setContentView(R.layout.fb_photo_grid_viewer);
				
		GridView gridView = (GridView)getActivity().findViewById(R.id.gridview);
		gridView.setAdapter(new ImageAdapter(getActivity()));		
		
		
		facebookData=new FacebookData();
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
	        fillAlbums(session);
	    }
	    
	}
	
	@Override
	public void onResume() {
	    super.onResume();
	    uiHelper.onResume();
	    Session session = Session.getActiveSession();
	    if (session != null && session.isOpened()) {
	        // Get the user's data
	        makeMeRequest(session);
	        fillAlbums(session);
	        imageView.setImageBitmap(facebookData.getAlbums().get(2).getCover_photo().getPhoto());
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
	    //((Button)view.findViewById(R.id.button)).setOnClickListener(this);
	    imageView = (ImageView) getActivity().findViewById(R.id.imageView);
	    return view;
	}
	
	public void onClick(View v) {
		final int viewId = v.getId();
		//if(viewId==R.id.button){
			//fillAlbums();
		//}
		
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
	                    //set userid and username
	                	User userobj = new User(user.getId(),user.getName());
	                	facebookData.setUser(userobj);
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
									
									String name = c.getString("name");
									String cover_photo = c.getString("cover_photo");
									//Toast.makeText(getSherlockActivity().getApplicationContext(), cover_photo, Toast.LENGTH_LONG).show();
									
									Photo photo = getPhoto(cover_photo);
									Debug.out(photo.getPhoto().toString());
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
	
	public class ImageAdapter extends BaseAdapter{
		private Context context;
		public ImageAdapter(Context c){
			context=c;
		}
		public int getCount(){
			return imageIDs.length;
		}
		public Object getItem(int position){
			return position;
		}
		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView imageView;
			if(convertView==null){
				imageView = new ImageView(context);
				imageView.setLayoutParams(new GridView.LayoutParams(85,85));
				imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
				imageView.setPadding(5,5,5,5);
			}
			else{
				imageView = (ImageView) convertView;
			}
			imageView.setImageResource(imageIDs[position]);
			return imageView;
		}
	}

}
