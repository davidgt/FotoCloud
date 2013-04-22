package com.app.fotocloud.common;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.app.fotocloud.MainActivity;
import com.app.fotocloud.R;

public class ImageViewPagerFragment extends SherlockFragment implements AdapterView.OnClickListener{
	
	static ImageViewPagerFragment newInstance(){
		return new ImageViewPagerFragment();
	}
	
	private List<Bitmap> mImageList;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getActivity().setContentView(R.layout.view_pager_layout);
		mImageList=((MainActivity)getSherlockActivity()).getPhotoBitmapList();
		ViewPager viewPager = (ViewPager)getActivity().findViewById(R.id.view_pager_container);
		ImagePagerAdapter adapter = new ImagePagerAdapter(mImageList);
		viewPager.setAdapter(adapter);
		viewPager.setOnClickListener(this);
		adapter.setPrimaryItem(null, 2, null);
		
		
		// Set the number of pages that should be retained to either 
		// side of the current page in the view hierarchy in an idle state.
		viewPager.setOffscreenPageLimit(1);
		
		// custom the action bar
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.view_pager_layout, container, false); 
		ViewPager viewPager = (ViewPager)view.findViewById(R.id.view_pager_container);
		ImagePagerAdapter adapter = new ImagePagerAdapter(mImageList);
		viewPager.setAdapter(adapter);
		
		
		// Set the number of pages that should be retained to either 
		// side of the current page in the view hierarchy in an idle state.
		viewPager.setOffscreenPageLimit(2);

		return view;
	}

	private class ImagePagerAdapter extends PagerAdapter {

		private List<Bitmap> mImageList;

		public ImagePagerAdapter(final List<Bitmap> resIdList) {
			mImageList = resIdList;
		}
		public void setPrimaryItem(ViewGroup container, int position, Object object) {
				
				super.setPrimaryItem(container, position, object);
		}

		@Override
		public int getCount() {
			return mImageList.size();
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == ((ImageView) object);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			Context context = container.getContext();
			ImageView imageView = new ImageView(context);
			imageView.setImageBitmap(mImageList.get(position));
			((ViewPager) container).addView(imageView);
			return imageView;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			((ViewPager) container).removeView((ImageView) object);
		}
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
	}
	
}
