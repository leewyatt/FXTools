package com.leewyatt.fxtools.ui.stages;

import com.leewyatt.fxtools.ui.paintpicker.PaintPickerStage;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.SVGPath;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;

import static com.leewyatt.fxtools.utils.ResourcesUtil.message;

/**
 * @author LeeWyatt
 */
public class FullSvgStage extends Stage {
    private final SVGPath svgPathNode = new SVGPath();

    private FullSvgStage() {
        Label label = new Label(message("svgStage.label"));
        label.setAlignment(Pos.CENTER);
        label.setTextFill(Color.WHITE);
        label.setStyle("-fx-background-color: gray");
        StackPane root = new StackPane(svgPathNode,label);
        root.setStyle("-fx-background-color: white");
        label.prefWidthProperty().bind(root.widthProperty());
        StackPane.setAlignment(label, Pos.TOP_CENTER);
        root.getStyleClass().add("svg-path-pane");
        root.setMinSize(500, 500);
        Scene scene = new Scene(root,Color.WHITE);
        setScene(scene);
        setTitle("JavaFX SVG View");
        initModality(Modality.APPLICATION_MODAL);
        //setAlwaysOnTop(true);
        URL resource = getClass().getResource("/css/svgPane.css");
        if (resource != null) {
            scene.getStylesheets().add(resource.toExternalForm());
        }

        root.setOnScroll(event -> {
            double value;
            if (event.getDeltaY() > 0) {
                value = svgPathNode.getScaleX() * 1.1;
            } else {
                value = svgPathNode.getScaleX() * 0.9;
            }
            svgPathNode.setScaleX(value);
            svgPathNode.setScaleY(value);
        });


        PaintPickerStage.getInstance().getPaintPicker().paintProperty().addListener((ob, ov, nv) -> {
            svgPathNode.setFill(nv);
        });
        setFullScreen(true);
    }



    public void setSvgContent(String svgContent, Paint paint,double scale) {
        svgPathNode.setScaleX(scale);
        svgPathNode.setScaleY(scale);
        svgPathNode.setContent(svgContent);
        svgPathNode.setFill(paint);
    }

    public SVGPath getSvgPathNode() {
        return svgPathNode;
    }

    @Override
    public void showAndWait() {
        if (!isFullScreen()) {
            setFullScreen(true);
        }
        super.showAndWait();
    }

    private static volatile FullSvgStage singleton;

    public static FullSvgStage getInstance() {
        if (singleton == null) {
            synchronized (FullSvgStage.class) {
                if (singleton == null) {
                    singleton = new FullSvgStage();
                }
            }
        }
        return singleton;
    }

}
