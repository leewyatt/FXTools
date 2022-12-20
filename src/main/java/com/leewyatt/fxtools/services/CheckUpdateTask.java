package com.leewyatt.fxtools.services;

import com.google.gson.Gson;
import com.leewyatt.fxtools.model.FXToolsVersion;
import com.leewyatt.fxtools.utils.Constants;
import com.leewyatt.fxtools.utils.OSUtil;
import com.leewyatt.fxtools.utils.ResourcesUtil;
import com.leewyatt.fxtools.utils.ToolSettingsUtil;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.logging.Logger;

import static com.leewyatt.fxtools.utils.ResourcesUtil.message;

/**
 * @author LeeWyatt
 *
 */
public class CheckUpdateTask extends Task<Void> {
    private String newVer="";
    @Override
    protected Void call() throws Exception {
        boolean isNewVerExist;
        try {
            isNewVerExist = checkNewVersion();
        } catch (Exception exception) {
            isNewVerExist = false;
            Logger logger = Logger.getLogger("com.leewyatt.fxtools.services.CheckUpdateTask");
            logger.severe("Exception: Check for update service failed.");
        }
        if (isNewVerExist) {
            Platform.runLater(() -> {
                String skinStyle = ToolSettingsUtil.getInstance().getSkin();
                boolean isDark = "dark".equalsIgnoreCase(skinStyle);
                SVGPath svgPath = new SVGPath();
                svgPath.setContent("M28.2,25.9c-3,3.8-7.5,6.1-12.3,6.1C8.2,32,1.7,26.3,0,18.4l3.2-0.8c1.3,6.3,6.6,10.9,12.7,10.9c4,0,7.6-1.9,10.1-5.1  l-3.5-4H32v10.8C32,30.2,28.2,25.9,28.2,25.9z M3.8,6.1C6.8,2.3,11.3,0,16.1,0C23.8,0,30.3,5.7,32,13.6l-3.2,0.8  C27.5,8.1,22.2,3.5,16.1,3.5c-4,0-7.6,1.9-10.1,5.1l3.5,4H0V1.8C0,1.8,3.8,6.1,3.8,6.1z");
                svgPath.setScaleX(0.6);
                svgPath.setScaleY(0.6);
                svgPath.setFill(isDark ? Color.WHITE : Color.web("#333333"));
                BorderPane contentPane = new BorderPane();
                contentPane.setLeft(new Label(message("updateContent")+" "+newVer, svgPath));
                Button updateBtn = new Button(message("update"));
                updateBtn.getStyleClass().add("update-btn");
                updateBtn.getStylesheets().add(ResourcesUtil.cssExternalForm("notification-" + skinStyle.toLowerCase() + ".css"));
                updateBtn.setOnAction(event -> {
                    if (OSUtil.isEnglish()) {
                        OSUtil.showDoc(Constants.LAST_VERSION_GITHUB);
                    } else {
                        OSUtil.showDoc(Constants.LAST_VERSION_GITEE);
                    }
                    event.consume();
                });
                contentPane.setRight(updateBtn);
                Notifications notifications = Notifications.create()
                        .title(message("updateTitle"))
                        .graphic(contentPane)
                        .hideAfter(Duration.seconds(10))
                        .position(Pos.BOTTOM_RIGHT);
                if (isDark) {
                    notifications.darkStyle();
                }
                notifications.show();
            });
        }
        return null;
    }

    private boolean checkNewVersion() throws Exception{
        String last = "https://thetbw-hk.cos.thetbw.xyz/fxtools/latest";
        InputStream in = null;
        InputStreamReader reader = null;
        try {
            URL url = new URL(last);
            in = url.openStream();
            reader = new InputStreamReader(in);
            FXToolsVersion version = new Gson().fromJson(reader, FXToolsVersion.class);
            in.close();
            reader.close();
            if (version != null) {
                newVer = version.getVer();
                String currentVer = message("toolsVersion");
                return needUpdate(currentVer, newVer);
            } else {
                throw new IOException();
            }
        } catch (Exception exception) {
            throw new Exception();
        } finally {
            if (in != null) {
                in.close();
            }
            if (reader != null) {
                reader.close();
            }
        }
    }

    /**
     * 只要版本号和最新的版本号不同,即可认为有新版本
     */
    private boolean needUpdate(String currentVer, String newVersion) {
        return !currentVer.equalsIgnoreCase(newVersion);
    }
}
