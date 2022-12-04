package com.leewyatt.fxtools.model;

import com.google.gson.annotations.SerializedName;

public class FXToolsVersion {
	@SerializedName("tag_name")
	private String tagName;

	public String getTagName() {
		return tagName;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
	}
}
