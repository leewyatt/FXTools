package com.leewyatt.fxtools.services;

import com.leewyatt.fxtools.model.FontInfo;
import com.leewyatt.fxtools.ui.alert.InformationAlert;
import com.leewyatt.fxtools.utils.FileUtil;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.text.Font;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

import static com.leewyatt.fxtools.utils.ResourcesUtil.message;

/**
 * @author LeeWyatt
 */
public class LoadFontTask extends Task<List<FontInfo>> {
    private List<File> files;
    private double fontSize;
    private int loadFailedFontNum;
    private int totalFontNum;

    private  List<FontInfo> fontInfos;

    public LoadFontTask(List<File> files, double fontSize,List<FontInfo> fontInfos) {
        this.files = files;
        this.fontSize = fontSize;
        this.fontInfos = fontInfos;
    }

    @Override
    protected List<FontInfo> call() {
        loadFailedFontNum = 0;
        List<FontInfo> result = new ArrayList<>();

        List<File> fileList = new ArrayList<>();
        for (File f : files) {
            boolean containFlag = false;
            for (FontInfo info : fontInfos) {
                if (Objects.equals(info.getPath(), FileUtil.getUrl(f))) {
                    containFlag = true;
                    break;
                }
            }
            if (!containFlag) {
                fileList.add(f);
            }
        }

        if (fileList.isEmpty()) {
            updateProgress(1, 1);
            Platform.runLater(() ->
                    new InformationAlert(message("alert.fontAlreadyExists")).showAndWait());
        }else {
            totalFontNum = fileList.size();
            for (int i = 0; i < fileList.size(); i++) {
                File file = fileList.get(i);
                if (file != null) {
                    String urlStr = null;
                    try {
                        urlStr = file.toURI().toURL().toExternalForm();
                    } catch (MalformedURLException e) {
                        loadFailedFontNum++;
                        updateProgress(i, totalFontNum);
                        Logger logger = Logger.getLogger("com.leewyatt.fxtools.services.LoadFontTask");
                        logger.warning("MalformedURLException: load Font failed.\t."+e);
                        continue;
                    }
                    Font font = Font.loadFont(urlStr, fontSize);
                    if (font != null) {
                        String fontName = font.getName();
                        String family = font.getFamily();
                        result.add(new FontInfo(family, fontName, urlStr));
                    } else {
                        loadFailedFontNum++;
                    }
                    updateProgress(i, totalFontNum);
                }else {
                    updateProgress(i, totalFontNum);
                }
            }
            //如果部分字体加载失败 ,那么进行提示
            Platform.runLater(()->
                    new InformationAlert(
                            message("alert.loadResult")
                                    +message("alert.fontLoadSucceed")+(totalFontNum-loadFailedFontNum)
                                    +"  "
                                    +message("alert.fontLoadFailed")+  loadFailedFontNum )
                            .showAndWait());
        }
        return result;
    }
}
