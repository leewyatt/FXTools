package com.leewyatt.fxtools.ui.canvas;

import com.google.common.eventbus.Subscribe;
import com.leewyatt.fxtools.FXToolsApp;
import com.leewyatt.fxtools.event.EventBusUtil;
import com.leewyatt.fxtools.event.PaintChooseEvent;
import com.leewyatt.fxtools.ui.paintpicker.PaintPickerStage;
import com.leewyatt.fxtools.utils.Constants;
import com.leewyatt.fxtools.utils.OSUtil;
import com.leewyatt.fxtools.utils.PaintConvertUtil;
import com.leewyatt.fxtools.utils.ToolSettingsUtil;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

import static com.leewyatt.fxtools.utils.ResourcesUtil.message;

/**
 * @author LeeWyatt
 */
public class ColorHorCanvas extends Canvas {

    public static final double CELL_SIZE = 35;
    private static final int SPACE_HEIGHT = 14;
    private final List<Color> colorList = new ArrayList<>();
    private final ContextMenu contextMenu;
    private String[] webColors;
    private int currentIndex = -1;

    private int selectedIndex = -1;

    public ColorHorCanvas() {
        EventBusUtil.getDefault().register(this);
        contextMenu = createContextMenu();
        setOnMouseMoved(event -> {
            int tempIndex = getPositionColorIndex(event);
            if (tempIndex != currentIndex) {
                currentIndex = tempIndex;
                refresh();
            }
        });

        setOnMouseClicked(event -> {
            int tempIndex = getPositionColorIndex(event);
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
                Color color = colorList.get(getPositionColorIndex(event));
                EventBusUtil.getDefault().post(new PaintChooseEvent(selectedIndex, color));
                PaintPickerStage.getInstance().getPaintPicker().setPaintProperty(color);
                refresh();
            }
            if (event.getButton() == MouseButton.SECONDARY) {
                contextMenu.show(ColorHorCanvas.this, event.getScreenX(), event.getScreenY());
            }

        });
    }

    private ContextMenu createContextMenu() {
        //copy java code
        MenuItem btnCopyJavaCode = new MenuItem(message("color.copyCode"), new Region());
        btnCopyJavaCode.getStyleClass().addAll("custom-menu-item", "copy-btn");
        btnCopyJavaCode.setOnAction(event -> {
            if (selectedIndex != -1) {
                OSUtil.writeToClipboard(PaintConvertUtil.convertPaintToJavaCode( colorList.get(selectedIndex)));
            }
        });
        //copy fx css
        MenuItem btnCopyFxCss = new MenuItem(message("color.copyCss"), new Region());
        btnCopyFxCss.getStyleClass().addAll("custom-menu-item", "copy-btn");
        btnCopyFxCss.setOnAction(event -> {
            if (selectedIndex != -1) {
                    OSUtil.writeToClipboard(PaintConvertUtil.convertPaintToCss( colorList.get(selectedIndex)));
            }
        });
        //edit color
        MenuItem btnEdit = new MenuItem(message("color.edit"), new Region());
        btnEdit.getStyleClass().addAll("custom-menu-item", "edit-btn");
        btnEdit.setOnAction(event -> {
            if (selectedIndex != -1) {
                PaintPickerStage.getInstance().getPaintPicker().setPaintProperty(colorList.get(selectedIndex));
                PaintPickerStage.getInstance().showOnTop();
            }
        });
        return new ContextMenu(btnCopyJavaCode, btnCopyFxCss, btnEdit);
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
        refresh();
        EventBusUtil.getDefault().post(new PaintChooseEvent(selectedIndex, colorList.get(selectedIndex)));
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    @Subscribe
    public void setWebColors(String[] webColors) {
        boolean containsNullValue = false;
        currentIndex = -1;
        if (webColors == null) {
            webColors = new String[0];
        }
        this.webColors = webColors;
        colorList.clear();
        for (String webColor : webColors) {
            if (webColor == null && !containsNullValue) {
                containsNullValue = true;
            }
            colorList.add(webColor == null ? null : Color.web(webColor));
        }
        // 默认选择第一个
        selectedIndex = 0;
        //EventBusUtil.getDefault().post(new PaintChooseEvent(selectedIndex, colorList.get(0)));
        refresh();
    }

    private int getPositionColorIndex(MouseEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();

        int size = colorList.size();
        int whiteCell = 0;
        int max = (int) (Math.ceil(size * 1.0 / 6) * 7);
        for (int i = 0; i < max; i++) {
            int row = i / 7;
            int col = i % 7;
            if (col == 3) {
                whiteCell++;
            } else {
                int index = i - whiteCell;
                if (index >= size) {
                    break;
                }
                if (x >= col * CELL_SIZE && x <= (col + 1) * CELL_SIZE && y >= row * (CELL_SIZE + SPACE_HEIGHT) && y <= row * (CELL_SIZE + SPACE_HEIGHT) + CELL_SIZE) {
                    return index;
                }
            }
        }
        return -1;
    }

    private Color getPositionColor(MouseEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        int size = colorList.size();
        int whiteCell = 0;
        int max = (int) (Math.ceil(size * 1.0 / 6) * 7);
        for (int i = 0; i < max; i++) {
            int row = i / 7;
            int col = i % 7;
            if (col == 3) {
                whiteCell++;
            } else {
                int index = i - whiteCell;
                if (index >= size) {
                    break;
                }
                if (x >= col * CELL_SIZE && x <= (col + 1) * CELL_SIZE && y >= row * (CELL_SIZE + SPACE_HEIGHT) && y <= row * (CELL_SIZE + SPACE_HEIGHT) + CELL_SIZE) {
                    return colorList.get(index);
                }
            }
        }
        return null;
    }

    private void refresh() {
        GraphicsContext g2d = getGraphicsContext2D();
        g2d.setFill(Color.WHITE);
        //g2d.fillRect(0, 0, getWidth(), getHeight());
        g2d.clearRect(0, 0, getWidth(), getHeight());

        setWidth(CELL_SIZE * 7);
        setHeight((int) ((CELL_SIZE + SPACE_HEIGHT) * Math.ceil(colorList.size() * 1.0 / 6)));
        g2d.setFont(FXToolsApp.BASIC_FONT);

        int size = colorList.size();
        int whiteCell = 0;
        int max = (int) (Math.ceil(size * 1.0 / 6) * 7);
        for (int i = 0; i < max; i++) {
            int row = i / 7;
            int col = i % 7;
            if (col == 3) {
                whiteCell++;
            } else {
                int index = i - whiteCell;
                if (index >= size) {
                    break;
                }
                Color c = colorList.get(index);
                if (c == null) {
                    continue;
                }
                g2d.setFill(c);
                g2d.fillRect(col * CELL_SIZE, row * (CELL_SIZE + SPACE_HEIGHT), CELL_SIZE, CELL_SIZE);
                if (currentIndex == index) {
                    g2d.setFill(Color.BLACK);
                    String skin = ToolSettingsUtil.getInstance().getSkin().toLowerCase();
                    g2d.setFill("dark".equalsIgnoreCase(skin) ? Color.WHITE : Color.BLACK);
                    if (col == 0 || col == 4) {
                        g2d.fillText(webColors[i - whiteCell], col * CELL_SIZE, (row + 1) * CELL_SIZE + SPACE_HEIGHT * row + 10);
                    } else if (col == 1 || col == 5) {
                        g2d.fillText(webColors[i - whiteCell], col * CELL_SIZE - 5, (row + 1) * CELL_SIZE + SPACE_HEIGHT * row + 10);
                    } else {
                        g2d.fillText(webColors[i - whiteCell], col * CELL_SIZE - 18, (row + 1) * CELL_SIZE + SPACE_HEIGHT * row + 10);
                    }
                }

                if (selectedIndex == index) {
                    g2d.drawImage(Constants.selectedImg, col * CELL_SIZE, row * (CELL_SIZE + SPACE_HEIGHT));
                }
            }
        }
    }
}
