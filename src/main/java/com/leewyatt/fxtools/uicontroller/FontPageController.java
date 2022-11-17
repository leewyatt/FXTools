package com.leewyatt.fxtools.uicontroller;

import com.leewyatt.fxtools.event.EventBusUtil;
import com.leewyatt.fxtools.model.FontInfo;
import com.leewyatt.fxtools.services.LoadFontService;
import com.leewyatt.fxtools.ui.cells.FontListCell;
import com.leewyatt.fxtools.ui.cells.FontTreeCell;
import com.leewyatt.fxtools.ui.paintpicker.PaintPicker;
import com.leewyatt.fxtools.ui.paintpicker.PaintPickerStage;
import com.leewyatt.fxtools.utils.PaintConvertUtil;
import com.leewyatt.fxtools.utils.OSUtil;
import com.leewyatt.rxcontrols.animation.carousel.*;
import com.leewyatt.rxcontrols.controls.RXCarousel;
import com.leewyatt.rxcontrols.controls.RXTextField;
import com.leewyatt.rxcontrols.event.RXActionEvent;
import com.leewyatt.rxcontrols.event.RXCarouselEvent;
import com.leewyatt.rxcontrols.pane.RXCarouselPane;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.TransferMode;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import org.controlsfx.control.MaskerPane;

import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static com.leewyatt.fxtools.utils.ResourcesUtil.message;

/**
 * @author LeeWyatt
 */
public class FontPageController {
    @FXML
    private ComboBox<String> copyTypeCombobox;

    private TreeView<FontInfo> fontTreeView;
    private ListView<FontInfo> fontListView;

    @FXML
    private Slider fontSizeSlider;

    @FXML
    private TextField previewTextField;

    @FXML
    private RXTextField searchFontField;

    @FXML
    private CheckBox withColorCheckBox;

    @FXML
    private Button editFontColorBtn;

    @FXML
    private RXCarousel fontCarousel;

    @FXML
    private MaskerPane maskPane;

    private ObservableList<FontInfo> fontInfos;
    private FilteredList<FontInfo> filteredFontList;
    private static final FontInfo DEFAULT_FONT_INFO = new FontInfo("System", "System Regular");
    private FontInfo lastFontInfo;

    private static final String REGULA_WEIGHT = "Regular";
    private static final String ITALIC_STYLE = "Italic";
    private static final String DEFAULT = "null";

    private static final String LOCAL_FONT = "Local Font";
    private static final String DEFAULT_STYLE_BLACK = "-fx-text-fill: black";

    private TreeItem<FontInfo> treeRoot;

    public FontPageController() {
        EventBusUtil.getDefault().register(this);
    }

    @FXML
    void initialize() {
        fontTreeView = new TreeView<>();
        fontListView = new ListView<>();

        initCarousel();

        List<String> families = Font.getFamilies();
        initFontTreeView(families);
        initFontListView(families);

        editFontColorBtn.disableProperty().bind(withColorCheckBox.selectedProperty().not());
        PaintPicker paintPicker = PaintPickerStage.getInstance().getPaintPicker();
        paintPicker.paintProperty().addListener((ob, ov, nv) -> changFill());
        withColorCheckBox.selectedProperty().addListener((ob, ov, nv) -> changFill());
        copyTypeCombobox.getItems().addAll(
                message("fontPage.copyCode"),
                message("fontPage.copyCss"),
                message("fontPage.copyShortCss"),
                message("fontPage.fontFamily"),
                message("fontPage.fontName")
        );
        copyTypeCombobox.getSelectionModel().select(0);

        searchFontField.textProperty().addListener((ob, ov, keywords) -> {
            boolean empty = keywords.trim().isEmpty();
            int gotoIndex = empty ? 1 : 0;
            if (fontCarousel.getSelectedIndex() != gotoIndex) {
                fontCarousel.setSelectedIndex(gotoIndex);
            }
            filteredFontList.setPredicate(item -> empty || item.containKeywords(keywords));
        });
        fontSizeSlider.valueProperty().addListener((os, ov, nv) -> {
            fontChanged();
        });

    }

