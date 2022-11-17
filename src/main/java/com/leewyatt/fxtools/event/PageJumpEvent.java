package com.leewyatt.fxtools.event;

/**
 * @author LeeWyatt
 */
public class PageJumpEvent {
    private int pageIndex;

    public PageJumpEvent() {
    }

    public PageJumpEvent(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }
}
