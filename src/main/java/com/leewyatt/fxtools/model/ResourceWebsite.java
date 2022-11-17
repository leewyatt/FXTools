package com.leewyatt.fxtools.model;

import java.util.List;
/**
 * @author LeeWyatt
 */
public class ResourceWebsite{
	private List<Photo> photos;
	private String descZh;
	private String name;
	private String descEn;
	private String url;

	public ResourceWebsite() {
	}

	public void setPhotos(List<Photo> photos){
		this.photos = photos;
	}

	public List<Photo> getPhotos(){
		return photos;
	}

	public void setDescZh(String descZh){
		this.descZh = descZh;
	}

	public String getDescZh(){
		return descZh;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setDescEn(String descEn){
		this.descEn = descEn;
	}

	public String getDescEn(){
		return descEn;
	}

	public void setUrl(String url){
		this.url = url;
	}

	public String getUrl(){
		return url;
	}
}