package com.leewyatt.fxtools.services;

import com.leewyatt.fxtools.event.EventBusUtil;
import com.leewyatt.fxtools.event.ImageEvent;
import com.leewyatt.fxtools.event.ImageEventType;
import com.leewyatt.fxtools.model.ImageInfo;
import com.leewyatt.fxtools.model.ImageSize;
import com.leewyatt.fxtools.utils.FileUtil;
import com.leewyatt.fxtools.utils.ImageUtil;
import com.leewyatt.fxtools.utils.StringUtil;
import com.leewyatt.fxtools.utils.ToolSettingsUtil;

import java.io.File;

/**
 * @author LeeWyatt
 */
public class TaskAnalyzeImageInfo implements Runnable {
    private File file;
    private ImageEventType taskType;
    public TaskAnalyzeImageInfo(File file, ImageEventType taskType) {
        this.file = file;
        this.taskType = taskType;
    }

    @Override
    public void run() {
        //执行的逻辑
        ImageInfo info = new ImageInfo();
        info.setPath(file.getAbsolutePath());
        info.setName(FileUtil.getNameWithoutExtension(file));
        //已经转小写了,所以后面不用管大小写
        info.setFormat(FileUtil.getFileExtension(file));
        info.setFileLen(StringUtil.formatFileSize(file.length()));
        boolean needParseImageSize = ToolSettingsUtil.getInstance().getParseImageSize();
        boolean needPreviewImage = ToolSettingsUtil.getInstance().getGeneratePreviewImg();
        if (needParseImageSize || needPreviewImage) {
            //读取图片大小
            ImageSize imageSize = ImageUtil.computeImageSize(file);
            int width = imageSize.getWidth();
            int height = imageSize.getHeight();
            info.setWidth(width);
            info.setHeight(height);
            if (width > 0 || height > 0) {
                if (needPreviewImage) {
                    //缓存一张小的缩略图
                    String cacheUrl = ImageUtil.writeCacheImage(width, height, info.getFormat(), info.getPath());
                    info.setCacheUrl(cacheUrl);
                }
            }

        }
        ImageEvent event = new ImageEvent();
        event.setTaskType(taskType);
        event.setImageInfo(info);
        EventBusUtil.getDefault().post(event);
    }
}