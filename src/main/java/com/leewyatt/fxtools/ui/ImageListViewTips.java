package com.leewyatt.fxtools.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import static com.leewyatt.fxtools.utils.ResourcesUtil.message;

/**
 * @author LeeWyatt
 */
public class ImageListViewTips extends VBox {

    public ImageListViewTips() {
        super(6);
        Label label = new Label(message("tips.instructions"));
        label.getStyleClass().add("xl-font");
        StackPane usePane = new StackPane(label);
        Label backLabel = createLabel(message("tips.back"),"back-btn-demo");
        Label addLabel = createLabel(message("tips.add"),"add-image-btn-demo");
        Label dragLabel = createLabel(message("tips.drag"),"drag-image-btn-demo");
        Label deleteLabel = createLabel(message("tips.delete"),"delete-btn-demo");
        Label clearLabel = createLabel(message("tips.clear"),"clear-btn-demo");
        Label handleLabel = createLabel(message("tips.handler"),"handler-btn-demo");
        setAlignment(Pos.CENTER_LEFT);
        getChildren().addAll(usePane,backLabel, addLabel, dragLabel,deleteLabel,clearLabel,handleLabel);
        getStyleClass().add("image-tips-pane");
    }

    public Label createLabel(String text, String cssName) {
        Region region = new Region();
        StackPane sp = new StackPane(region);
        sp.getStyleClass().add("wrapper");
        Label backLabel = new Label(text, sp);
        backLabel.getStyleClass().addAll("m-font","btn-demo",cssName);
        backLabel.setWrapText(true);
        return backLabel;
    }
}
