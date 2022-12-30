package com.leewyatt.fxtools.uicontroller;

import com.leewyatt.fxtools.model.SvgXml;
import com.leewyatt.fxtools.ui.alert.InformationAlert;
import com.leewyatt.fxtools.ui.paintpicker.DoubleTextField;
import com.leewyatt.fxtools.ui.paintpicker.PaintPicker;
import com.leewyatt.fxtools.ui.paintpicker.PaintPickerStage;
import com.leewyatt.fxtools.ui.stages.FullSvgStage;
import com.leewyatt.fxtools.utils.*;
import com.leewyatt.rxcontrols.controls.RXToggleButton;
import com.leewyatt.rxcontrols.event.RXCarouselEvent;
import com.leewyatt.rxcontrols.utils.StyleUtil;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.SVGPath;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static com.leewyatt.fxtools.utils.OSUtil.openAndSelectedFile;
import static com.leewyatt.fxtools.utils.ResourcesUtil.message;

/**
 * @author LeeWyatt
 */
public class SvgPageController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button editSvgColorBtn;

    @FXML
    private StackPane svgPreviewPane;

    @FXML
    private CheckBox svgWithColorCheckBox;

    @FXML
    private StackPane imagePreviewPane;

    @FXML
    private ComboBox<String> codeComboBox;

    @FXML
    private CheckBox graySelected;

    @FXML
    private CheckBox doubleCheckBox;

    @FXML
    private Label fxHeightLabel;

    @FXML
    private Label fxPathLabel;

    @FXML
    private Label fxWidthLabel;

    @FXML
    private ComboBox<String> imageComboBox;

    @FXML
    private TextArea textArea;

    @FXML
    private CheckBox tripleCheckBox;

    @FXML
    private Label webHeightLabel;

    @FXML
    private Label webWidthLabel;
    @FXML
    private Label svgFileNameLabel;

    @FXML
    private HBox widthBox;
    @FXML
    private HBox heightBox;

    @FXML
    private Pane svgControlsPane;

    @FXML
    private ToggleGroup imageBgGroup;

    @FXML
    private ToggleGroup svgBgGroup;

    @FXML
    private RXToggleButton imgBgWhiteBtn;

    @FXML
    private RXToggleButton svgBgTransparentBtn;

    private DoubleTextField widthFiled;

    private DoubleTextField heightField;

    private String lastFilePath;
    private SVGPath svgPath;
    private ImageView imageView;
    private Region svgRegion;
    private SvgXml lastSvgXml;
    private static final String DEFAULT_BLACK_GROUND = "-fx-background-color: black;";
    private ChangeListener<String> wChangedListener;
    private ChangeListener<String> hChangedListener;
    private static final String TYPE_PATH_CONTENT = "Path Content";
    private static final String TYPE_CODE_SVG_PATH = "Code SvgPath";
    private static final String TYPE_CODE_REGION = "Code Region";
    private static final String TYPE_CSS = "Css";
    private static final String TYPE_FILE_CONTENT = "File Content";
    private ContextMenu regionContextMenu;
    private ContextMenu webViewContextMenu;

    @FXML
    void initialize() {
        //svgControlsPane.setDisable(true);
        widthFiled = createNumberField();
        widthBox.getChildren().add(widthFiled);
        heightField = createNumberField();
        heightBox.getChildren().add(heightField);
        initImagePreviewPane();
        initSvgPreviewPane();
        PaintPicker paintPicker = PaintPickerStage.getInstance().getPaintPicker();
        paintPicker.paintProperty().addListener((ob, ov, nv) -> changFill());
        svgWithColorCheckBox.selectedProperty().addListener((ob, ov, nv) -> changFill());
        imageComboBox.getItems().addAll("png", "jpg", "gif");
        imageComboBox.getSelectionModel().select(0);
        initCodeComboBox(paintPicker);
        initSizeField();

    }

    private void initSvgPreviewPane() {
        svgPath = new SVGPath();
        svgRegion = new Region();
        svgRegion.setMaxSize(0, 0);
        svgRegion.setStyle(DEFAULT_BLACK_GROUND);
        svgRegion.setShape(svgPath);
        svgPreviewPane.getChildren().add(svgRegion);
        svgPreviewPane.setOnDragDropped(this::onDragDroppedSvg);
        svgPreviewPane.setOnDragOver(this::onDragOverSvg);
        svgPreviewPane.setOnScroll(event -> {
            if (!verifiedPath()) {
                return;
            }
            double w;
            if (event.getDeltaY() > 0) {
                w = widthFiled.getValue() * 1.1;
            } else {
                w = widthFiled.getValue() * 0.9;
            }
            widthFiled.setText(w + "");
        });
        svgBgGroup.selectedToggleProperty().addListener((ob, ov, nv) -> changePreviewBg((RXToggleButton) nv, svgPreviewPane));
        svgBgTransparentBtn.setSelected(true);
        regionContextMenu = createRegionContextMenu();
        svgPreviewPane.setOnMousePressed(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                if (regionContextMenu.isShowing()) {
                    regionContextMenu.hide();
                }
                if (event.isControlDown() && lastFilePath != null) {
                    openAndSelectedFile(lastFilePath);
                }
            } else if (event.getButton() == MouseButton.SECONDARY) {
                regionContextMenu.show(svgPreviewPane, event.getScreenX(), event.getScreenY());
            }
            event.consume();
        });
    }

    private void initImagePreviewPane() {
        imageView = new ImageView();
        imagePreviewPane.setOnDragDropped(this::onDragDroppedSvg);
        imagePreviewPane.setOnDragOver(this::onDragOverSvg);
        imagePreviewPane.getChildren().add(imageView);
        imageBgGroup.selectedToggleProperty().addListener((ob, ov, nv) -> changePreviewBg((RXToggleButton) nv, imagePreviewPane));
        imgBgWhiteBtn.setSelected(true);
        webViewContextMenu = createWebViewContextMenu();
        imagePreviewPane.setOnMousePressed(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                if (webViewContextMenu.isShowing()) {
                    webViewContextMenu.hide();
                }
                if (event.isControlDown() && lastFilePath != null) {
                    openAndSelectedFile(lastFilePath);
                }
            } else if (event.getButton() == MouseButton.SECONDARY) {
                webViewContextMenu.show(imagePreviewPane, event.getScreenX(), event.getScreenY());
            }
            event.consume();
        });
    }

    private void changePreviewBg(RXToggleButton nv, StackPane previewPane) {
        ObservableList<String> styleClass = nv.getStyleClass();
        String bgCss = "";
        for (String aClass : styleClass) {
            if (aClass.startsWith("svg-bg")) {
                bgCss = aClass;
                break;
            }
        }
        StyleUtil.removeClass(previewPane, "svg-bg-translucent", "svg-bg-transport", "svg-bg-white", "svg-bg-black", "svg-bg-gray");
        StyleUtil.addClass(previewPane, bgCss);
    }

    private void initSizeField() {
        wChangedListener = (ob, ov, nv) -> {
            heightField.textProperty().removeListener(hChangedListener);
            widthChanged();
            heightField.textProperty().addListener(hChangedListener);
        };
        hChangedListener = (ob, ov, nv) -> {
            widthFiled.textProperty().removeListener(wChangedListener);
            heightChanged();
            widthFiled.textProperty().addListener(wChangedListener);
        };
        widthFiled.textProperty().addListener(wChangedListener);
        heightField.textProperty().addListener(hChangedListener);
    }

    private void initCodeComboBox(PaintPicker paintPicker) {
        codeComboBox.getItems().addAll(TYPE_PATH_CONTENT, TYPE_FILE_CONTENT, TYPE_CODE_SVG_PATH, TYPE_CODE_REGION, TYPE_CSS);

        codeComboBox.getSelectionModel().selectedItemProperty().addListener((ob, ov, item) -> {
            if (lastSvgXml == null) {
                return;
            }
            if (item == null) {
                return;
            } else if (TYPE_PATH_CONTENT.equals(item)) {//Path Content
                textArea.setText(lastSvgXml.getSvgPath());
            } else if (TYPE_CODE_SVG_PATH.equals(item)) {//Code SvgPath
                Paint fill = svgWithColorCheckBox.isSelected() ? paintPicker.getPaintProperty() : Color.BLACK;
                double scaleX = NumberUtil.roundingDouble(widthFiled.getValue() / svgPath.getLayoutBounds().getWidth(), 3);
                String paintStr = PaintConvertUtil.convertPaintToJavaCode(fill);
                String result;
                if (Color.BLACK.equals(fill) && Double.compare(scaleX, 1.0) == 0) {
                    result = String.format("SVGPath svgPath = new SVGPath();%ssvgPath.setContent(\"%s\");",
                            System.lineSeparator(), svgPath.getContent());
                } else if (Color.BLACK.equals(fill)) {
                    result = String.format("SVGPath svgPath = new SVGPath();%ssvgPath.setContent(\"%s\");" +
                                    "%ssvgPath.setScaleX(%s);%ssvgPath.setScaleY(%s);",
                            System.lineSeparator(), svgPath.getContent(), System.lineSeparator(),
                            scaleX, System.lineSeparator(), scaleX);
                } else if (Double.compare(scaleX, 1.0) == 0) {
                    result = String.format("SVGPath svgPath = new SVGPath();%ssvgPath.setContent(\"%s\");" +
                                    "%s%s%ssvgPath.setFill(paint);",
                            System.lineSeparator(), svgPath.getContent(), System.lineSeparator(), paintStr, System.lineSeparator());
                } else {
                    result = String.format("SVGPath svgPath = new SVGPath();%ssvgPath.setContent(\"%s\");" +
                                    "%s%s%ssvgPath.setFill(paint);%ssvgPath.setScaleX(%s);%ssvgPath.setScaleY(%s);",
                            System.lineSeparator(), svgPath.getContent(), System.lineSeparator(), paintStr, System.lineSeparator(), System.lineSeparator(),
                            scaleX, System.lineSeparator(), scaleX);
                }
                textArea.setText(result);
            } else if (TYPE_CODE_REGION.equals(item)) {//Code Region
                Paint fill = svgWithColorCheckBox.isSelected() ? paintPicker.getPaintProperty() : Color.BLACK;
                String content = lastSvgXml.getSvgPath();
                String paint = PaintConvertUtil.convertPaintToJavaCode(fill);
                String w = widthFiled.getText();
                String h = heightField.getText();
                String formatWithColor = "Region region = new Region();" + System.lineSeparator() +
                        "SvgPath svgPath = new SvgPath();" + System.lineSeparator() +
                        "svgPath.setContent(\"%s\");" + System.lineSeparator() +
                        "svgRegion.setShape(svgPath);" + System.lineSeparator() +
                        "%s;" + System.lineSeparator() +
                        "svgRegion.setBackground(new Background(new BackgroundFill(paint, null,null)));" + System.lineSeparator() +
                        "region.setPreSize(%s,%s);" + System.lineSeparator() +
                        "region.setMaxSize(%s,%s);";

                String result = String.format(formatWithColor, content, paint, w, h, w, h);
                textArea.setText(result);
            } else if (TYPE_CSS.equals(item)) { //Css
                Paint fill = svgWithColorCheckBox.isSelected() ? paintPicker.getPaintProperty() : Color.BLACK;
                String cssStr = String.format("-fx-shape: \"%s\";", lastSvgXml.getSvgPath());
                if (!Color.BLACK.equals(fill)) {
                    String paintStr = PaintConvertUtil.convertPaintToCss(fill);
                    cssStr += String.format("%s-fx-background-color: %s;", System.lineSeparator(), paintStr);
                }
                textArea.setText(cssStr);
            } else if (TYPE_FILE_CONTENT.equals(item)) {//SVG File Content
                if (lastFilePath != null) {
                    StringBuilder sb = new StringBuilder();
                    try (Stream<String> stream = Files.lines(Paths.get(lastFilePath))) {
                        stream.forEach(line -> sb.append(line).append(System.lineSeparator()));
                        textArea.setText(sb.toString());
                    } catch (IOException ioException) {
                        Logger logger = Logger.getLogger("com.leewyatt.fxtools.uicontroller.SvgPageController");
                        logger.severe("IOException: read Svg File failed.\t" + ioException);
                        ioException.printStackTrace();
                    }
                } else {
                    textArea.setText("");
                }
            }
            textArea.positionCaret(0);
        });
        codeComboBox.getSelectionModel().select(0);
    }

    private void onDragDroppedSvg(DragEvent event) {
        Dragboard db = event.getDragboard();
        boolean success = false;
        File svgFile = null;
        if (db.hasFiles()) {
            List<File> files = event.getDragboard().getFiles();
            for (File file : files) {
                if (isSvgFile(file)) {
                    svgFile = file;
                    success = true;
                    break;
                }
            }
        }
        event.setDropCompleted(success);
        event.consume();
        if (svgFile != null) {
            setXMLData(svgFile);
        }
    }

    void onDragOverSvg(DragEvent event) {
        if (event.getGestureSource() != imageView
                && event.getDragboard().hasFiles()) {
            List<File> files = event.getDragboard().getFiles();
            for (File file : files) {
                if (isSvgFile(file)) {
                    event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                    break;
                }
            }
        }
        event.consume();
    }

    private boolean isSvgFile(File file) {
        String name = file.getName().toLowerCase(Locale.ROOT);
        return file.isFile() && (name.endsWith(".svg"));
    }

    @FXML
    void openIcoMoonWebsite(ActionEvent event) {
        OSUtil.showDoc(Constants.icoMoon);
    }

    @FXML
    void openIconfontWebsite(ActionEvent event) {
        OSUtil.showDoc(Constants.iconfont);
    }

    /**
     * 是否是合理的SVG路径
     */
    private boolean verifiedPath() {
        return lastSvgXml != null
                && !lastSvgXml.getPathCount().equals("0")
                && !lastSvgXml.getPathCount().equalsIgnoreCase("Error");
    }

    private void heightChanged() {
        Bounds bounds = svgPath.getLayoutBounds();
        double width = bounds.getWidth();
        double height = bounds.getHeight();
        double newHeight = heightField.getValue();
        double scale = newHeight / height;
        double newWidth = width * scale;
        widthFiled.setText(newWidth + "");
        double rate = NumberUtil.computeScaleRate(width, height, 128);
        svgRegion.setMaxSize(newWidth * rate, newHeight * rate);
        reSelectedCodeCombobox();
    }
    private void widthChanged() {
        Bounds bounds = svgPath.getLayoutBounds();
        double width = bounds.getWidth();
        double height = bounds.getHeight();
        double newWidth = widthFiled.getValue();
        double scale = newWidth / width;
        double newHeight= height * scale;
        heightField.setText(newHeight + "");
        double rate = NumberUtil.computeScaleRate(width, height, 128);
        svgRegion.setMaxSize(newWidth * rate, newHeight * rate);
        reSelectedCodeCombobox();
    }

    private DoubleTextField createNumberField() {
        DoubleTextField widthFiled = new DoubleTextField();
        widthFiled.setPrefSize(58, 25);
        widthFiled.getStyleClass().add("size-field");
        return widthFiled;
    }

    @FXML
    void onOpeningSvgPage(RXCarouselEvent event) {
        svgRegion.setStyle(DEFAULT_BLACK_GROUND);

        changFill();
    }

    private void changFill() {
        Paint paint = PaintPickerStage.getInstance().getPaintPicker().getPaintProperty();
        FullSvgStage svgStage = FullSvgStage.getInstance();
        SVGPath svgPathNode = svgStage.getSvgPathNode();
        if (svgWithColorCheckBox.isSelected()) {
            svgPathNode.setFill(paint);
            svgRegion.setStyle("-fx-background-color: " + PaintConvertUtil.convertPaintToCss(paint) + ";");
        } else {
            svgPathNode.setFill(paint);
            svgRegion.setStyle(DEFAULT_BLACK_GROUND);
        }
        reSelectedCodeCombobox();
    }

    private void reSelectedCodeCombobox() {
        String selectedItem = codeComboBox.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            codeComboBox.getSelectionModel().select(-1);
            codeComboBox.getSelectionModel().select(selectedItem);
        }
    }

    @FXML
    void copyCodeAction(MouseEvent event) {
        String text = textArea.getText();
        if (text == null || text.isEmpty()) {
            return;
        }
        String selectedItem = codeComboBox.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            OSUtil.writeToClipboard(text);
        }

    }

    @FXML
    void copyImageAction(MouseEvent event) {
        if (lastSvgXml == null) {
            return;
        }
        double scale = widthFiled.getValue() / svgPath.getLayoutBounds().getWidth();
        FullSvgStage svgStage = FullSvgStage.getInstance();
        svgStage.setSvgContent(lastSvgXml.getSvgPath(),
                svgWithColorCheckBox.isSelected() ? PaintPickerStage.getInstance().getPaintPicker().getPaintProperty() : Color.BLACK,
                1.0);
        svgStage.getSvgPathNode().setScaleX(scale);
        svgStage.getSvgPathNode().setScaleY(scale);
        ImageUtil.copyNodeSnapshotImagesToClipboard(svgStage.getSvgPathNode(), FileUtil.getNameWithoutExtension(lastFilePath), imageComboBox.getSelectionModel().getSelectedItem(),
                doubleCheckBox.isSelected(), tripleCheckBox.isSelected(), graySelected.isSelected());
    }

    @FXML
    void openSvgFileAction(ActionEvent event) {
        openSvgFile();
    }

    private void openSvgFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("SVG", "*.svg", "*.SVG"));
        if (lastFilePath != null) {
            File initDir = new File(lastFilePath).getParentFile();
            if (initDir.exists()) {
                fileChooser.setInitialDirectory(initDir);
            }
        }
        File file = fileChooser.showOpenDialog(imagePreviewPane.getScene().getWindow());
        if (file != null) {
            setXMLData(file);
        }
    }

    @FXML
    void pasteSvgPathAction(ActionEvent event) {
        String str = OSUtil.getClipboardString();
        if (str == null || str.trim().isEmpty()) {
            new InformationAlert(message("svgPage.alert")).showAndWait();
            return;
        }
        String path = str.trim();
        String reg = "\"([^\"]*)\"";
        Pattern pattern = Pattern.compile(reg);

        if (str.contains("-fx-shape")) {
            String s = str.split("-fx-shape")[1].trim();
            Matcher matcher = pattern.matcher(s);
            if (matcher.find()) {
                path = matcher.group(0).replace("\"", "");
            }
        } else if (str.contains("\"")) {
            Matcher matcher = pattern.matcher(str);
            if (matcher.find()) {
                path = matcher.group(0).replace("\"", "");
            }
        }
        lastSvgXml = new SvgXml();
        lastSvgXml.setWidth("-1");
        lastSvgXml.setHeight("-1");
        lastSvgXml.setPathCount("-");
        lastSvgXml.setSvgPath(path);
        lastFilePath = null;
        imageView.setImage(null);
        parseToShow();
    }

    private void setXMLData(File file) {
        svgFileNameLabel.setText(file.getName());
        lastSvgXml = SVGUtil.getSvgPath(file);
        boolean validPath = verifiedPath();
        lastFilePath = file.getAbsolutePath();
        if (!validPath) {
            textArea.setText("");
        } else {
            double width = Double.parseDouble(lastSvgXml.getWidth());
            double height = Double.parseDouble(lastSvgXml.getHeight());
            double scale = NumberUtil.computeScaleRate(width, height, 122);
            imageView.setImage(ImageUtil.svgToPngFXImg(file.getPath(), (int) (width * scale), (int) (height * scale)));
            reSelectedCodeCombobox();
        }
        parseToShow();
    }

    private void parseToShow() {
        webWidthLabel.setText(String.format(lastSvgXml.getWidth()));
        webHeightLabel.setText(String.format(lastSvgXml.getHeight()));
        String path = lastSvgXml.getSvgPath();
        svgPath.setContent(path);

        Bounds bounds = svgPath.getLayoutBounds();
        double width = bounds.getWidth();
        double height = bounds.getHeight();
        if (width == -1 || height == -1) {
            svgPath.setContent(null);
            textArea.setText("");
            lastSvgXml = null;
            widthFiled.setText("");
            heightField.setText("");
            fxWidthLabel.setText("");
            fxHeightLabel.setText("");
            fxPathLabel.setText("");
            imageView.setImage(null);
            new InformationAlert(message("svgPage.alert")).showAndWait();
            return;
        }
        double rate = NumberUtil.computeScaleRate(width, height, 128);
        svgRegion.setMaxSize(width * rate, height * rate);

        widthFiled.setText(String.format("%.2f", width));
        heightField.setText(String.format("%.2f", height));
        fxWidthLabel.setText(String.format("%.2f", width));
        fxHeightLabel.setText(String.format("%.2f", height));
        String pathCount = lastSvgXml.getPathCount();
        fxPathLabel.setText(pathCount);
    }

    @FXML
    void onEditSvgColorAction(ActionEvent event) {
        PaintPickerStage.getInstance().showOnTop();
    }

    private ContextMenu createWebViewContextMenu() {
        MenuItem openAtDeskTopItem = new MenuItem(message("svgPage.open"), new Region());
        openAtDeskTopItem.getStyleClass().addAll("custom-menu-item", "browser-btn");
        openAtDeskTopItem.setOnAction(event -> {
            if (lastFilePath != null) {
                OSUtil.showDoc(new File(lastFilePath).toURI().toString());
            }
            event.consume();
        });
        MenuItem openAtSelectedItem = new MenuItem(message("svgPage.explorer"), new Region());
        openAtSelectedItem.getStyleClass().addAll("custom-menu-item", "select-svg-file-btn");
        openAtSelectedItem.setOnAction(event -> {
            if (lastFilePath != null) {
                OSUtil.openAndSelectedFile(lastFilePath);
            }
            event.consume();
        });
        MenuItem reLoadItem = getReLoadItem();

        return new ContextMenu(openAtDeskTopItem, openAtSelectedItem, reLoadItem);
    }

    private ContextMenu createRegionContextMenu() {
        MenuItem fullScreenItem = new MenuItem(message("svgPage.fullScreen"), new Region());
        fullScreenItem.getStyleClass().addAll("custom-menu-item", "full-btn");
        fullScreenItem.setOnAction(event -> {
            if (lastSvgXml != null) {
                FullSvgStage svgStage = FullSvgStage.getInstance();
                svgStage.setSvgContent(lastSvgXml.getSvgPath(),
                        svgWithColorCheckBox.isSelected() ? PaintPickerStage.getInstance().getPaintPicker().getPaintProperty() : Color.BLACK,
                        1.0);
                svgStage.showAndWait();
            }
            event.consume();
        });

        MenuItem reLoadItem = getReLoadItem();
        return new ContextMenu(fullScreenItem, reLoadItem);
    }

    private MenuItem getReLoadItem() {
        MenuItem reLoadItem = new MenuItem(message("svgPage.reload"), new Region());
        reLoadItem.getStyleClass().addAll("custom-menu-item", "reload-btn");
        reLoadItem.setOnAction(event -> {
            if (lastFilePath != null) {
                File file = new File(lastFilePath);
                if (file.exists()) {
                    setXMLData(file);
                }
            }
            event.consume();
        });
        return reLoadItem;
    }
}
