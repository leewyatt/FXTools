package com.leewyatt.fxtools.ui.cells;

import com.leewyatt.fxtools.model.FontInfo;
import com.leewyatt.fxtools.utils.OSUtil;
import javafx.scene.control.Label;
import javafx.scene.control.TreeCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

/**
 * @author LeeWyatt
 */
public class FontTreeCell extends TreeCell<FontInfo> {

    private final Region region;
    private final Label label;
    private final HBox box;

    public FontTreeCell() {
        label = new Label();
        region = new Region();
        box = new HBox(3, region, label);
        region.setOnMouseReleased(event -> openFontFile());
    }

    private void openFontFile() {
        if (getItem() != null && getItem().getPath() != null) {
            OSUtil.openAndSelectedFile(getItem().getPath());
        }
    }
    @Override
    protected void updateItem(FontInfo item, boolean empty) {
        super.updateItem(item, empty);
        if (item == null || empty) {
            setGraphic(null);
        } else {
            if (item.getPath() != null) {
                region.getStyleClass().add("link-shape");
            }else {
                region.getStyleClass().clear();
            }
            label.setText(item.getName());
            setGraphic(box);
        }
    }


}