    private void initCarousel() {
        int x = (int) (Math.random() * 5);
        CarouselAnimation animation;
        if (x == 0) {
            animation = new AnimVerBox();
        } else if (x == 1) {
            animation = new AnimHorBlinds();
        } else if (x == 2) {
            animation = new AnimLinePair();
        } else if (x == 3) {
            animation = new AnimFlip();
        } else {
            animation = new AnimLineSingle(RXCarousel.RXDirection.VER);
        }
        fontCarousel.setCarouselAnimation(animation);

        fontCarousel.setAnimationTime(Duration.millis(200));
        fontCarousel.getPaneList().setAll(new RXCarouselPane(fontListView), new RXCarouselPane(fontTreeView));
        fontCarousel.setSelectedIndex(1);

        fontCarousel.setOnDragDropped(this::onDragDroppedFont);
        fontCarousel.setOnDragOver(this::onDragOverFont);
    }

    void onDragDroppedFont(DragEvent event) {
        Dragboard db = event.getDragboard();
        boolean success = false;
        if (db.hasFiles()) {
            List<File> files = event.getDragboard().getFiles();
            files = files.stream().filter(this::isFontFile).collect(Collectors.toList());
            loadFontFiles(files);
            success = true;
        }
        event.setDropCompleted(success);
        event.consume();
    }

    void onDragOverFont(DragEvent event) {
        if (event.getGestureSource() != fontTreeView
                && event.getDragboard().hasFiles()) {
            List<File> files = event.getDragboard().getFiles();
            for (File file : files) {
                if (isFontFile(file)) {
                    event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                    break;
                }
            }
        }
        event.consume();
    }

    private boolean isFontFile(File file) {
        String name = file.getName().toLowerCase(Locale.ROOT);
        return file.isFile() && (name.endsWith(".otf") || name.endsWith(".ttf"));
    }

    @FXML
    void onOpeningFontPage(RXCarouselEvent event) {
        changFill();
        //searchFontField.requestFocus();
    }

    private void changFill() {
        Paint paint = PaintPickerStage.getInstance().getPaintPicker().getPaintProperty();
        if (withColorCheckBox.isSelected()) {
            previewTextField.setStyle("-fx-text-fill: " + PaintConvertUtil.convertPaintToCss(paint) + ";");
        } else {
            previewTextField.setStyle(DEFAULT_STYLE_BLACK);
        }
    }

