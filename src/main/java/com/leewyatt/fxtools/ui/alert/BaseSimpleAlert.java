package com.leewyatt.fxtools.ui.alert;

import com.leewyatt.fxtools.FXToolsApp;
import com.leewyatt.fxtools.utils.ToolSettingsUtil;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.stage.StageStyle;

/**
 * @author LeeWyatt
 */
public abstract class BaseSimpleAlert extends Alert {

    public BaseSimpleAlert(AlertType alertType) {
        super(alertType);
        initStyle(StageStyle.TRANSPARENT);
        String skinStyle = ToolSettingsUtil.getInstance().getSkin();
        getDialogPane().getStylesheets().add("/css/custom-alert-"+skinStyle+".css");
        getDialogPane().setPrefWidth(316);
        Scene scene = getDialogPane().getScene();
        scene.setFill(Color.TRANSPARENT);
        setHeaderText(null);
        setGraphic(null);

        initOwner(FXToolsApp.mainStage);
    }

    public void setContentNode(Node node) {
        getDialogPane().setContent(node);
    }


    public void setContentLabel(String content) {
        Region region = new Region();
        region.setPrefSize(18, 18);
        Label contentLabel = new Label(content, region);
        contentLabel.getStyleClass().add("content-label");
        contentLabel.setWrapText(true);
        contentLabel.setMinHeight(Region.USE_PREF_SIZE);
        contentLabel.setPrefHeight(Region.USE_COMPUTED_SIZE);
        contentLabel.setPrefHeight(80);
        contentLabel.setAlignment(Pos.TOP_CENTER);
        setContentNode(contentLabel);

    }

}
