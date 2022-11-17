package com.leewyatt.fxtools.ui.paintpicker;

import com.leewyatt.fxtools.utils.Constants;
import com.leewyatt.fxtools.utils.CustomColorUtil;
import com.leewyatt.fxtools.utils.PaintConvertUtil;
import com.leewyatt.fxtools.utils.OSUtil;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;

/**
 * @author LeeWyatt
 */
public class FXColorInfoPane extends BorderPane {
    private static final String COLOR_REG = "^#([a-fA-F0-9]){6}";

    private final TextField fieldHsl = new TextField();
    private final TextField fieldRgb = new TextField();
    private final TextField fieldHex = new TextField();
    private final TextField fieldHsb = new TextField();

    private final CheckBox checkBoxWithAlpha = new CheckBox("opacity");
    private final CheckBox checkBoxSharp = new CheckBox("with #");
    private final ComboBox<String> comboBoxType = new ComboBox<String>(FXCollections.observableArrayList("Integer", "Decimal", "Percentage"));

    //private final ComboBox<String> comboBoxCodeType = new ComboBox<String>(
    //        FXCollections.observableArrayList("JavaFX Code", "JavaFX CSS", "Android Code", "Android XML", "Swing Code", "Web CSS"));
    private final ComboBox<String> comboBoxCodeType = new ComboBox<String>(
            FXCollections.observableArrayList("JavaFX Code", "JavaFX CSS"));
    private PaintPickerController paintPickerController;

    public FXColorInfoPane() {
        getStyleClass().add("info-root-pane");
        //URL cssResource = getClass().getResource("/css/colorInfoPane.css");
        //if (cssResource != null) {
        //    getStylesheets().add(cssResource.toExternalForm());
        //}
        comboBoxType.getStyleClass().add("custom-combobox");
        comboBoxCodeType.getStyleClass().add("custom-combobox");
        checkBoxWithAlpha.setSelected(true);
        checkBoxSharp.setSelected(true);
        comboBoxType.getSelectionModel().select(1);
        comboBoxCodeType.getSelectionModel().select(0);
        fieldHsb.setText("0,0,0,1.0");
        fieldHsl.setText("0,0,0,1.0");
        fieldRgb.setText("0,0,0,1.0");
        fieldHex.setText("#000000FF");


        //Center: 中部组件
        buildUI();
    }

    public void setPaintPickerController(PaintPickerController paintPickerController) {
        this.paintPickerController = paintPickerController;
        checkBoxWithAlpha.selectedProperty().addListener(e -> refreshData());
        checkBoxSharp.selectedProperty().addListener(e ->
                fieldHex.setText(paintPickerController.getColorPicker().getHexValue(checkBoxWithAlpha.isSelected(), checkBoxSharp.isSelected())));
        comboBoxType.getSelectionModel().selectedItemProperty().addListener(e -> {
            refreshData();
        });

        paintPickerController.paintProperty().addListener((observableValue, oldValue, newValue) -> {
            refreshData();
        });

        paintPickerController.getColorPicker().getHexaTextfield().textProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue.matches(COLOR_REG)) {
                refreshData();
            }
        });
        //refreshData();

    }

    public void refreshData() {
        if (paintPickerController == null) {
            return;
        }
        String text = paintPickerController.getColorPicker().getHexaTextfield().getText();
        if (!text.matches(COLOR_REG)) {
            return;
        }
        //Color c = paintPickerController.getColorPicker().getValue();
        //java.awt.Color color = ColorUtil.fromHex(text, java.awt.Color.WHITE);
        Color color;
        try {
            color = Color.web(text);
        } catch (Exception e) {
            color = Color.WHITE;
        }
        int type = comboBoxType.getSelectionModel().getSelectedIndex();
        boolean withAlpha = checkBoxWithAlpha.isSelected();
        fieldHex.setText(paintPickerController.getColorPicker().getHexValue(withAlpha, checkBoxSharp.isSelected()));
        fieldHsl.setText(CustomColorUtil.toHsl(color, withAlpha, type));
        fieldHsb.setText(CustomColorUtil.toHsb(color, withAlpha, type));
        if (type == CustomColorUtil.TYPE_INT) {
            fieldRgb.setText(paintPickerController.getColorPicker().getRGBValue(withAlpha));
        } else {
            fieldRgb.setText(CustomColorUtil.toRgb(color, withAlpha, type));
        }
    }


    private void buildUI() {
        HBox topBox = new HBox(15);
        topBox.getChildren().add(checkBoxWithAlpha);
        topBox.getChildren().add(checkBoxSharp);
        Region spaceRegion = new Region();
        topBox.getChildren().add(spaceRegion);
        topBox.getChildren().add(comboBoxType);
        //SVGPath collectSvg = new SVGPath();
        //collectSvg.setContent(PluginConstant.CollectionSvg);
        //collectSvg.setFill(Color.web("#DF5050FF"));
        //topBox.getChildren().add(new Button("", collectSvg));
        HBox.setHgrow(spaceRegion, Priority.ALWAYS);
        topBox.setAlignment(Pos.CENTER_LEFT);
        setMargin(topBox, new Insets(0, 0, 2, 0));
        setTop(topBox);

        VBox leftPane = new VBox(2, createColorPane("HSB:", fieldHsb), createColorPane("HSL:", fieldHsl));
        VBox rightPane = new VBox(2, createColorPane("RGB:", fieldRgb), createColorPane("Hex:", fieldHex));
        HBox centerBox = new HBox(2, leftPane, rightPane);
        setCenter(centerBox);
        Button copyBtn = new Button("", getCopySvgPath());
        copyBtn.getStyleClass().add("icon-btn");

        HBox bottomBox = new HBox(2, new Label("Copy:"), comboBoxCodeType, copyBtn);
        comboBoxCodeType.setMaxWidth(Double.MAX_VALUE);
        HBox.setMargin(bottomBox, new Insets(0, 0, 0, 5));
        HBox.setHgrow(comboBoxCodeType, Priority.ALWAYS);
        bottomBox.setAlignment(Pos.CENTER_RIGHT);
        setBottom(bottomBox);
        setPadding(new Insets(3));
        setBorder(new Border(new BorderStroke(Color.LIGHTGRAY, BorderStrokeStyle.SOLID, null, new BorderWidths(1))));
        copyBtn.setOnAction(e -> {
            int index = comboBoxCodeType.getSelectionModel().getSelectedIndex();
            //"JavaFX Code", "JavaFX CSS", "Swing Code"
            if (index == 0) {
                String javaCode = PaintConvertUtil.convertPaintToJavaCode(paintPickerController.getPaintProperty());
                OSUtil.writeToClipboard(javaCode);
            }
            if (index == 1) {
                String cssCode = PaintConvertUtil.convertPaintToCss(paintPickerController.getPaintProperty());
                OSUtil.writeToClipboard(cssCode);
            }
        });
    }

    private BorderPane createColorPane(String text, TextField field) {
        BorderPane pane = new BorderPane();
        Label label = new Label(text);
        BorderPane.setAlignment(label, Pos.CENTER);
        pane.setLeft(label);
        BorderPane.setMargin(field, new Insets(2));
        pane.setCenter(field);
        Button copyBtn = new Button("", getCopySvgPath());
        copyBtn.getStyleClass().add("icon-btn");
        pane.setRight(copyBtn);
        copyBtn.setOnAction(e -> {
           OSUtil.writeToClipboard(field.getText());
        });
        return pane;
    }


    private SVGPath getCopySvgPath() {
        SVGPath path = new SVGPath();
        path.setContent(Constants.CopySVG);
        return path;
    }
}
