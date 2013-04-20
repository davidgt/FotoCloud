package com.app.fotocloud.facebook;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.app.fotocloud.R;
import com.facebook.widget.LoginButton;

public class SplashFragment extends SherlockFragment {
	
	private LoginButton loginButton;
	private List<String> readPermissions;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActivity().setContentView(R.layout.fb_splash);
		
		loginButton = (LoginButton) getActivity().findViewById(R.id.login_button);	
		readPermissions=new ArrayList<String>();
		readPermissions.add("user_photos");
		readPermissions.add("photo_upload");
		loginButton.setReadPermissions(readPermissions);
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getActivity().getMenuInflater().inflate(R.menu.splash, menu);
		return true;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	    View view = inflater.inflate(R.layout.fb_splash, container, false);
	    return view;
	}

}
