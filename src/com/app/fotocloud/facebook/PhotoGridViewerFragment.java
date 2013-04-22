package com.app.fotocloud.facebook;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
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
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.app.debug.Debug;
import com.app.fotocloud.MainActivity;
import com.app.fotocloud.R;
import com.app.objects.Photo;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;

public class PhotoGridViewerFragment extends SherlockFragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
	
	private static final String TAG = "PHOTO_GRID_VIEWER";
	
	private GridView gridView;
	
	List<String> albumsUrlList=new ArrayList<String>();
	
	private List<Photo> photoList;
	
	private UiLifecycleHelper uiHelper;
	private Session.StatusCallback callback;
	
	private String albumId;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActivity().setContentView(R.layout.fb_photo_grid_viewer);
		
		albumId=getArguments().getString("albumId");
		photoList = new ArrayList<Photo>();
		uiHelper = new UiLifecycleHelper(getActivity(), callback);
	    uiHelper.onCreate(savedInstanceState);
		
		callback = new Session.StatusCallback() {
		    @Override
		    public void call(final Session session, final SessionState state, final Exception exception) {
		        onSessionStateChange(session, state, exception);
		    }
		}; 
		
	}
	@Override
	public void onStart() {
		super.onStart();
		
		Session session = Session.getActiveSession();
		if(albumId!="null" && albumId!=null){
		    if (session != null && session.isOpened()) {
		        // Get the user's data
		    	if(((MainActivity)getSherlockActivity()).isNetworkAvailable()){
		    		fillPhotos(session);
		    	}
		    	else
		    		Toast.makeText(getSherlockActivity().getApplicationContext(), "No Network Available", Toast.LENGTH_LONG).show();
		       
		    }
		    if(photoList.size()>0){
			    gridView = (GridView)getActivity().findViewById(R.id.gridView);
				gridView.setAdapter(new ImageAdapter(getSherlockActivity()));
				gridView.setOnItemClickListener(this);
				gridView.setOnItemLongClickListener(this);
			}
	    }
	}

	
	@Override
	public void onResume() {
	    super.onResume();
	    uiHelper.onResume();
	    Session session = Session.getActiveSession();
		if(albumId!="null" && albumId!=null){
		    if (session != null && session.isOpened()) {
		        // Get the user's data
		    	if(((MainActivity)getSherlockActivity()).isNetworkAvailable()){
		    		fillPhotos(session);
		    	}
		    	else
		    		Toast.makeText(getSherlockActivity().getApplicationContext(), "No Network Available", Toast.LENGTH_LONG).show();
		       
		    }
		    if(photoList.size()>0){
			    gridView = (GridView)getActivity().findViewById(R.id.gridView);
				gridView.setAdapter(new ImageAdapter(getSherlockActivity()));
				gridView.setOnItemClickListener(this);
				gridView.setOnItemLongClickListener(this);
			}
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
	    
	    return view;
	}
	
	private void onSessionStateChange(final Session session, SessionState state, Exception exception) {
	    if (session != null && session.isOpened()) {
	        // Get the user's data.
	        fillPhotos(session);
	    }
	    Toast.makeText(getSherlockActivity().getApplicationContext(), "OnSesion", Toast.LENGTH_LONG).show();
	}
	
	private void fillPhotos(final Session session) {	
		photoList.clear();
		photoList.removeAll(photoList);
			Request request = Request.newGraphPathRequest(session, ""+albumId+"/photos", new Request.Callback() {				
				JSONObject graphResposte=null;
				JSONArray jsonArray = null;
				@Override
				public void onCompleted(Response response) {
					if(response.getGraphObject()!=null){
						graphResposte = response.getGraphObject().getInnerJSONObject();
						try {
							jsonArray = graphResposte.getJSONArray("data");			
							
							for(int i = 0; i < jsonArray.length(); i++){				                
									JSONObject c = jsonArray.getJSONObject(i);
									
									//String name = c.getString("name");
									String name="no_name";
									String sphoto = c.getString("picture");
									//Toast.makeText(getSherlockActivity().getApplicationContext(), cover_photo, Toast.LENGTH_LONG).show();
									
									Bitmap bitmap = getBitmapFromURL(sphoto);
									Photo photo = new Photo(name,bitmap);
									//Debug.out(photo.getPhoto().toString());
									photoList.add(photo);
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
	
				}
				
				public Bitmap getBitmapFromURL(String src) {
				    try {
				        URL url = new URL(src);
				        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				        connection.setDoInput(true);
				        connection.connect();
				        InputStream input = connection.getInputStream();
				        Bitmap myBitmap = BitmapFactory.decodeStream(input);
				        return myBitmap;
				    } catch (IOException e) {
				        e.printStackTrace();
				        return null;
				    }
				}
				
				
			});
			request.executeAndWait();		
	}
	
	public class ImageAdapter extends BaseAdapter{
		private Context context;
		private GridView.LayoutParams mImageViewLayoutParams;
		
		public ImageAdapter(Context c){
			context=c;
		}
		public int getCount(){
			return photoList.size();
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
				 mImageViewLayoutParams = new GridView.LayoutParams(
		                    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
				imageView = new ImageView(context);
				//imageView.setLayoutParams(new GridView.LayoutParams(85,85));
				imageView.setLayoutParams(mImageViewLayoutParams);
				imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
				imageView.setPadding(5,5,5,5);
			}
			else{
				imageView = (ImageView) convertView;
			}
			//imageView.setImageResource(imageIDs[position]);
			if(photoList.size()>0){
				imageView.setImageBitmap(photoList.get(position).getPhoto());
				Debug.out(photoList.size());
			}
			return imageView;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		// TODO Auto-generated method stub
		//Toast.makeText(getSherlockActivity().getApplicationContext(), ""+arg2, Toast.LENGTH_SHORT).show();
		if(!onItemLongClick(arg0, arg1, position, arg3))
			((MainActivity)getSherlockActivity()).callImageView(photoList,position);
	}
	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		Toast.makeText(getActivity().getApplicationContext(), "Long!", Toast.LENGTH_SHORT ).show();
		((MainActivity)getSherlockActivity()).showDownloadPhotoDialog(photoList.get(arg2).getPhoto());
		return false;
	}
	

}
