package com.app.objects;

public class User {
	private String uid;
	private String name;
	
	public User(String uid, String name) {
		super();
		this.uid = uid;
		this.name = name;
	}
	public User (String uid){this(uid,"noname");}
	public User (){this("null","noname");}
	
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
}
