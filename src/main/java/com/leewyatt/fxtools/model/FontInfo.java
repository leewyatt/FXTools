package com.leewyatt.fxtools.model;

import java.util.Locale;
import java.util.Objects;

/**
 * @author LeeWyatt
 */
public class FontInfo {
    private String family;

    private String name;

    private String path;

    public FontInfo() {
    }

    public FontInfo(String family, String name) {
        this.family = family;
        this.name = name;
    }

    public FontInfo(String family, String name, String path) {
        this.family = family;
        this.name = name;
        this.path = path;

    }

    public boolean containKeywords(String keywords) {
        return name.toLowerCase(Locale.ROOT).contains(keywords.toLowerCase(Locale.ROOT))
                || family.toLowerCase(Locale.ROOT).contains(keywords.toLowerCase(Locale.ROOT));
    }

    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
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

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FontInfo fontInfo = (FontInfo) o;
        return Objects.equals(path, fontInfo.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path);
    }
}
