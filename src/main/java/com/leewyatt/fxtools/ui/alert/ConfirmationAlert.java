package com.leewyatt.fxtools.ui.alert;

import javafx.scene.control.ButtonType;

import java.util.Optional;

/**
 * @author LeeWyatt
 */
public class ConfirmationAlert extends BaseSimpleAlert {

    public ConfirmationAlert(String content) {
        super(AlertType.CONFIRMATION);
        setContentLabel(content);
        getDialogPane().getStylesheets().add("/css/confirmation-alert.css");

    }

    /**
     * @return true 确认; false 取消
     */
    public boolean showAndGetResult() {
        Optional<ButtonType> buttonType = showAndWait();
        boolean flag = false;
        if (buttonType.isPresent()) {
            if (buttonType.get() == ButtonType.OK) {
                flag = true;
            }
        }
        return flag;
    }
}
