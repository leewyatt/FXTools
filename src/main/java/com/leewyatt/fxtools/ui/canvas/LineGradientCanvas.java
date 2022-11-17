package com.leewyatt.fxtools.ui.canvas;

import com.leewyatt.fxtools.event.EventBusUtil;
import com.leewyatt.fxtools.event.PaintChooseEvent;
import com.leewyatt.fxtools.ui.paintpicker.PaintPickerStage;
import com.leewyatt.fxtools.utils.Constants;
import com.leewyatt.fxtools.utils.LinearGradientConstants;
import com.leewyatt.fxtools.utils.OSUtil;
import com.leewyatt.fxtools.utils.PaintConvertUtil;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;

import java.util.List;

import static com.leewyatt.fxtools.utils.ResourcesUtil.message;

/**
 * @author LeeWyatt
 */
public class LineGradientCanvas extends Canvas {

    public static final int CELL_W = 243;
    public static final int CELL_H = 80;

    private static final int SPACE_HEIGHT = 0;
    private final ContextMenu contextMenu;
    private int currentIndex = -1;
    private int selectedIndex = -1;

    private final List<LinearGradient> paintList;
    private final int height;

    public LineGradientCanvas() {
        paintList = LinearGradientConstants.LIST;
        height = (CELL_H + SPACE_HEIGHT) * paintList.size();
        setWidth(CELL_W);
        setHeight(height);
        contextMenu = createContextMenu();

        setOnMouseMoved(event -> {
            int tempIndex = getCurrentIndex(event);
            if (tempIndex != currentIndex && tempIndex != -1) {
                currentIndex = tempIndex;
                refresh();
            }
        });

        setOnMouseClicked(event -> {
            int tempIndex = getCurrentIndex(event);
            if (tempIndex == -1) {
                if (contextMenu.isShowing()) {
                    contextMenu.hide();
                }
                return;
            }
            if (tempIndex != selectedIndex) {
                if (event.getButton() == MouseButton.PRIMARY && contextMenu.isShowing()) {
                    contextMenu.hide();
                }
                selectedIndex = tempIndex;
                EventBusUtil.getDefault().post(new PaintChooseEvent(selectedIndex, paintList.get(selectedIndex)));
                PaintPickerStage.getInstance().getPaintPicker().setPaintProperty(paintList.get(selectedIndex));
                refresh();
            }
            if (event.getButton() == MouseButton.SECONDARY) {
                contextMenu.show(LineGradientCanvas.this, event.getScreenX(), event.getScreenY());
            }
        });

        selectedIndex = 0;
        //EventBusUtil.getDefault().post(new PaintChooseEvent(selectedIndex, paintList.get(selectedIndex)));
        PaintPickerStage.getInstance().getPaintPicker().setPaintProperty(paintList.get(selectedIndex));
        PaintPickerStage.getInstance().getPaintPicker().setPaintProperty(paintList.get(selectedIndex));//need twice
        refresh();
    }

    private ContextMenu createContextMenu() {
        //copy java code
        MenuItem btnCopyJavaCode = new MenuItem(message("color.copyCode"), new Region());
        btnCopyJavaCode.getStyleClass().addAll("custom-menu-item", "copy-btn");
        btnCopyJavaCode.setOnAction(event -> {
            if (selectedIndex != -1) {
                OSUtil.writeToClipboard(PaintConvertUtil.convertPaintToJavaCode( paintList.get(selectedIndex)));
            }
        });
        //copy fx css
        MenuItem btnCopyFxCss = new MenuItem(message("color.copyCss"), new Region());
        btnCopyFxCss.getStyleClass().addAll("custom-menu-item", "copy-btn");
        btnCopyFxCss.setOnAction(event -> {
            if (selectedIndex != -1) {
                OSUtil.writeToClipboard(PaintConvertUtil.convertPaintToCss( paintList.get(selectedIndex)));
            }
        });
        //edit color
        MenuItem btnEdit = new MenuItem(message("color.edit"), new Region());
        btnEdit.getStyleClass().addAll("custom-menu-item", "edit-btn");
        btnEdit.setOnAction(event -> {
            if (selectedIndex != -1) {
                PaintPickerStage.getInstance().getPaintPicker().setPaintProperty(paintList.get(selectedIndex));
                PaintPickerStage.getInstance().showOnTop();
            }
        });
        return new ContextMenu(btnCopyJavaCode, btnCopyFxCss, btnEdit);
    }

    private int getCurrentIndex(MouseEvent event) {
        double ex = event.getX();
        double ey = event.getY();
        int tempIndex = -1;
        for (int i = 0; i < paintList.size(); i++) {
            if (ex >= 0 && ex <= CELL_W
                    && ey >= i * (CELL_H + SPACE_HEIGHT)
                    && ey <= i * (CELL_H + SPACE_HEIGHT) + CELL_H) {
                tempIndex = i;
                break;
            }
        }
        return tempIndex;
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
        refresh();
        EventBusUtil.getDefault().post(new PaintChooseEvent(selectedIndex, paintList.get(selectedIndex)));
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    private void refresh() {
        setWidth(CELL_W);
        setHeight(height);
        GraphicsContext g2d = getGraphicsContext2D();
        //清除
        g2d.setFill(Color.WHITE);
        g2d.clearRect(0, 0, CELL_W, height);

        g2d.setStroke(Color.WHITE);
        g2d.setLineWidth(3);
        //绘制
        for (int i = 0; i < paintList.size(); i++) {
            g2d.setFill(paintList.get(i));
            g2d.fillRect(0, i * (CELL_H + SPACE_HEIGHT), CELL_W, CELL_H);
            if (i == currentIndex) {
                g2d.strokeRect(3, i * (CELL_H + SPACE_HEIGHT) + 3, CELL_W - 6, CELL_H - 6);
            }
            if (selectedIndex == i) {
                g2d.drawImage(Constants.selectedImg, 104, 23+i * (CELL_H + SPACE_HEIGHT));
            }
        }
    }

    private LinearGradient getPaint(MouseEvent event) {
        double x = event.getX();
        double y = event.getY();
        for (int i = 0; i < paintList.size(); i++) {
            if (x >= 0 && x <= CELL_W
                    && y >= i * (CELL_H + SPACE_HEIGHT)
                    && y <= i * (CELL_H + SPACE_HEIGHT) + CELL_H) {
                return paintList.get(i);
            }
        }
        return null;
    }
}
