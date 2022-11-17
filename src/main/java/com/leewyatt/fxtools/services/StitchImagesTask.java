package com.leewyatt.fxtools.services;

import com.leewyatt.fxtools.model.ImageInfo;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import net.coobird.thumbnailator.Thumbnails;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Logger;

/**
 * @author LeeWyatt
 */
public class StitchImagesTask extends Task<Void> {

    private ObservableList<ImageInfo> imageInfos;
    private File destFile;
    private double scale;
    private int margin;
    private int gap;
    private int row;
    private int col;

    public StitchImagesTask(ObservableList<ImageInfo> imageInfos, File destFile, double scale, int margin, int gap, int row, int col) {
        this.imageInfos = imageInfos;
        this.destFile = destFile;
        this.scale = scale;
        this.margin = margin;
        this.gap = gap;
        this.row = row;
        this.col = col;
    }

    @Override
    protected Void call() throws Exception {
        int size = imageInfos.size();

        int maxW = 0;
        int maxH = 0;
        for (ImageInfo imageInfo : imageInfos) {
            if (imageInfo.getWidth() > maxW) {
                maxW = imageInfo.getWidth();
            }
            if (imageInfo.getHeight() > maxH) {
                maxH = imageInfo.getHeight();
            }
        }
        maxW = (int) (maxW * scale);
        maxH = (int) (maxH * scale);
        int totalWidth = col * maxW + (col - 1) * gap + margin * 2;
        int totalHeight = row * maxH + (row - 1) * gap + margin * 2;
        BufferedImage result = new BufferedImage(totalWidth, totalHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics graphics = result.getGraphics();
        for (int i = 0; i < size; i++) {
            BufferedImage bfImg = readAsBufferedImage(imageInfos.get(i).getPath(),scale);
            graphics.drawImage(bfImg, (i % col) * maxW + margin + (i % col) * gap, (i / col) * maxH + margin + (i / col) * gap, null);
            updateProgress(i, size);
        }
        graphics.dispose();
        try {
            OutputStream out = new FileOutputStream(destFile);
            ImageIO.write(result, "png", out);
            out.close();
        } catch (IOException e) {
            Logger logger =  Logger.getLogger("com.leewyatt.fxtools.services.StitchImagesTask");
            logger.severe("IOException: StitchImages failed.\t"+e);
            e.printStackTrace();
        }
        return null;
    }

    private BufferedImage readAsBufferedImage(String path,double scale) {
        try {
            return Thumbnails.of(path)
                    .scale(scale)
                    .asBufferedImage();
        } catch (IOException e) {
            Logger logger =  Logger.getLogger("com.leewyatt.fxtools.services.StitchImagesTask");
            logger.severe("IOException: StitchImagesTask readAsBufferedImage failed.\t"+e);
            throw new RuntimeException(e);
        }
    }
}
