package com.app.objects;

import java.util.ArrayList;
import java.util.List;

public class FacebookData {
	private User user;
	private List<Album> albums;
	
	public FacebookData(User user, ArrayList<Album> albums) {
		super();
		this.user = user;
		this.albums = albums;
	}
	public FacebookData(){this(null,new ArrayList<Album>());}	

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public List<Album> getAlbums() {
		return albums;
	}

	public void setAlbums(List<Album> albums) {
		this.albums = albums;
	}
	
	public void addAlbum(Album album){
		this.albums.add(album);
	}
	
	
	
}
