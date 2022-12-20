package com.leewyatt.fxtools.model;

import com.google.gson.annotations.SerializedName;

public class FXToolsVersion {
	@SerializedName("tag_name")
	private String tagName;

	private String ver;

	public String getVer() {
		return ver;
	}

	public void setVer(String ver) {
		this.ver = ver;
	}

	public String getTagName() {
		return tagName;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
	}
}
