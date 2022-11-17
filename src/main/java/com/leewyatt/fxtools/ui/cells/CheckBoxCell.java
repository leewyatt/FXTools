package com.leewyatt.fxtools.ui.cells;

import com.leewyatt.fxtools.model.OSInfo;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListCell;

/**
 * @author LeeWyatt
 *
 */
public class CheckBoxCell extends ListCell<OSInfo> {

    public CheckBoxCell() {
        CheckBox checkBox = new CheckBox();
        //Labe
    }

    @Override
    protected void updateItem(OSInfo item, boolean empty) {
        super.updateItem(item, empty);
        if (item == null || empty) {
            setText("");
            setGraphic(null);
            return;
        }

    }
}
