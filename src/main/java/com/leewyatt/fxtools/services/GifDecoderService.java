package com.leewyatt.fxtools.services;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

/**
 * @author LeeWyatt
 */
public class GifDecoderService extends Service<Void> {

    private String gifPath;
    private String exportPath;
    private String prefix;
    private String suffix;
    private double scale;

    private boolean isSingleImage;

    public GifDecoderService(String gifPath, String exportPath, String prefix, String suffix, double scale, boolean isSingleImage) {
        this.gifPath = gifPath;
        this.exportPath = exportPath;
        this.prefix = prefix;
        this.suffix = suffix;
        this.scale = scale;
        this.isSingleImage = isSingleImage;
    }

    @Override
    protected Task<Void> createTask() {
        return new GifDecoderTask(gifPath, exportPath, prefix, suffix, scale, isSingleImage);
    }
}
