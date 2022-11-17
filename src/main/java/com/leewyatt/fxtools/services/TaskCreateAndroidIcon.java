package com.leewyatt.fxtools.services;

import com.leewyatt.fxtools.event.AppIconCreatedEvent;
import com.leewyatt.fxtools.event.EventBusUtil;
import com.leewyatt.fxtools.utils.ImageUtil;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;

/**
 * @author LeeWyatt
 */
public class TaskCreateAndroidIcon implements Runnable{
    private Path path;
    private BufferedImage awtImage;
    private String fileName;

    public TaskCreateAndroidIcon(Path path, BufferedImage awtImage, String fileName) {
        this.path = path;
        this.awtImage = awtImage;
        this.fileName = fileName;
    }

    @Override
    public void run() {
        if (fileName.isEmpty()) {
            fileName = "ic_launcher";
        }
        if (!fileName.toLowerCase().endsWith(".png")) {
            fileName = fileName + ".png";
        }
        try {
            Path androidPath = path.resolve("android");
            Files.createDirectories(androidPath);
            Path playstorePath = androidPath.resolve("playstore.png");
            ImageUtil.writeScaleImage2File(playstorePath.toFile(), awtImage, 512);
            saveAndroidIcon(androidPath, "mipmap-ldpi", fileName, 36);
            saveAndroidIcon(androidPath, "mipmap-mdpi", fileName, 48);
            saveAndroidIcon(androidPath, "mipmap-hdpi", fileName, 72);
            saveAndroidIcon(androidPath, "mipmap-xhdpi", fileName, 96);
            saveAndroidIcon(androidPath, "mipmap-xxhdpi", fileName, 144);
            saveAndroidIcon(androidPath, "mipmap-xxxhdpi", fileName, 192);
        } catch (IOException e) {
            Logger logger = Logger.getLogger("com.leewyatt.fxtools.services.TaskCreateAndroidIcon");
            logger.severe("IOException: createAndroidIcon failed.\t"+e);
            throw new RuntimeException(e);
        }
        EventBusUtil.getDefault().post(new AppIconCreatedEvent());

    }
    private void saveAndroidIcon(Path androidPath, String other, String fileName, int size) throws IOException {
        Path mdipPath = androidPath.resolve(other);
        Files.createDirectories(mdipPath);
        Path mdpiIconPath = mdipPath.resolve(fileName);
        ImageUtil.writeScaleImage2File(mdpiIconPath.toFile(), awtImage, size);
    }
}
