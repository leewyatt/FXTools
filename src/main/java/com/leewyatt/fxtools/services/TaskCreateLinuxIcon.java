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
public class TaskCreateLinuxIcon implements Runnable{
    private Path path;
    private BufferedImage awtImage;
    private boolean allSizeSelected;

    public TaskCreateLinuxIcon(Path path, BufferedImage awtImage, boolean allSizeSelected) {
        this.path = path;
        this.awtImage = awtImage;
        this.allSizeSelected = allSizeSelected;
    }

    @Override
    public void run() {
        try {
            Path linuxPath = path.resolve("linux");
            Files.createDirectories(linuxPath);
            int[] linuxIconSize = {16, 32, 48, 64, 128, 256};//512
            if (allSizeSelected) {
                for (int size : linuxIconSize) {
                    Path iconPath = linuxPath.resolve(size + "x" + size + ".png");
                    ImageUtil.writeScaleImage2File(iconPath.toFile(), awtImage, size);
                }
            }
            Path icon512Path = linuxPath.resolve("512x512.png");
            ImageUtil.writeScaleImage2File(icon512Path.toFile(), awtImage, 512);
        } catch (IOException e) {
            Logger logger = Logger.getLogger("com.leewyatt.fxtools.services.TaskCreateLinuxIcon");
            logger.severe("IOException: createLinuxIcon failed.\t"+e);
            throw new RuntimeException(e);
        }
        EventBusUtil.getDefault().post(new AppIconCreatedEvent());
    }
}
