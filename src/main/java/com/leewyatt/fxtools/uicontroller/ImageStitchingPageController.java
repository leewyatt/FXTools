package com.leewyatt.fxtools.uicontroller;

import com.leewyatt.fxtools.event.EventBusUtil;
import com.leewyatt.fxtools.event.ImageEventType;
import com.leewyatt.fxtools.model.ImageInfo;
import com.leewyatt.fxtools.services.StitchImagesService;
import com.leewyatt.fxtools.ui.ImageListViewTips;
import com.leewyatt.fxtools.ui.alert.InformationAlert;
import com.leewyatt.fxtools.ui.cells.ImageListCell;
import com.leewyatt.fxtools.ui.paintpicker.DoubleTextField;
import com.leewyatt.fxtools.ui.paintpicker.IntegerTextField;
import com.leewyatt.fxtools.utils.AlphanumComparator;
import com.leewyatt.fxtools.utils.ToolSettingsUtil;
import com.leewyatt.fxtools.utils.OSUtil;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static com.leewyatt.fxtools.utils.ResourcesUtil.message;

/**
 * @author LeeWyatt
 */
public class ImageStitchingPageController extends ImageTaskController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private GridPane gridPane;

    @FXML
    private Pane centerPane;

    @FXML
    private StackPane deleteImagBtn;

    @FXML
    private StackPane sortByNameBtn;

    @FXML
    private StackPane moveUpBtn;

    @FXML
    private StackPane moveDownBtn;
    @FXML
    private StackPane doublePane;

    @FXML
    private Label amountLabel;
    @FXML
    private Label widthLabel;
    @FXML
    private Label heightLabel;

    private IntegerTextField rowNumField;

    private IntegerTextField gapField;

    private IntegerTextField marginField;

    private DoubleTextField scaleField;

    private boolean flag;

    public ImageStitchingPageController() {
        EventBusUtil.getDefault().register(this);
        importEventType = ImageEventType.STITCHING_IMPORT;
        exportEventType = ImageEventType.CUTTER_EXPORT;
        supportedExtensions = new String[]{"*.png", "*.jpg", "*.bmp", "*.gif", "*.jpeg"};
    }

    @FXML
    void sortByNameAction(MouseEvent event) {
        int size = imageListView.getItems().size();
        flag = !flag;
        sortByNameBtn.getStyleClass().add(flag ? "sort-btn-flip" : "sort-btn");
        sortByNameBtn.getStyleClass().remove(flag ? "sort-btn" : "sort-btn-flip");
        if (size <= 1) {
            return;
        }
        List<ImageInfo> imageInfos = imageListView.getItems().stream().sorted((a, b) -> (flag ? -1 : 1) * new AlphanumComparator().compare(a.getName(), b.getName())).collect(Collectors.toList());
        imageListView.getItems().setAll(imageInfos);

    }

    @FXML
    void moveUpAction(MouseEvent event) {
        ImageInfo item = imageListView.getSelectionModel().getSelectedItem();
        int index = imageListView.getItems().indexOf(item);
        if (index < 1) { //0 或者 -1
            return;
        }
        boolean remove = imageListView.getItems().remove(item);
        if (remove) {
            imageListView.getItems().add(index - 1, item);
            imageListView.getSelectionModel().select(index - 1);
        }
    }

    @FXML
    void moveDownAction(MouseEvent event) {
        ImageInfo item = imageListView.getSelectionModel().getSelectedItem();
        int index = imageListView.getItems().indexOf(item);
        if (index == -1 || index == imageListView.getItems().size() - 1) { //最后一个元素 或者 -1
            return;
        }
        boolean remove = imageListView.getItems().remove(item);
        if (remove) {
            imageListView.getItems().add(index + 1, item);
            imageListView.getSelectionModel().select(index + 1);
        }
    }

    @FXML
    void onStitchingImageAction(ActionEvent event) {
        ObservableList<ImageInfo> items = imageListView.getItems();
        int size = items.size();
        int row = readIntFromField(rowNumField);
        int col = readIntFromField(colNumField);
        double scale = scaleField.getValue();
        int margin = readIntFromField(marginField);
        int gap = readIntFromField(gapField);
        if (size < 1) {
            new InformationAlert(message("alert.addImage")).showAndWait();
            return;
        }
        if (row <= 0 || col <= 0) {
            new InformationAlert(message("alert.rowColGreaterThan1")).showAndWait();
            return;
        }
        if (scale <= 0) {
            new InformationAlert(message("alert.scaleGreaterThan0")).showAndWait();
            return;
        }

        int totalWidth = Integer.parseInt(widthLabel.getText());
        int totalHeight = Integer.parseInt(heightLabel.getText());
        if (totalWidth * totalHeight >= 214554200) { //46340*46340 像素
            new InformationAlert(message("alert.imageIsTooBig")).showAndWait();
            return;
        }
        if (totalWidth <= 0 || totalHeight <= 0) { // 图片尺寸小于等于0
            new InformationAlert(message("alert.imageIsTooSmall")).showAndWait();
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save");
        fileChooser.setInitialFileName(items.get(0).getName() + "_stitch");
        if (lastExportDirPath != null) {
            File file = new File(lastExportDirPath);
            if (file.exists() && file.isDirectory()) {
                fileChooser.setInitialDirectory(new File(lastExportDirPath));
            }
        }
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PNG File", "*.png"));
        File file = fileChooser.showSaveDialog(colNumField.getScene().getWindow());
        if (file == null) {
            return;
        }
        isExportFileOpened = false;
        lastExportDirPath = file.getParent();
        resultPath = file.getPath();
        StitchImagesService service = new StitchImagesService(items, file, scale, margin, gap, row, col);
        maskerPane.visibleProperty().unbind();
        maskerPane.progressProperty().unbind();
        maskerPane.visibleProperty().bind(service.runningProperty());
        maskerPane.progressProperty().bind(service.progressProperty());
        service.runningProperty().addListener((ob, ov, nv) -> {
            if (!nv) {
                boolean needOpen = ToolSettingsUtil.getInstance().getTaskCompleteOpenFile();
                if (needOpen) {
                    OSUtil.openAndSelectedFile(resultPath);
                }
            }
        });
        service.start();
    }

    private int readIntFromField(TextField field) {
        String str = field.getText().trim();
        if (str.isEmpty()) {
            return 0;
        }
        return Integer.parseInt(str);
    }

    @FXML
    void initialize() {
        scaleField = new DoubleTextField();
        scaleField.setText("1.0");
        scaleField.textProperty().addListener((ob, ov, nv) -> updateResultSize());
        doublePane.getChildren().add(scaleField);

        rowNumField = new IntegerTextField();
        rowNumField.setText("1");
        rowNumField.setPrefWidth(45);
        gridPane.add(rowNumField, 1, 0);

        colNumField = new IntegerTextField();
        colNumField.setText("1");
        colNumField.setPrefWidth(45);
        gridPane.add(colNumField, 3, 0);

        marginField = new IntegerTextField();
        marginField.setText("0");
        marginField.setPrefWidth(45);
        gridPane.add(marginField, 1, 1);

        gapField = new IntegerTextField();
        gapField.setText("0");
        gapField.setPrefWidth(45);
        gridPane.add(gapField, 3, 1);

        ObservableList<ImageInfo> items = imageListView.getItems();
        items.addListener((ListChangeListener<ImageInfo>) c -> {
            amountLabel.setText(String.valueOf(items.size()));
            updateResultSize();
        });

        rowNumField.textProperty().addListener((ob, ov, nv) -> updateResultSize());
        colNumField.textProperty().addListener((ob, ov, nv) -> updateResultSize());
        marginField.textProperty().addListener((ob, ov, nv) -> updateResultSize());
        gapField.textProperty().addListener((ob, ov, nv) -> updateResultSize());
        if (ToolSettingsUtil.getInstance().getShowImgTips()) {
            imageListView.setPlaceholder(new ImageListViewTips());
        }
        imageListView.setCellFactory(param -> new ImageListCell());
        imageListView.setOnDragDropped(this::onDragDroppedImage);
        imageListView.setOnDragOver(this::onDragOverImage);
        imageListView.setEditable(true);
        imageListView.getSelectionModel().selectedIndexProperty().addListener((ob, ov, nv) -> {
            int i = nv.intValue();
            if (i == -1) {
                moveUpBtn.setDisable(true);
                moveDownBtn.setDisable(true);
                deleteImagBtn.setDisable(true);
            } else if (i == 0) {
                moveUpBtn.setDisable(true);
                moveDownBtn.setDisable(false);
                deleteImagBtn.setDisable(false);
            } else if (i == items.size() - 1) {
                moveUpBtn.setDisable(false);
                moveDownBtn.setDisable(true);
                deleteImagBtn.setDisable(false);
            } else {
                moveUpBtn.setDisable(false);
                moveDownBtn.setDisable(false);
                deleteImagBtn.setDisable(false);
            }
        });
    }

    private void updateResultSize() {
        ObservableList<ImageInfo> items = imageListView.getItems();
        int size = items.size();
        int row = readIntFromField(rowNumField);
        int col = readIntFromField(colNumField);
        double scale = scaleField.getValue();
        if (size == 0 || row == 0 || col == 0 || scale == 0) {
            widthLabel.setText("0");
            heightLabel.setText("0");
            return;
        }
        int margin = readIntFromField(marginField);
        int gap = readIntFromField(gapField);
        int maxW = 0;
        int maxH = 0;
        for (ImageInfo imageInfo : items) {
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
        widthLabel.setText(String.valueOf(totalWidth));
        heightLabel.setText(String.valueOf(totalHeight));
    }

}
