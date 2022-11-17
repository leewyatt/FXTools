package com.leewyatt.fxtools.event;

import com.leewyatt.fxtools.model.ImageInfo;

/**
 * @author LeeWyatt
 */
public class ImageEvent {

    private ImageEventType taskType;
    private ImageInfo imageInfo;

    public ImageEvent() {
    }

    public ImageEvent(ImageEventType taskType, ImageInfo imageInfo) {
        this.taskType = taskType;
        this.imageInfo = imageInfo;
    }

    public ImageEventType getTaskType() {
        return taskType;
    }

    public void setTaskType(ImageEventType taskType) {
        this.taskType = taskType;
    }

    public ImageInfo getImageInfo() {
        return imageInfo;
    }

    public void setImageInfo(ImageInfo imageInfo) {
        this.imageInfo = imageInfo;
    }
}
