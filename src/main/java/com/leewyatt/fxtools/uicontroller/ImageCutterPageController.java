package com.leewyatt.fxtools.uicontroller;

import com.leewyatt.fxtools.event.EventBusUtil;
import com.leewyatt.fxtools.event.ImageEventType;
import com.leewyatt.fxtools.model.ImageInfo;
import com.leewyatt.fxtools.services.TaskCutImage;
import com.leewyatt.fxtools.ui.ImageListViewTips;
import com.leewyatt.fxtools.ui.alert.InformationAlert;
import com.leewyatt.fxtools.ui.cells.ImageListCell;
import com.leewyatt.fxtools.utils.ToolSettingsUtil;
import com.leewyatt.rxcontrols.pane.RXCarouselPane;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import static com.leewyatt.fxtools.utils.ResourcesUtil.message;

/**
 * @author LeeWyatt
 */
public class ImageCutterPageController extends ImageTaskController {

    @FXML
    private Pane centerPane;

    @FXML
    private StackPane deleteImagBtn;

    @FXML
    private RadioButton source1xBox;

    @FXML
    private RadioButton source2xBox;

    @FXML
    private RadioButton source3XBox;

    @FXML
    private RadioButton source4XBox;

    @FXML
    private CheckBox iosExportBox;

    @FXML
    private CheckBox javafxExportBox;

    @FXML
    private CheckBox androidExportBox;

    @FXML
    private ToggleGroup sourceImageGroup;



    public ImageCutterPageController() {
        importEventType = ImageEventType.CUTTER_IMPORT;
        exportEventType = ImageEventType.CUTTER_EXPORT;
        supportedExtensions = new String[]{"*.webp", "*.svg", "*.png", "*.jpg", "*.bmp", "*.gif", "*.jpeg"};
        EventBusUtil.getDefault().register(this);
    }

    @FXML
    void initialize() {
        if (ToolSettingsUtil.getInstance().getShowImgTips()) {
            imageListView.setPlaceholder(new ImageListViewTips());
        }
        imageListView.setCellFactory(param -> new ImageListCell());
        imageListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        imageListView.getSelectionModel().selectedIndexProperty().addListener((ob, ov, nv) -> {
            deleteImagBtn.setDisable(nv.intValue() == -1);
        });
        imageListView.setOnDragDropped(this::onDragDroppedImage);
        imageListView.setOnDragOver(this::onDragOverImage);
        imageListView.setEditable(true);
    }

    @FXML
    void onCutImageAction(ActionEvent event) {
        if (!javafxExportBox.isSelected() && !iosExportBox.isSelected() && !androidExportBox.isSelected()) {
            new InformationAlert(message("cutterPage.choosePlatform")).showAndWait();
            return;
        }
        ObservableList<ImageInfo> items = imageListView.getItems();
        taskSize = items.size();
        File realExportDir = chooseExportDir(taskSize);
        if (realExportDir == null) {
            return;
        }

        Path path = realExportDir.toPath();
        resultPath = realExportDir.getPath();
        lastExportDirPath = realExportDir.getParent();
        RadioButton radioButton = (RadioButton) sourceImageGroup.getSelectedToggle();
        double sourceScale = Double.parseDouble(radioButton.getText().substring(0, 1));
        Path javaDirPath = null, iosDirPath = null, androidDirPath = null,
                ldpiPath = null,
                mdpiPath = null,
                hdpiPath = null, xhdpiPath = null, xxhdpiPath = null, xxxhdpiPath = null;
        try {
            if (javafxExportBox.isSelected()) {
                javaDirPath = path.resolve("javafx");
                Files.createDirectories(javaDirPath);
            }
            if (iosExportBox.isSelected()) {
                iosDirPath = path.resolve("ios");
                Files.createDirectories(iosDirPath);
            }
            if (androidExportBox.isSelected()) {
                androidDirPath = path.resolve("android");
                Files.createDirectories(androidDirPath);
                ldpiPath = androidDirPath.resolve("drawable-ldpi");
                Files.createDirectories(ldpiPath);
                mdpiPath = androidDirPath.resolve("drawable-mdpi");
                Files.createDirectories(mdpiPath);
                hdpiPath = androidDirPath.resolve("drawable-hdpi");
                Files.createDirectories(hdpiPath);
                xhdpiPath = androidDirPath.resolve("drawable-xhdpi");
                Files.createDirectories(xhdpiPath);
                xxhdpiPath = androidDirPath.resolve("drawable-xxhdpi");
                Files.createDirectories(xxhdpiPath);
                xxxhdpiPath = androidDirPath.resolve("drawable-xxxhdpi");
                Files.createDirectories(xxxhdpiPath);
            }
        } catch (IOException e) {
            Logger logger = Logger.getLogger("com.leewyatt.fxtools.uicontroller.ImageCutterPageController");
            logger.severe("IOException: "+message("markDirectoryFailed")+"\t"+e);
            new InformationAlert(message("markDirectoryFailed")).showAndWait();
            return;
        }
        isExportFileOpened = false;
        completedSize = 0;
        maskerPane.setProgress(0);
        maskerPane.setVisible(true);
        int threadNum = ToolSettingsUtil.getInstance().getThreadNum();
        ExecutorService exec = Executors.newFixedThreadPool(threadNum);
        for (int i = 0; i < taskSize; i++) {
            exec.submit(new TaskCutImage(sourceScale, javaDirPath,
                    iosDirPath, androidExportBox.isSelected() ? new Path[]{ldpiPath,mdpiPath, hdpiPath, xhdpiPath, xxhdpiPath, xxxhdpiPath} : null,
                    items.get(i)));
        }
        exec.shutdown();
    }

}