    @FXML
    void addFontAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("ttf otf", "*.ttf", "*.TTF", "*.otf", "*.OTF"));
        List<File> files = fileChooser.showOpenMultipleDialog(fontListView.getScene().getWindow());
        if (files == null) {
            return;
        }
        loadFontFiles(files);
    }

    private void loadFontFiles(List<File> files) {
        if (files == null || files.isEmpty()) {
            return;
        }

        LoadFontService service = new LoadFontService(files, fontSizeSlider.getValue(), fontInfos);
        maskPane.visibleProperty().bind(service.runningProperty());
        maskPane.progressProperty().bind(service.progressProperty());
        service.valueProperty().addListener((ob, ov, nv) -> {
            if (nv == null || nv.isEmpty()) {
                return;
            }
            for (FontInfo fontInfo : nv) {
                if (!fontInfos.contains(fontInfo)) {
                    fontInfos.add(fontInfo);
                    TreeItem<FontInfo> parentNode = new TreeItem<>(fontInfo);
                    treeRoot.getChildren().add(parentNode);
                }
            }

        });
        service.start();
    }

    @FXML
    void clearKeyWordsAction(RXActionEvent event) {
        searchFontField.setText("");
    }

    @FXML
    void onCopyFontAction(ActionEvent event) {
        int selectedIndex = copyTypeCombobox.getSelectionModel().getSelectedIndex();
        if (selectedIndex == 0) {
            copyJavaCodeAction();
        } else if (selectedIndex == 1) {
            copyCss();
        } else if (selectedIndex == 2) {
            copyShortenedCss();
        } else if (selectedIndex == 3) {
            OSUtil.writeToClipboard(lastFontInfo.getFamily());
        } else if (selectedIndex == 4) {
            OSUtil.writeToClipboard(lastFontInfo.getName());
        }
    }

    @FXML
    void onEditColorAction(ActionEvent event) {
        PaintPickerStage.getInstance().showOnTop();
    }

    public void initFontListView(List<String> families) {
        fontInfos = FXCollections.observableArrayList();
        for (String family : families) {
            fontInfos.addAll(Font.getFontNames(family).stream().map(name -> new FontInfo(family, name)).collect(Collectors.toList()));
        }
        filteredFontList = fontInfos.filtered(item -> true);
        fontListView.setCellFactory(param -> new FontListCell());
        fontListView.setItems(filteredFontList);
        fontListView.setOnMousePressed(event -> {
            selectedFontAtListView();
        });
        fontListView.setOnKeyPressed(event -> {
            KeyCode code = event.getCode();
            if (code == KeyCode.UP || code == KeyCode.DOWN || code==KeyCode.HOME || code==KeyCode.END || code==KeyCode.PAGE_UP || code==KeyCode.PAGE_DOWN) {
                selectedFontAtListView();
            }
        });

        //bug原因,可能跟scene有关
        //fontListView.getSelectionModel().selectedItemProperty().addListener((ob, ov, nv) -> {
        //    lastFontInfo = nv == null ? DEFAULT_FONT_INFO : nv;
        //    previewTextField.setFont(new Font(lastFontInfo.getName(), fontSizeSlider.getValue()));
        //});


    }

    private void selectedFontAtListView() {
        FontInfo fontInfo = fontListView.getSelectionModel().getSelectedItem();
        lastFontInfo = fontInfo == null ? DEFAULT_FONT_INFO : fontInfo;
        previewTextField.setFont(new Font(lastFontInfo.getName(), fontSizeSlider.getValue()));
    }

    private void initFontTreeView(List<String> families) {
        treeRoot = new TreeItem<>(new FontInfo("System Fonts", "System Fonts"));
        fontTreeView.setRoot(treeRoot);
        fontTreeView.setShowRoot(false);
        fontTreeView.setCellFactory(param -> new FontTreeCell());
        for (String family : families) {
            List<String> fontNames = Font.getFontNames(family);
            int size = fontNames.size();
            if (size > 0) {
                TreeItem<FontInfo> parentNode = new TreeItem<>(new FontInfo(family, fontNames.get(0)));
                for (int i = 1; i < size; i++) {
                    parentNode.getChildren().add(new TreeItem<>(new FontInfo(family, fontNames.get(i))));
                }
                treeRoot.getChildren().add(parentNode);
            }
        }
        fontTreeView.getSelectionModel().selectedItemProperty().addListener((ob, ov, newValue) -> {
            fontChanged();
        });
        fontTreeView.getSelectionModel().selectFirst();
        fontTreeView.getSelectionModel().clearSelection();

    }

    private void fontChanged() {
        int selectedIndex = fontCarousel.getSelectedIndex();
        if (selectedIndex == 1) {
            TreeItem<FontInfo> selectedItem = fontTreeView.getSelectionModel().getSelectedItem();
            lastFontInfo = selectedItem == null ? DEFAULT_FONT_INFO : selectedItem.getValue();
        }else {
            FontInfo selectedItem = fontListView.getSelectionModel().getSelectedItem();
            lastFontInfo = selectedItem == null ? DEFAULT_FONT_INFO : selectedItem;
        }
        Font tempFont = new Font(lastFontInfo.getName(), fontSizeSlider.getValue());
        previewTextField.setFont(tempFont);
    }

    private void copyCss() {
        String cssCode;
        int fontSize = (int) fontSizeSlider.getValue();
        String cssFontSize = "-fx-font-size: " + fontSize + ";";
        String paintCss = "";
        if (withColorCheckBox.isSelected()) {
            String paintStr = PaintConvertUtil.convertPaintToCss(PaintPickerStage.getInstance().getPaintPicker().getPaintProperty());
            paintCss = String.format("%s-fx-text-fill: %s;%s-fx-fill: %s;", System.lineSeparator(), paintStr, System.lineSeparator(), paintStr);
        }
        String fontFamily = lastFontInfo.getFamily();

        String cssFontPosture = null;
        String newFontStyle = lastFontInfo.getName().replace(lastFontInfo.getFamily(), "");
        if (newFontStyle.endsWith(ITALIC_STYLE)) {
            cssFontPosture = "-fx-font-style: italic;";
        }
        String cssWeightName = null;
        String weightName = newFontStyle.replace(ITALIC_STYLE, "").replace("-", "").trim();
        FontWeight fontWeight = FontWeight.findByName(weightName);
        if (fontWeight != null && fontWeight != FontWeight.NORMAL) {
            cssWeightName = "-fx-font-weight: " + fontWeight.name() + ";";
        }

        String cssFontFamily = String.format("-fx-font-family: \"%s\";", fontFamily);
        StringBuilder sb = new StringBuilder();
        sb.append(cssFontFamily).append(System.lineSeparator());
        if (cssFontPosture != null) {
            sb.append(cssFontPosture).append(System.lineSeparator());
        }
        if (cssWeightName != null) {
            sb.append(cssWeightName).append(System.lineSeparator());
        }
        sb.append(cssFontSize);
        sb.append(paintCss);
        cssCode = sb.toString();

        if (lastFontInfo.getPath() != null) {
            OSUtil.writeToClipboard("/*If external font, need to load it; @font-face {...} */" + System.lineSeparator() + cssCode);
        } else {
            OSUtil.writeToClipboard(cssCode);
        }

    }

    private void copyShortenedCss() {
        String newFontStyle = lastFontInfo.getName().replace(lastFontInfo.getFamily(), "");
        String cssCode;
        int fontSize = (int) fontSizeSlider.getValue();
        String paintCss = "";
        if (withColorCheckBox.isSelected()) {
            String paintStr = PaintConvertUtil.convertPaintToCss(PaintPickerStage.getInstance().getPaintPicker().getPaintProperty());
            paintCss = String.format("%s-fx-text-fill: %s;%s-fx-fill: %s;", System.lineSeparator(), paintStr, System.lineSeparator(), paintStr);
        }
        String fontFamily = "\"" + lastFontInfo.getFamily() + "\"";
        String strFontPosture = null;
        if (newFontStyle.endsWith(ITALIC_STYLE)) {
            strFontPosture = "italic";
        }
        String strWeightName = null;
        String weightName = newFontStyle.replace(ITALIC_STYLE, "").trim();
        FontWeight fontWeight = FontWeight.findByName(weightName);
        if (fontWeight != null && fontWeight != FontWeight.NORMAL) {
            strWeightName = fontWeight.name();
        }
        if (strFontPosture == null && strWeightName == null) {
            cssCode = String.format("-fx-font:%d %s;", fontSize, fontFamily);
        } else if (strFontPosture == null) {
            cssCode = String.format("-fx-font: %s %d %s;", strWeightName, fontSize, fontFamily);
        } else if (strWeightName == null) {
            cssCode = String.format("-fx-font: %s %d %s;", strFontPosture, fontSize, fontFamily);
        } else {
            cssCode = String.format("-fx-font: %s %s %d %s;", strFontPosture, strWeightName, fontSize, fontFamily);
        }
        cssCode += paintCss;

        if (lastFontInfo.getPath() != null) {
            OSUtil.writeToClipboard("/*If external font, need to load it; @font-face {...} */" + System.lineSeparator() + cssCode);
        } else {
            OSUtil.writeToClipboard(cssCode);
        }
    }

    private void copyJavaCodeAction() {
        String javaCode;
        int fontSize = (int) fontSizeSlider.getValue();
        String paintStr = "";
        if (withColorCheckBox.isSelected()) {
            Paint paint = PaintPickerStage.getInstance().getPaintPicker().getPaintProperty();
            paintStr = PaintConvertUtil.convertPaintToJavaCode(paint);
        }
        javaCode = String.format("Font font = new Font(\"%s\", %d);", lastFontInfo, fontSize);
        if (lastFontInfo.getPath() != null) {
            OSUtil.writeToClipboard(String.format("%s%s%s%s", paintStr.isEmpty() ? "" : paintStr + System.lineSeparator(), "\t//If external font, need to load it; Font.loadFont(...);", System.lineSeparator(), javaCode));
        } else {
            OSUtil.writeToClipboard(String.format("%s%s", paintStr.isEmpty() ? "" : paintStr + System.lineSeparator(), javaCode));
        }
    }

    @FXML
    void openFonteskWebsite(ActionEvent event) {
        OSUtil.showDoc("https://fontesk.com/license/free-for-commercial-use/");
    }

    @FXML
    void openGoogleFontsWebsite(ActionEvent event) {
        OSUtil.showDoc("https://fonts.google.com/");
    }

}
