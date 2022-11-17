package com.leewyatt.fxtools.ui.stages;

import com.leewyatt.fxtools.FXToolsApp;
import com.leewyatt.fxtools.event.EventBusUtil;
import com.leewyatt.fxtools.event.ScreenTaskEndEvent;
import com.leewyatt.fxtools.utils.PaintConvertUtil;
import com.leewyatt.fxtools.utils.OSUtil;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.robot.Robot;
import javafx.scene.shape.Line;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * @author LeeWyatt
 */
public class ScreenColorPickerStage extends Stage {
    private final Pane rootPane;

    private final double screenScaleX;
    private final double screenScaleY;
    private final Pane controlsBox;
    private final WritableImage fxScreenImage;
    private final Robot robot;
    private final double fxScreenWidth;
    private final double fxScreenHeight;
    private ImageView previewImg;

    private final int previewSize = 60;
    private final double previewScale = 2;

    public ScreenColorPickerStage() {
        this.initOwner(FXToolsApp.mainStage);
        screenScaleX = Screen.getPrimary().getOutputScaleX();
        screenScaleY = Screen.getPrimary().getOutputScaleY();
        fxScreenWidth = Screen.getPrimary().getBounds().getWidth();
        fxScreenHeight = Screen.getPrimary().getBounds().getHeight();
        Rectangle2D bounds = Screen.getPrimary().getBounds();
        int screenW = (int) (bounds.getWidth() * screenScaleX);
        int screenH = (int) (bounds.getHeight() * screenScaleY);

        robot = new Robot();
        fxScreenImage = robot.getScreenCapture(null, new Rectangle2D(0, 0, screenW, screenH), false);
        ImageView imageView = new ImageView();
        imageView.setFitWidth(screenW);
        imageView.setFitHeight(screenH);
        imageView.setImage(fxScreenImage);

        this.setAlwaysOnTop(true);
        this.setFullScreen(true);
        this.setFullScreenExitHint("");

        controlsBox = createPreviewPane();
        rootPane = new Pane(imageView, controlsBox);
        rootPane.setMouseTransparent(false);

        showPreviewPane(robot.getMouseX(), robot.getMouseY());
        controlsBox.setVisible(true);
        rootPane.setOnMouseMoved(event -> {
            showPreviewPane(event.getX(), event.getY());
            event.consume();
        });
        rootPane.setOnMouseClicked(event -> {
            Color color = fxScreenImage.getPixelReader().getColor((int) (event.getX() * screenScaleX), (int) (event.getY() * screenScaleY));
            OSUtil.writeToClipboard(PaintConvertUtil.convertPaintToCss(color));
            EventBusUtil.getDefault().post(color);
            endPickColor();
        });

        Scene scene = new Scene(rootPane, screenW, screenH);
        scene.getStylesheets().add(getClass().getResource("/css/screenshot-stage-light.css").toExternalForm());
        this.setScene(scene);
        scene.setFill(Color.TRANSPARENT);
        //scene.setCursor(new ImageCursor(new javafx.scene.image.Image(getClass().getResource("/images/color-cursor.png").toExternalForm())));
        this.initStyle(StageStyle.TRANSPARENT);
        //rootPane.setVisible(false);

        //退出全屏, 结束截屏
        //this.fullScreenProperty().addListener((ob, ov, nv) -> {
        //    if (!nv) {
        //        endPickColor();
        //    }
        //});

        //失去焦点,结束截屏
        this.focusedProperty().addListener((ob, ov, nv) -> {
            if (!nv) {
                endPickColor();
            }
        });

    }

    private Pane createPreviewPane() {
        previewImg = new ImageView();
        previewImg.setImage(fxScreenImage);
        previewImg.setFitHeight(previewSize);
        previewImg.setFitWidth(previewSize);
        Line hr = new Line(0, previewSize / 2.0, previewSize, previewSize / 2.0);
        Line vr = new Line(previewSize / 2.0, 0, previewSize / 2.0, previewSize);
        StackPane stackPane = new StackPane(previewImg, hr, vr);
        stackPane.getStyleClass().add("preview-pane");
        stackPane.setScaleX(previewScale);
        stackPane.setScaleY(previewScale);
        stackPane.setVisible(false);
        return stackPane;
    }

    private void endPickColor() {
        rootPane.setVisible(false);
        setAlwaysOnTop(false);
        EventBusUtil.getDefault().post(new ScreenTaskEndEvent());
        this.hide();
    }

    public void showStage() {
        this.show();
        this.toFront();
        rootPane.setVisible(true);
    }

    private void showPreviewPane(double x, double y) {
        previewImg.setViewport(new Rectangle2D(x * screenScaleX - previewSize / 2.0, y * screenScaleY - previewSize / 2.0, previewSize, previewSize));
        double boxW = controlsBox.getWidth() * previewScale;
        double layoutX = x + 50;
        double boxH = controlsBox.getHeight() * previewScale;
        double layoutY = y + 50;
        if (layoutX > fxScreenWidth - boxW) {
            layoutX = x - 100;
        }
        if (layoutY > fxScreenHeight - boxH) {
            layoutY = y - 100;
        }
        controlsBox.setLayoutX(layoutX);
        controlsBox.setLayoutY(layoutY);
    }
}
