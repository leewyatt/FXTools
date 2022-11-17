package com.leewyatt.fxtools.services;

import com.leewyatt.fxtools.event.EventBusUtil;
import com.leewyatt.fxtools.event.ImageEvent;
import com.leewyatt.fxtools.event.ImageEventType;
import com.leewyatt.fxtools.model.ImageInfo;
import com.leewyatt.fxtools.utils.ImageUtil;

import java.io.File;

/**
 * @author LeeWyatt
 */
public class TaskFormatConvert implements Runnable {

    private ImageInfo imageInfo;
    private String lastExportDirPath;
    private String destFormat;
    private float jpgQuality;
    private double scale;

    public TaskFormatConvert(ImageInfo imageInfo, String lastExportDirPath, String destFormat, float jpgQuality, double scale) {
        this.imageInfo = imageInfo;
        this.lastExportDirPath = lastExportDirPath;
        this.destFormat = destFormat;
        this.jpgQuality = jpgQuality;
        this.scale = scale;
    }

    @Override
    public void run() {
        //真实的输出路径
        File realExportDir;
        int x = 0;
        do {
            realExportDir = new File(lastExportDirPath + File.separator + imageInfo.getName() + (x == 0 ? "" : "(" + x + ")") + "." + destFormat);
            x++;
        } while (realExportDir.exists());
        //解析的文件可能出错
        boolean success = ImageUtil.writeImage2File(imageInfo.getPath(), realExportDir, jpgQuality, scale);
        if (!success) {
            imageInfo.setWidth(-1);
            imageInfo.setHeight(-1);
        }
        ImageEvent event = new ImageEvent();
        event.setTaskType(ImageEventType.FORMAT_EXPORT);
        event.setImageInfo(imageInfo);
        EventBusUtil.getDefault().post(event);
    }

}
