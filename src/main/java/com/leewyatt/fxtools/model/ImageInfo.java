package com.leewyatt.fxtools.model;

import java.util.Objects;

/**
 * @author LeeWyatt
 */
public class ImageInfo {
    private String path;
    private String name;
    private String format;
    private String fileLen;

    private String cacheUrl;

    private int width;
    private int height;


    public ImageInfo() {
    }

    public String getCacheUrl() {
        return cacheUrl;
    }

    public void setCacheUrl(String cacheUrl) {
        this.cacheUrl = cacheUrl;
    }

    public ImageInfo( String path, String name, String format, String fileLen, String cacheUrl, int width, int height) {
        this.path = path;
        this.name = name;
        this.format = format;
        this.fileLen = fileLen;
        this.cacheUrl = cacheUrl;
        this.width = width;
        this.height = height;
    }



    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getFileLen() {
        return fileLen;
    }

    public void setFileLen(String fileLen) {
        this.fileLen = fileLen;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImageInfo imageInfo = (ImageInfo) o;
        return path.equals(imageInfo.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path);
    }
}
