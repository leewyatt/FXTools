package com.leewyatt.fxtools.services;

import com.leewyatt.fxtools.event.EventBusUtil;
import com.leewyatt.fxtools.event.ImageEvent;
import com.leewyatt.fxtools.event.ImageEventType;
import com.leewyatt.fxtools.model.ImageInfo;
import com.leewyatt.fxtools.utils.ImageUtil;

import java.nio.file.Path;

/**
 * @author LeeWyatt
 */
public class TaskCutImage implements Runnable {
    private double sourceScale;
    private Path javaDirPath;
    private Path iosDirPath;
    private Path[] androidDirPaths;
    private ImageInfo imageInfo;

    public TaskCutImage(double sourceScale, Path javaDirPath, Path iosDirPath, Path[] androidDirPaths, ImageInfo imageInfo) {
        this.sourceScale = sourceScale;
        this.javaDirPath = javaDirPath;
        this.iosDirPath = iosDirPath;
        this.androidDirPaths = androidDirPaths;
        this.imageInfo = imageInfo;
    }

    @Override
    public void run() {
        //  ldpi 0.75
        double[] dpiScale = {0.75, 1.0, 1.5, 2.0, 3.0, 4.0};
        String format = imageInfo.getFormat();
        boolean success = true;
        if (javaDirPath != null) {
            //webp 或者  //想要保持格式不变的话, 那么就需要在writeBMP里添加 bmp 在webp里添加webp 还是全部转png吧
            if (ImageUtil.FORMAT_SVG.equals(imageInfo.getFormat()) || ImageUtil.FORMAT_WEBP.equals(imageInfo.getFormat())) {
                format = ImageUtil.FORMAT_PNG;
            }
            for (int j = 1; j <= 2; j++) {
                Path fxPath = javaDirPath.resolve(imageInfo.getName() + (j == 1 ? "." : "@" + j + "x.") + format);
                success = ImageUtil.writeImage2File(imageInfo.getPath(), fxPath.toFile(), 1.0F, j / sourceScale);
            }
        }
        if (!success) {
            imageInfo.setWidth(-1);
            imageInfo.setHeight(-1);
            postCompleted();
            return;
        }
        //TODO 如果java被选择了. 这里部分的图片,可以直接复制
        if (iosDirPath != null) {
            if (ImageUtil.FORMAT_SVG.equals(imageInfo.getFormat()) || ImageUtil.FORMAT_WEBP.equals(imageInfo.getFormat())) {
                format = ImageUtil.FORMAT_PNG;
            }
            for (int j = 1; j <= 3; j++) {
                Path iosPath = iosDirPath.resolve(imageInfo.getName() + (j == 1 ? "." : "@" + j + "x.") + format);
                success = ImageUtil.writeImage2File(imageInfo.getPath(), iosPath.toFile(), 1.0F, j / sourceScale);
            }
        }
        if (!success) {
            imageInfo.setWidth(-1);
            imageInfo.setHeight(-1);
            postCompleted();
            return;
        }

        if (androidDirPaths != null) {
            if (ImageUtil.FORMAT_SVG.equals(imageInfo.getFormat()) || ImageUtil.FORMAT_WEBP.equals(imageInfo.getFormat())) {
                format = ImageUtil.FORMAT_PNG;
            }
            for (int j = 0; j < dpiScale.length; j++) {
                Path destPath = androidDirPaths[j].resolve(imageInfo.getName() + "." + format);
                double scale = dpiScale[j] / sourceScale;
                success = ImageUtil.writeImage2File(imageInfo.getPath(), destPath.toFile(), 1.0F, scale);
            }
        }
        if (!success) {
            imageInfo.setWidth(-1);
            imageInfo.setHeight(-1);
            postCompleted();
            return;
        }
        postCompleted();
    }

    private void postCompleted() {
        ImageEvent event = new ImageEvent();
        event.setTaskType(ImageEventType.CUTTER_EXPORT);
        event.setImageInfo(imageInfo);
        EventBusUtil.getDefault().post(event);
    }
}
