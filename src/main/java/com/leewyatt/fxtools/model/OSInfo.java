package com.leewyatt.fxtools.model;

/**
 * @author LeeWyatt
 */
public class OSInfo {
    public static final String OS_APPLE = "Apple";//iphone ipad watchOs macos
    public static final String OS_ANDROID = "Android";
    public static final String OS_WIN = "Windows";
    public static final String OS_LINUX = "Linux";
    private String osName;
    private boolean selected;

    public OSInfo() {
    }

    public OSInfo(String osName, boolean selected) {
        this.osName = osName;
        this.selected = selected;
    }

    public String getOsName() {
        return osName;
    }

    public void setOsName(String osName) {
        this.osName = osName;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
