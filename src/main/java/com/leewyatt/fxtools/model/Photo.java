package com.leewyatt.fxtools.model;
/**
 * @author LeeWyatt
 */
public class Photo {
	private String photoName;
	private String photoAuthor;

	public Photo() {
	}

	public void setPhotoName(String photoName){
		this.photoName = photoName;
	}

	public String getPhotoName(){
		return photoName;
	}

	public void setPhotoAuthor(String photoAuthor){
		this.photoAuthor = photoAuthor;
	}

	public String getPhotoAuthor(){
		return photoAuthor;
	}
}
