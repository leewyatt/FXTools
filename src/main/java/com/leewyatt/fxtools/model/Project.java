package com.leewyatt.fxtools.model;

import java.util.List;

public class Project {
	private String descEn;
	private List<String> keywords;
	private String descZh;
	private String name;
	private String url;

	private String type;

	public void setDescEn(String descEn){
		this.descEn = descEn;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDescEn(){
		return descEn;
	}

	public void setKeywords(List<String> keywords){
		this.keywords = keywords;
	}

	public List<String> getKeywords(){
		return keywords;
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

	public void setUrl(String url){
		this.url = url;
	}

	public String getUrl(){
		return url;
	}
}