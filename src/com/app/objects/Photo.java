package com.app.objects;

import android.graphics.Bitmap;

public class Photo {
	private String title;
	private Bitmap photo;
	
	public Photo(){
		this.title="no_title";
		this.photo=null;
	}
	public Photo(String title,Bitmap bitmap){
		this.title=title;
		this.photo=bitmap;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Bitmap getPhoto() {
		return photo;
	}

	public void setPhoto(Bitmap photo) {
		this.photo = photo;
	}
}
