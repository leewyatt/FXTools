package com.leewyatt.fxtools.services;

import com.leewyatt.fxtools.event.AppIconCreatedEvent;
import com.leewyatt.fxtools.event.EventBusUtil;
import com.leewyatt.fxtools.uicontroller.AppIconController;
import com.leewyatt.fxtools.utils.ImageUtil;
import net.sf.image4j.codec.ico.ICOEncoder;

import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * @author LeeWyatt
 */
public class TaskCreateWinIcon implements Runnable{
    private Path path;
    private BufferedImage awtImage;
    private String selectedItem;
    private boolean winPngsSelected;
    private boolean winIconSelected;

    public TaskCreateWinIcon(Path path, BufferedImage awtImage, String selectedItem, boolean winPngsSelected, boolean winIconSelected) {
        this.path = path;
        this.awtImage = awtImage;
        this.selectedItem = selectedItem;
        this.winPngsSelected = winPngsSelected;
        this.winIconSelected = winIconSelected;
    }

    @Override
    public void run() {
        try {
            Path winPath = path.resolve("windows");
            Files.createDirectories(winPath);
            if (winPngsSelected) {
                Path pngsPath = winPath.resolve("pngs");
                Files.createDirectories(pngsPath);
                if (AppIconController.WIN_10.equals(selectedItem)) {
                    saveWinPngs(new int[]{16, 48, 96, 256, 512}, pngsPath);
                } else if (AppIconController.WIN_VISTA.equals(selectedItem)) {
                    saveWinPngs(new int[]{16, 24, 32, 48, 64, 128, 256}, pngsPath);
                } else {
                    saveWinPngs(new int[]{16, 24, 32, 48, 64, 128}, pngsPath);
                }
            }
            if (winIconSelected) {
                if (AppIconController.WIN_10.equals(selectedItem)) {
                    saveWinIcon(new int[]{16, 48, 96, 256, 512}, winPath);
                } else if (AppIconController.WIN_VISTA.equals(selectedItem)) {
                    saveWinIcon(new int[]{16, 24, 32, 48, 64, 128, 256}, winPath);
                } else {
                    saveWinIcon(new int[]{16, 24, 32, 48, 64, 128}, winPath);
                }
            }
        } catch (IOException e) {
            Logger logger = Logger.getLogger("com.leewyatt.fxtools.services.TaskCreateWinIcon");
            logger.severe("IOException: createWinIcon failed.\t"+e);
            throw new RuntimeException(e);
        }
        EventBusUtil.getDefault().post(new AppIconCreatedEvent());
    }
    private void saveWinIcon(int[] winSizeAry, Path winPath) throws IOException {
        ArrayList<BufferedImage> icons = new ArrayList<>();
        for (int size : winSizeAry) {
            icons.add(ImageUtil.resizeImageBySize(awtImage, size));
        }
        Path resolve = winPath.resolve("icon.ico");
        FileOutputStream fileOutputStream = new FileOutputStream(resolve.toFile());
        ICOEncoder.write(icons, fileOutputStream);
        fileOutputStream.close();
    }

    private void saveWinPngs(int[] winSizeAry, Path winPath) throws IOException {
        for (int size : winSizeAry) {
            Path winIconPath = winPath.resolve(size + "x" + size + ".png");
            ImageUtil.writeScaleImage2File(winIconPath.toFile(), awtImage, size);
        }
    }

}
