package com.leewyatt.fxtools.uicontroller;

import com.leewyatt.fxtools.event.EventBusUtil;
import com.leewyatt.fxtools.event.ImageEventType;
import com.leewyatt.fxtools.model.ImageInfo;
import com.leewyatt.fxtools.services.TaskFormatConvert;
import com.leewyatt.fxtools.ui.ImageListViewTips;
import com.leewyatt.fxtools.ui.alert.InformationAlert;
import com.leewyatt.fxtools.ui.cells.ImageListCell;
import com.leewyatt.fxtools.ui.paintpicker.DoubleTextField;
import com.leewyatt.fxtools.utils.ImageUtil;
import com.leewyatt.fxtools.utils.ToolSettingsUtil;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.leewyatt.fxtools.utils.ResourcesUtil.message;

/**
 * @author LeeWyatt
 */
public class ImageFormatPageController extends ImageTaskController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private ComboBox<String> qualityComboBox;

    @FXML
    private ComboBox<String> convertComboBox;

    @FXML
    private StackPane deleteImagBtn;

    @FXML
    private Pane centerPane;

    private DoubleTextField scaleField;

    public ImageFormatPageController() {
        EventBusUtil.getDefault().register(this);
        importEventType = ImageEventType.FORMAT_IMPORT;
        exportEventType = ImageEventType.FORMAT_EXPORT;
        supportedExtensions = new String[]{"*.webp", "*.svg", "*.png", "*.jpg", "*.bmp", "*.gif", "*.jpeg"};
    }

    @FXML
    void initialize() {
        maskerPane.setText(message("message.wait"));
        scaleField = new DoubleTextField();
        scaleField.setText("1.0");
        scaleField.setPrefSize(158, 31);
        scaleField.setLayoutX(100);
        scaleField.setLayoutY(494);
        int size = centerPane.getChildren().size();
        centerPane.getChildren().add(size - 3, scaleField);
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

        convertComboBox.getItems().addAll(ImageUtil.FORMAT_PNG, ImageUtil.FORMAT_JPG, ImageUtil.FORMAT_GIF, ImageUtil.FORMAT_BMP, ImageUtil.FORMAT_ICO);
        convertComboBox.getSelectionModel().select(0);
        convertComboBox.getSelectionModel().selectedItemProperty().addListener((ob, ov, nv) ->
                qualityComboBox.setDisable(!ImageUtil.FORMAT_JPG.equals(nv))
        );
        qualityComboBox.getItems().addAll(message("imagePage.format.quality1"), message("imagePage.format.quality2"), message("imagePage.format.quality3"));
        qualityComboBox.getSelectionModel().select(0);
    }

    @FXML
    void onConvertImageFormatAction(ActionEvent event) throws IOException {
        ObservableList<ImageInfo> items = imageListView.getItems();
        double scale = scaleField.getValue();
        if (scale <= 0) {
            new InformationAlert(message("alert.scaleGreaterThan0")).showAndWait();
            return;
        }
        taskSize = items.size();
        File realExportDir = chooseExportDir(taskSize);
        if (realExportDir == null) {
            return;
        }
        resultPath = realExportDir.getPath();
        lastExportDirPath = realExportDir.getParent();
        String destFormat = convertComboBox.getSelectionModel().getSelectedItem();
        float jpgQuality = getJPGQuality(qualityComboBox.getSelectionModel().getSelectedItem());
        isExportFileOpened = false;
        completedSize = 0;
        maskerPane.setProgress(0);
        maskerPane.setVisible(true);
        int threadNum = ToolSettingsUtil.getInstance().getThreadNum();
        ExecutorService exec = Executors.newFixedThreadPool(threadNum);
        for (int i = 0; i < taskSize; i++) {
            exec.submit(new TaskFormatConvert(items.get(i), resultPath, destFormat, jpgQuality, scale));
        }
        exec.shutdown();
    }

    private float getJPGQuality(String quality) {
        if (quality.equalsIgnoreCase(message("imagePage.format.quality1"))) {
            return ImageUtil.QUALITY_HIGHEST;
        } else if (quality.equalsIgnoreCase(message("imagePage.format.quality2"))) {
            return ImageUtil.QUALITY_HIGHER;
        } else {
            return ImageUtil.QUALITY_MEDIA;
        }
    }
    //@Subscribe
    //public void skinChangedHandler(SkinChangedEvent event) {
    //    String skinStyle = ToolSettingsUtil.getInstance().getSkin();
    //    formatPage.getStylesheets().setAll(getClass().getResource("/css/picker-stage-"+ skinStyle +".css").toExternalForm());
    //}
}
