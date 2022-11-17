package com.leewyatt.fxtools.model;

import com.google.gson.annotations.SerializedName;
/**
 * @author LeeWyatt
 */
public class AppleIcon {
	private String filename;
	private String size;
	private String scale;
	private String idiom;
	@SerializedName("expected-size")
	private String expectedSize;
	private String role;
	private String subtype;

	public AppleIcon() {
	}

	public AppleIcon(String filename, String size, String scale, String idiom, String expectedSize, String role, String subtype) {
		this.filename = filename;
		this.size = size;
		this.scale = scale;
		this.idiom = idiom;
		this.expectedSize = expectedSize;
		this.role = role;
		this.subtype = subtype;
	}

	public void setFilename(String filename){
		this.filename = filename;
	}

	public String getFilename(){
		return filename;
	}

	public void setSize(String size){
		this.size = size;
	}

	public String getSize(){
		return size;
	}

	public void setScale(String scale){
		this.scale = scale;
	}

	public String getScale(){
		return scale;
	}

	public void setIdiom(String idiom){
		this.idiom = idiom;
	}

	public String getIdiom(){
		return idiom;
	}



	public void setRole(String role){
		this.role = role;
	}

	public String getRole(){
		return role;
	}

	public void setSubtype(String subtype){
		this.subtype = subtype;
	}

	public String getSubtype(){
		return subtype;
	}

	public String getExpectedSize() {
		return expectedSize;
	}

	public void setExpectedSize(String expectedSize) {
		this.expectedSize = expectedSize;
	}

	@Override
	public String toString() {
		return "AppleIcon{" +
				"filename='" + filename + '\'' +
				", size='" + size + '\'' +
				", scale='" + scale + '\'' +
				", idiom='" + idiom + '\'' +
				", expectedSize='" + expectedSize + '\'' +
				", role='" + role + '\'' +
				", subtype='" + subtype + '\'' +
				'}';
	}
}
