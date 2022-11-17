package com.leewyatt.fxtools.model;
/**
 * @author LeeWyatt
 */
public class SvgXml {

	private String width;
	private String height;
	private String trueWidth;
	private String trueHeight;
	private String pathCount;
	private String svgPath;

	public SvgXml() {
	}

	public SvgXml(String width, String height, String trueWidth, String trueHeight, String pathCount, String svgPath) {
		this.width = width;
		this.height = height;
		this.trueWidth = trueWidth;
		this.trueHeight = trueHeight;
		this.pathCount = pathCount;
		this.svgPath = svgPath;
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public String getTrueWidth() {
		return trueWidth;
	}

	public void setTrueWidth(String trueWidth) {
		this.trueWidth = trueWidth;
	}

	public String getTrueHeight() {
		return trueHeight;
	}

	public void setTrueHeight(String trueHeight) {
		this.trueHeight = trueHeight;
	}

	public String getPathCount() {
		return pathCount;
	}

	public void setPathCount(String pathCount) {
		this.pathCount = pathCount;
	}

	public String getSvgPath() {
		return svgPath;
	}

	public void setSvgPath(String svgPath) {
		this.svgPath = svgPath;
	}

	public static final SvgXml getInitXml(){
		return new SvgXml("128", "128", "128", "128", "128", "");
	}
	public static final SvgXml getEmpty(){
		return new SvgXml("", "", "", "", "", "");
	}
	public static final SvgXml getErrorSvgXml(){
		return new SvgXml("Error","Error","Error","Error","Error","");
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((height == null) ? 0 : height.hashCode());
		result = prime * result + ((pathCount == null) ? 0 : pathCount.hashCode());
		result = prime * result + ((svgPath == null) ? 0 : svgPath.hashCode());
		result = prime * result + ((trueHeight == null) ? 0 : trueHeight.hashCode());
		result = prime * result + ((trueWidth == null) ? 0 : trueWidth.hashCode());
		result = prime * result + ((width == null) ? 0 : width.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SvgXml other = (SvgXml) obj;
		if (height == null) {
			if (other.height != null)
				return false;
		} else if (!height.equals(other.height))
			return false;
		if (pathCount == null) {
			if (other.pathCount != null)
				return false;
		} else if (!pathCount.equals(other.pathCount))
			return false;
		if (svgPath == null) {
			if (other.svgPath != null)
				return false;
		} else if (!svgPath.equals(other.svgPath))
			return false;
		if (trueHeight == null) {
			if (other.trueHeight != null)
				return false;
		} else if (!trueHeight.equals(other.trueHeight))
			return false;
		if (trueWidth == null) {
			if (other.trueWidth != null)
				return false;
		} else if (!trueWidth.equals(other.trueWidth))
			return false;
		if (width == null) {
			if (other.width != null)
				return false;
		} else if (!width.equals(other.width))
			return false;
		return true;
	}

}
