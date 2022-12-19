package com.leewyatt.fxtools.ui.stages;

import com.leewyatt.fxtools.FXToolsApp;
import com.leewyatt.fxtools.event.EventBusUtil;
import com.leewyatt.fxtools.event.ScreenTaskEndEvent;
import com.leewyatt.fxtools.utils.FileUtil;
import com.leewyatt.fxtools.utils.ToolSettingsUtil;
import com.leewyatt.fxtools.utils.OSUtil;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.robot.Robot;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.coobird.thumbnailator.Thumbnails;
import org.controlsfx.control.SnapshotView;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.logging.Logger;

import static com.leewyatt.fxtools.utils.ResourcesUtil.message;

/**
 * @author LeeWyatt
 */
public class ScreenshotStage extends Stage {
    private final Pane rootPane;
    private final SnapshotView snapshotView;

    private final double screenScaleX;
    private final double screenScaleY;
    private final boolean hideMainStage;
    private final HBox controlsBox;
    private BufferedImage screenImg;
    private final WritableImage fxScreenImage;
    private boolean isSaving;
    private final Robot robot;
    private Label sizeLabel;
    private final Label tipsLabel;

    public ScreenshotStage(boolean hideMainStage) {
        this.initOwner(FXToolsApp.mainStage);
        this.hideMainStage = hideMainStage;
        if (hideMainStage) {
            FXToolsApp.mainStage.setIconified(true);
        }

        screenScaleX = Screen.getPrimary().getOutputScaleX();
        screenScaleY = Screen.getPrimary().getOutputScaleY();
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
        snapshotView = new SnapshotView(imageView);
        snapshotView.setSelectionBorderPaint(Color.rgb(18, 210, 105));
        snapshotView.setSelectionBorderWidth(1);
        controlsBox = createControlsBox();
        Region region = new Region();
        region.setPrefSize(13, 18);
        tipsLabel = new Label(message("screenshot.tips"), region);
        tipsLabel.getStyleClass().add("tips-label");
        rootPane = new Pane(snapshotView, controlsBox, tipsLabel);
        double realHeight = screenH / screenScaleY;

        showTipsLabel();
        snapshotView.setOnMouseMoved(event -> {
            if (!snapshotView.hasSelection()) {
                tipsLabel.setLayoutX(event.getSceneX() - tipsLabel.getWidth() / 2);
                tipsLabel.setLayoutY(event.getSceneY() + 25);
                tipsLabel.setVisible(true);
            } else {
                tipsLabel.setVisible(false);
            }
        });

        snapshotView.selectionProperty().addListener((ob, ov, nv) -> {
            if (nv == null) {
                sizeLabel.setText("0 x 0");
                controlsBox.setVisible(false);
                showTipsLabel();

            } else {
                controlsBox.setVisible(true);
                tipsLabel.setVisible(false);
                int width = (int) (snapshotView.getSelection().getWidth() * screenScaleX);
                int height = (int) (snapshotView.getSelection().getHeight() * screenScaleY);
                sizeLabel.setText(width + " x " + height);
                Rectangle2D rect = snapshotView.getSelection();
                double boxW = controlsBox.getWidth();
                double layoutX = rect.getMaxX() - boxW;
                double boxH = controlsBox.getHeight();
                double layoutY = rect.getMaxY() + 8;
                if (layoutX < 0) {
                    layoutX = 0;
                }
                if (layoutY > realHeight - boxH) {
                    layoutY = rect.getMinY() - boxH - 8;
                    if (layoutY <= 0) {
                        layoutY = rect.getMaxY() - boxH;
                    }
                }
                controlsBox.setLayoutX(layoutX);
                controlsBox.setLayoutY(layoutY);

            }
        });
        Scene scene = new Scene(rootPane, screenW, screenH);
        scene.getStylesheets().add(getClass().getResource("/css/screenshot-stage.css").toExternalForm());
        this.setScene(scene);
        scene.setFill(Color.TRANSPARENT);
        //scene.setCursor(new ImageCursor(new javafx.scene.image.Image(getClass().getResource("/images/color-cursor.png").toExternalForm())));
        this.initStyle(StageStyle.TRANSPARENT);
        rootPane.setVisible(false);

        //退出全屏, 结束截屏
        this.fullScreenProperty().addListener((ob, ov, nv) -> {
            if (!nv) {
                endScreenshot();
            }
        });

        //失去焦点,结束截屏
        this.focusedProperty().addListener((ob, ov, nv) -> {
            if (!nv && !isSaving) {
                endScreenshot();
            }
        });
        //右键按下, 结束截屏
        snapshotView.setOnMousePressed(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                endScreenshot();
            }
        });
    }

    private void showTipsLabel() {
        Point2D location = robot.getMousePosition();
        tipsLabel.setLayoutX(location.getX() - tipsLabel.getWidth() / 2);
        tipsLabel.setLayoutY(location.getY() + 25);
        tipsLabel.setVisible(true);
    }

    private HBox createControlsBox() {
        //退出按钮
        Button exitBtn = new Button("", new Region());
        exitBtn.getStyleClass().addAll("region-btn", "exit-btn");
        exitBtn.setOnAction(event -> {
            endScreenshot();
        });

        //复制按钮
        Button copyImageBtn = new Button("", new Region());
        copyImageBtn.getStyleClass().addAll("region-btn", "copy-btn");
        copyImageBtn.setOnAction(event -> {
            if (snapshotView.hasSelection() && snapshotView.getSelection().getWidth() > 1 && snapshotView.getSelection().getHeight() > 1) {
                copyScreenshot();
            }
        });
        //保存按钮
        Button saveBtn = new Button("", new Region());
        saveBtn.getStyleClass().addAll("region-btn", "save-btn");
        saveBtn.setOnAction(event -> {
            if (snapshotView.hasSelection() && snapshotView.getSelection().getWidth() > 1 && snapshotView.getSelection().getHeight() > 1) {
                saveScreenshot();
            }
        });

        Region region = new Region();
        region.setPrefSize(15, 15);
        region.setMinSize(15, 15);
        sizeLabel = new Label("0 x 0", region);
        sizeLabel.setPrefWidth(116);
        sizeLabel.getStyleClass().add("size-label");
        sizeLabel.setTextAlignment(TextAlignment.CENTER);
        sizeLabel.setAlignment(Pos.CENTER);
        HBox box = new HBox(8, sizeLabel, copyImageBtn, saveBtn, exitBtn);
        box.setAlignment(Pos.CENTER);
        box.getStyleClass().add("btns-box");
        box.setVisible(false);
        return box;
    }

    private void saveScreenshot() {
        isSaving = true;
        Rectangle2D selection = snapshotView.getSelection();
        controlsBox.setVisible(false);
        FileChooser fileChooser = new FileChooser();
        if (FileUtil.getInstance().getLastScreenshotSaveLocation() != null) {
            fileChooser.setInitialDirectory(new File(FileUtil.getInstance().getLastScreenshotSaveLocation()));
        }
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        fileChooser.setInitialFileName("screenshot_" + dtf.format(LocalDateTime.now()));
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image File", "*.jpg", "*.png", "*.bmp", "gif", "*.webp"));
        File outFile = fileChooser.showSaveDialog(this);
        if (outFile == null) {
            isSaving = false;
            controlsBox.setVisible(true);
            return;
        }

        BufferedImage bufferedImage = getScreenBufImg().getSubimage((int) (selection.getMinX() * screenScaleX), (int) (selection.getMinY() * screenScaleY),
                (int) (selection.getWidth() * screenScaleX), (int) (selection.getHeight() * screenScaleY));

        FileUtil.getInstance().setLastScreenshotSaveLocation(outFile.getParent()) ;
        boolean flag = false;
        String[] supportedFormats = {".png", ".jpg", ".bmp", "gif", ".webp"};
        String name = outFile.getName().toLowerCase(Locale.ROOT);
        for (String supportedFormat : supportedFormats) {
            if (name.endsWith(supportedFormat)) {
                flag = true;
                break;
            }
        }
        //如果保存的文件格式不对,那么就加个后缀
        if (!flag) {
            File realExportDir;
            int x = 0;
            do {
                realExportDir = new File(outFile.getParent() + File.separator + outFile.getName() + (x == 0 ? "" : "(" + x + ")") + "." + "jpg");
                x++;
            } while (realExportDir.exists());
            outFile = realExportDir;
        }
        try {
            Thumbnails.of(bufferedImage)
                    .scale(1.0)
                    .outputQuality(1.0f)
                    .toFile(outFile);
            if (ToolSettingsUtil.getInstance().getScreenshotOpenImg()) {
                OSUtil.showDoc(outFile.getAbsolutePath());
            }
            if (ToolSettingsUtil.getInstance().getScreenshotOpenFile()) {
                OSUtil.openAndSelectedFile(outFile);
            }
        } catch (IOException e) {
            Logger logger = Logger.getLogger("com.leewyatt.fxtools.ui.stages.ScreenshotStage");
            logger.severe("IOException: saveScreenshot failed.\t"+e);
            throw new RuntimeException(e);
        } finally {
            isSaving = false;
            endScreenshot();
        }

    }

    private void copyScreenshot() {
        Rectangle2D selection = snapshotView.getSelection();
        controlsBox.setVisible(false);
        //如果有空实现了 划线, 画矩形 等功能, 那么就需要再次截屏
        int w = (int) (selection.getWidth() * screenScaleX);
        int h = (int) (selection.getHeight() * screenScaleY);
        int startX = (int) (selection.getMinX() * screenScaleX);
        int startY = (int) (selection.getMinY() * screenScaleY);
        BufferedImage subImage = getScreenBufImg().getSubimage(startX, startY, w, h);
        try {
            BufferedImage result = Thumbnails.of(subImage)
                    .scale(1.0)
                    .outputQuality(1.0f)
                    .asBufferedImage();
            OSUtil.writeToClipboard(SwingFXUtils.toFXImage(result, null));
        } catch (IOException e) {
            Logger logger = Logger.getLogger("com.leewyatt.fxtools.ui.stages.ScreenshotStage");
            logger.severe("IOException: copyScreenshot failed.\t"+e);
            throw new RuntimeException(e);
        }
        endScreenshot();
    }

    private BufferedImage getScreenBufImg() {
        if (screenImg == null) {
            screenImg = SwingFXUtils.fromFXImage(fxScreenImage, null);
        }
        return screenImg;
    }

    /**
     * 结束截屏
     */
    private void endScreenshot() {
        rootPane.setVisible(false);
        if (hideMainStage) {
            FXToolsApp.mainStage.setIconified(false);
        }
        EventBusUtil.getDefault().post(new ScreenTaskEndEvent());
        this.hide();
    }

    public void showStage() {
        rootPane.setVisible(true);
        this.show();
        this.toFront();

    }
}
