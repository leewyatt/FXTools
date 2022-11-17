package com.leewyatt.fxtools.ui;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * @author LeeWyatt
 * 支持拖动的场景图
 */
public class DragScene extends Scene {
    private double offsetX, offsetY;
    private Stage stage;
    public DragScene(Parent root, Stage stage) {
        super(root);
        this.stage = stage;
    }


    public void setCanDrag() {
        // 鼠标按下时记录偏移量
        this.setOnMousePressed(event -> {
            offsetX = event.getSceneX();
            offsetY = event.getSceneY();
        });
        // 鼠标拖动时改变界面的位置
        this.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX()-offsetX);
            stage.setY(event.getScreenY()-offsetY);
        });
    }
}
