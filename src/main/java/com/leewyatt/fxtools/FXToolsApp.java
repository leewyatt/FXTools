package com.leewyatt.fxtools;

import com.google.common.eventbus.Subscribe;
import com.leewyatt.fxtools.event.AnimFinishedEvent;
import com.leewyatt.fxtools.event.EventBusUtil;
import com.leewyatt.fxtools.services.CheckUpdateService;
import com.leewyatt.fxtools.ui.DragScene;
import com.leewyatt.fxtools.ui.InitialLoadingPane;
import com.leewyatt.fxtools.uicontroller.MainPaneController;
import com.leewyatt.fxtools.utils.FileUtil;
import com.leewyatt.fxtools.utils.ResourcesUtil;
import com.leewyatt.fxtools.utils.ToolSettingsUtil;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.PerspectiveCamera;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * @author LeeWyatt
 */

public class FXToolsApp extends Application {
    public static Stage mainStage;
    public static Font BASIC_FONT;
    public static HostServices hostServices;

    private boolean animationFinished;
    private Parent parent;
    private DragScene scene;
    private boolean rootChanged;
    private MainPaneController controller;

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setAlwaysOnTop(true);
        //Used for testing, set to English text
        //Locale.setDefault(Locale.ENGLISH);
        EventBusUtil.getDefault().register(this);
        mainStage = primaryStage;
        hostServices = getHostServices();
        ToolSettingsUtil settingsUtil = ToolSettingsUtil.getInstance();
        String skinStyle = settingsUtil.getSkin();
        //boot animation
        InitialLoadingPane initialLoadingPane = new InitialLoadingPane(skinStyle);
        scene = new DragScene(initialLoadingPane, primaryStage);
        scene.getStylesheets().add(ResourcesUtil.cssExternalForm("main-stage-" + skinStyle + ".css"));
        loadMainPane();
        scene.setFill(Color.TRANSPARENT);
        scene.setCamera(new PerspectiveCamera());
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setScene(scene);
        primaryStage.setTitle("FXTools");
        primaryStage.getIcons().addAll(ResourcesUtil.getIconImages());
        primaryStage.show();
        //Start the service to check if there is a new version
        if (settingsUtil.getUpdateNotify()) {
            CheckUpdateService checkUpdateService = new CheckUpdateService();
            checkUpdateService.start();
        }
    }

    private void loadMainPane() {
        Platform.runLater(() -> {
            BASIC_FONT = Font.loadFont(ResourcesUtil.fontExternalForm("SourceHanSansCN-Regular.otf"), 12);
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/fxml/main-pane.fxml"));
            fxmlLoader.setResources(ResourcesUtil.RESOURCE_BUNDLE);
            Parent load = null;
            try {
                load = fxmlLoader.load();
            } catch (IOException e) {
                Logger logger = Logger.getLogger("com.leewyatt.fxtools.FXToolsApp");
                logger.severe("IOException: Load Main Pane failed.\t" + e);
                throw new RuntimeException();
            }
            controller = fxmlLoader.getController();
            controller.setStage(mainStage);
            EventBusUtil.getDefault().post(load);
        });
    }

    private void resetRoot(Parent parent) {
        Platform.runLater(() -> {
            scene.setRoot(parent);
            scene.setCanDrag();
            scene.getRoot().applyCss();
            scene.getRoot().requestLayout();
            scene.getRoot().layout();
            mainStage.setAlwaysOnTop(ToolSettingsUtil.getInstance().getAlwaysTop());
        });
    }

    @Subscribe
    public void animFinishedHandler(AnimFinishedEvent event) {
        animationFinished = true;
        if (parent != null && !rootChanged) {
            rootChanged = true;
            resetRoot(parent);
        }
    }

    @Subscribe
    public void mainPaneLoadHandler(Parent parent) {
        this.parent = parent;
        if (animationFinished && !rootChanged) {
            rootChanged = true;
            resetRoot(parent);
        }
    }

    public static void main(String[] args) {
        //Create folders to save cache and logs
        FileUtil.getInstance().init();
        //Load log configuration
        InputStream ins = FXToolsApp.class.getClassLoader().getResourceAsStream("logging.properties");
        LogManager logManager = LogManager.getLogManager();
        try {
            logManager.readConfiguration(ins);
            if (ins != null) {
                ins.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (ins != null) {
                try {
                    ins.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        Logger logger = Logger.getLogger("com.leewyatt.fxtools.FXToolsApp");
        logger.info("Fxtools application startup.");
        //starting program
        launch(args);
    }
}
