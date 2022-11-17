package com.leewyatt.fxtools.services;

import com.leewyatt.fxtools.model.ImageInfo;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.io.File;

/**
 * @author LeeWyatt
 */
public class StitchImagesService extends Service<Void> {

    private ObservableList<ImageInfo> imageInfos;
    private File destFile;
    private double scale;
    private int margin;
    private int gap;
    private int row;
    private int col;

    public StitchImagesService(ObservableList<ImageInfo> imageInfos, File destFile, double scale, int margin, int gap, int row, int col) {
        this.imageInfos = imageInfos;
        this.destFile = destFile;
        this.scale = scale;
        this.margin = margin;
        this.gap = gap;
        this.row = row;
        this.col = col;
    }

    @Override
    protected Task<Void> createTask() {
        return new StitchImagesTask(imageInfos,destFile,scale, margin, gap, row, col);
    }
}
