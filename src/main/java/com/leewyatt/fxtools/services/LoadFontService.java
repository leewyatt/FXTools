package com.leewyatt.fxtools.services;

import com.leewyatt.fxtools.model.FontInfo;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.io.File;
import java.util.List;

/**
 * @author LeeWyatt
 */
public class LoadFontService extends Service<List<FontInfo>> {
    private List<File> files;
    private double fontSize;
   private List<FontInfo> fontInfos;
    public LoadFontService(List<File> files,double fontSize,List<FontInfo> fontInfos) {
        this.files = files;
        this.fontSize = fontSize;
        this.fontInfos = fontInfos;
    }

    @Override
    protected Task<List<FontInfo>> createTask() {
        return new LoadFontTask(files,fontSize,fontInfos);
    }
}
