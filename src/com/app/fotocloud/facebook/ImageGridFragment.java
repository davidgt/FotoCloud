package com.app.fotocloud.facebook;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.app.fotocloud.MainActivity;
import com.app.fotocloud.R;
import com.app.fotocloud.common.ImagePagerActivity;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

public class ImageGridFragment extends SherlockFragment{
	
	private ImageLoader imageLoader;
	private AbsListView listView;
	
	List<String> imageUrlsList;
	String[] imageUrls;
	String albumId;

	DisplayImageOptions options;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActivity().setContentView(R.layout.ac_image_grid);
		
		imageLoader =ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration.createDefault(getActivity()));
		
		//Fill Image URLs
		imageUrlsList = new ArrayList<String>();
		albumId=getArguments().getString("albumId");
		
		getPhotoUrls(albumId);
		if(imageUrlsList.size()>0){
			imageUrls = new String[imageUrlsList.size()];
			imageUrlsList.toArray(imageUrls);
		}
		else{
			imageUrls= new String[0];
			Toast.makeText(getSherlockActivity().getApplicationContext(), "This Album is empty", Toast.LENGTH_LONG).show();
		} 

		options = new DisplayImageOptions.Builder()
			.showStubImage(R.drawable.ic_stub)
			.showImageForEmptyUri(R.drawable.ic_empty)
			.showImageOnFail(R.drawable.ic_error)
			.cacheInMemory()
			.cacheOnDisc()
			.bitmapConfig(Bitmap.Config.RGB_565)
			.build();

		if(imageUrls.length!=0){
			listView = (GridView) getActivity().findViewById(R.id.gridView);
			((GridView) listView).setAdapter(new ImageAdapter());
			listView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					startImagePagerActivity(position);
				}
			});
			listView.setOnItemLongClickListener(new OnItemLongClickListener() {

				@Override
				public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
						long arg3) {
					((MainActivity)getSherlockActivity()).showDownloadPhotoDialog(imageUrls[arg2]);
					return false;
				}

			});
		}
		
	}
	/*public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	    View view = inflater.inflate(R.layout.ac_image_grid, container, false);	
	    //((Button)view.findViewById(R.id.button)).setOnClickListener(this);
	    
	    return view;
	}*/
	
	private void getPhotoUrls(String albumId2) {
		Session session = Session.getActiveSession();
		if(albumId!="null" && albumId!=null){
		    if (session != null && session.isOpened()) {
		        // Get the user's data
		    	if(((MainActivity)getSherlockActivity()).isNetworkAvailable()){
		    		fillUrls(session);
		    	}
		    	else
		    		Toast.makeText(getSherlockActivity().getApplicationContext(), "No Network Available", Toast.LENGTH_LONG).show();
		       
		    }
	    }
		
	}

	private void fillUrls(final Session session) {	
		imageUrlsList.clear();
		imageUrlsList.removeAll(imageUrlsList);
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
									JSONArray a = c.getJSONArray("images");
									JSONObject c2= a.getJSONObject(0);
									
									imageUrlsList.add(c2.getString("source"));									
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
			});
			request.executeAndWait();		
	}

	private void startImagePagerActivity(int position) {
		Intent intent = new Intent(getSherlockActivity(), ImagePagerActivity.class);
		intent.putExtra("imagesUrl", imageUrls);
		intent.putExtra("position", position);
		startActivity(intent);
	}
	public static ImageLoaderConfiguration initImageLoader(Context context) {
		// This configuration tuning is custom. You can tune every option, you may tune some of them, 
		// or you can create default configuration by
		//  ImageLoaderConfiguration.createDefault(this);
		// method.
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.enableLogging() // Not necessary in common
				.build();
		return config;
		// Initialize ImageLoader with configuration.
		//ImageLoader.getInstance().init(config);
	}
	
	public class ImageAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return imageUrls.length;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final ImageView imageView;
			if (convertView == null) {
				imageView = (ImageView) getActivity().getLayoutInflater().inflate(R.layout.item_grid_image, parent, false);
			} else {
				imageView = (ImageView) convertView;
			}

			imageLoader.displayImage(imageUrls[position], imageView, options);

			return imageView;
		}
		
	}
}
