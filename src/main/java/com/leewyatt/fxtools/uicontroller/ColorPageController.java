package com.leewyatt.fxtools.uicontroller;

import com.google.common.eventbus.Subscribe;
import com.leewyatt.fxtools.event.EventBusUtil;
import com.leewyatt.fxtools.event.PaintChooseEvent;
import com.leewyatt.fxtools.event.ShowColorPickerEvent;
import com.leewyatt.fxtools.ui.canvas.ColorHorCanvas;
import com.leewyatt.fxtools.ui.canvas.LineGradientCanvas;
import com.leewyatt.fxtools.ui.cells.GradientCell;
import com.leewyatt.fxtools.ui.paintpicker.PaintPickerStage;
import com.leewyatt.fxtools.ui.stages.ScreenColorPickerStage;
import com.leewyatt.fxtools.utils.ColorScheme01;
import com.leewyatt.fxtools.utils.OSUtil;
import com.leewyatt.fxtools.utils.PaintConvertUtil;
import com.leewyatt.rxcontrols.pane.RXCarouselPane;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

import static com.leewyatt.fxtools.utils.ResourcesUtil.message;

/**
 * @author LeeWyatt
 */
public class ColorPageController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private RXCarouselPane carouselPane;

    @FXML
    private ComboBox<String> colorCombobox;
    @FXML
    private Rectangle previewRect;
    private AnchorPane colorHorPage;

    private ScrollPane lineGradientPage;

    /**
     * 保存颜色页面选择的数据
     * Key: Integer 代表 第几页
     * Value:Integer 代表 第几个颜色
     */
    public static HashMap<Integer, Integer> colorIndexMap = new HashMap<>();

    @FXML
    void onClickLeftBtn(MouseEvent event) {
        int selectedIndex = colorCombobox.getSelectionModel().getSelectedIndex();
        if (selectedIndex <= 0) {
            colorCombobox.getSelectionModel().select(colorCombobox.getItems().size() - 1);
        } else {
            colorCombobox.getSelectionModel().select(selectedIndex - 1);
        }
    }

    @FXML
    void onClickRightBtn(MouseEvent event) {
        int selectedIndex = colorCombobox.getSelectionModel().getSelectedIndex();
        int size = colorCombobox.getItems().size();
        if (selectedIndex >= size - 1) {
            colorCombobox.getSelectionModel().select(0);
        } else {
            colorCombobox.getSelectionModel().select(selectedIndex + 1);
        }
    }

    public ColorPageController() {
        EventBusUtil.getDefault().register(this);
    }

    @FXML
    void initialize() {

        ColorHorCanvas colorHorCanvas = new ColorHorCanvas();
        colorHorPage = new AnchorPane(colorHorCanvas);
        LineGradientCanvas lineGradientCanvas = new LineGradientCanvas();
        lineGradientPage = new ScrollPane(lineGradientCanvas);

        colorCombobox.setCellFactory(param -> new GradientCell());
        colorCombobox.getSelectionModel().selectedIndexProperty().addListener((ob, ov, nv) -> {
            int index = nv.intValue();
            if (index < 0) {
                return;
            }
            if (index == 0) {
                lineGradientCanvas.setSelectedIndex(colorIndexMap.getOrDefault(0, 0));
                carouselPane.setCenter(lineGradientPage);
            }
            if (index > 0 && index <= 10) {
                //colorHorPage.setWebColors(ColorScheme01.HueScheme[index - 1].getWebColorAry());
                EventBusUtil.getDefault().post(ColorScheme01.HueScheme[index - 1].getWebColorAry());
                colorHorCanvas.setSelectedIndex(colorIndexMap.getOrDefault(index, 0));
                carouselPane.setCenter(colorHorPage);
            }
            if (index > 10 && index <= 27) {
                //colorHorPage.setWebColors(ColorScheme01.ImpressionScheme[index - 11].getWebColorAry());
                EventBusUtil.getDefault().post(ColorScheme01.ImpressionScheme[index - 11].getWebColorAry());
                colorHorCanvas.setSelectedIndex(colorIndexMap.getOrDefault(index, 0));
                carouselPane.setCenter(colorHorPage);
            }
        });

        ObservableList<String> observableList = FXCollections.observableArrayList();
        for (int i = 0; i <= 27; i++) {
            observableList.add(message("colorScheme.cs" + i));
        }
        colorCombobox.setItems(observableList);
        colorCombobox.getSelectionModel().select(0);
    }

    @Subscribe
    public void choosePaintChooseHandler(PaintChooseEvent paintChooseEvent) {
        colorIndexMap.put(colorCombobox.getSelectionModel().getSelectedIndex(), paintChooseEvent.getIndex());
        previewRect.setFill(paintChooseEvent.getPaint());
        PaintPickerStage.getInstance().getPaintPicker().setPaintProperty(paintChooseEvent.getPaint());
    }

    @Subscribe
    public void colorPickerHandler(Color color) {
        previewRect.setFill(color);
        PaintPickerStage.getInstance().getPaintPicker().setPaintProperty(color);
    }


    @FXML
    void copyPaintCodeAction(ActionEvent event) {
        OSUtil.writeToClipboard(PaintConvertUtil.convertPaintToJavaCode(previewRect.getFill()));
    }

    @FXML
    void copyPaintCssAction(ActionEvent event) {
        OSUtil.writeToClipboard(PaintConvertUtil.convertPaintToCss(previewRect.getFill()));
    }

    @FXML
    void editPaintAction(ActionEvent event) {
        colorPickerStageShow();
    }

    @FXML
    void pickerColorAction(ActionEvent event) {
        new ScreenColorPickerStage().showStage();
    }

    @Subscribe
    public void showColorPickerHandler(ShowColorPickerEvent event) {
        colorPickerStageShow();
    }

    private void colorPickerStageShow() {
        PaintPickerStage.getInstance().getPaintPicker().setPaintProperty(previewRect.getFill());
        PaintPickerStage.getInstance().showOnTop();
    }
}