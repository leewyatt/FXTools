package com.leewyatt.fxtools.ui.alert;

/**
 * @author LeeWyatt
 */
public class InformationAlert extends BaseSimpleAlert {

    public InformationAlert(String content) {
        super(AlertType.INFORMATION);
        setContentLabel(content);
        getDialogPane().getStylesheets().add("/css/information-alert.css");
    }

}
