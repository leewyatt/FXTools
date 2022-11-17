package com.leewyatt.fxtools.utils;

import javafx.util.Pair;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author LeeWyatt
 */
public class ColorScheme {
    private String title;
    private String desc;
    private String[] webColorAry;
    private boolean vertical;

    private List<Pair<Integer, String>> titleList;

    public ColorScheme() {
    }
    public ColorScheme(String title, String[] webColorAry) {
        this.title = title;
        this.webColorAry = webColorAry;
    }
    public ColorScheme(String title,boolean vertical, String[] webColorAry) {
        this(title, webColorAry);
        this.vertical=vertical;
    }
    public ColorScheme(String title, String desc, String[] webColorAry) {
        this(title, webColorAry);
        this.desc = desc;
    }
    public ColorScheme(String title,boolean vertical, String desc, String[] webColorAry) {
        this(title, desc, webColorAry);
        this.vertical=vertical;
    }
    public ColorScheme(String title,List<Pair<Integer, String>> titleList,boolean vertical, String desc, String[] webColorAry) {
        this(title,vertical, desc, webColorAry);
        this.titleList=titleList;
    }

    public List<Pair<Integer, String>> getTitleList() {
        return titleList;
    }

    public void setTitleList(List<Pair<Integer, String>> titleList) {
        this.titleList = titleList;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String[] getWebColorAry() {
        return webColorAry;
    }

    public void setWebColorAry(String[] webColorAry) {
        this.webColorAry = webColorAry;
    }

    public boolean isVertical() {
        return vertical;
    }

    public void setVertical(boolean vertical) {
        this.vertical = vertical;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ColorScheme that = (ColorScheme) o;
        return vertical == that.vertical && Objects.equals(title, that.title) && Objects.equals(desc, that.desc) && Arrays.equals(webColorAry, that.webColorAry) && Objects.equals(titleList, that.titleList);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(title, desc, vertical, titleList);
        result = 31 * result + Arrays.hashCode(webColorAry);
        return result;
    }

    @Override
    public String toString() {
        return title;
    }
}
