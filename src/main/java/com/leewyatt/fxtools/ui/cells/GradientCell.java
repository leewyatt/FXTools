package com.leewyatt.fxtools.ui.cells;

import com.leewyatt.rxcontrols.utils.StyleUtil;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;

/**
 * @author LeeWyatt
 */
public class GradientCell extends ListCell<String> {
    public static final String[] CLASS_NAMES = {"color-cell-separator", "color-cell"};
    Label label;

    public GradientCell() {
        label = new Label();
    }
    @Override
    protected void updateItem(String s, boolean b) {
        super.updateItem(s, b);
        if (s == null || b) {
            setGraphic(null);
            return;
        }
        label.setText(s);
        if (s.endsWith(" ")) {
            StyleUtil.toggleClass(this, CLASS_NAMES,"color-cell-separator");
        } else {
            StyleUtil.toggleClass(this, CLASS_NAMES,"color-cell");
        }
        setGraphic(label);
    }
}
