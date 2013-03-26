package com.app.objects;

import java.util.ArrayList;
import java.util.List;

public class Album {
	private String title;
	private Photo cover_photo;
	private List<Photo> photos;
	
	public Album(){
		this.title="no title";
		this.cover_photo=null;
		this.photos=new ArrayList<Photo>();
	}
	
	public Album(String title,Photo cover_photo,ArrayList<Photo> photos){
		this.title=title;
		this.cover_photo=cover_photo;
		this.photos=photos;		
	}
	public Album(String title,Photo cover_photo){
		this.title=title;
		this.cover_photo=cover_photo;
		this.photos=new ArrayList<Photo>();
	}
	
	public void addPhoto(Photo photo){
		this.photos.add(photo);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Photo getCover_photo() {
		return cover_photo;
	}

	public void setCover_photo(Photo cover_photo) {
		this.cover_photo = cover_photo;
	}

	public List<Photo> getPhotos() {
		return photos;
	}

	public void setPhotos(List<Photo> photos) {
		this.photos = photos;
	}

}
