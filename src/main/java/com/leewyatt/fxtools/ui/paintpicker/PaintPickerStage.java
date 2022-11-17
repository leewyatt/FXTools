package com.leewyatt.fxtools.ui.paintpicker;

import com.google.common.eventbus.Subscribe;
import com.leewyatt.fxtools.event.EventBusUtil;
import com.leewyatt.fxtools.event.SkinChangedEvent;
import com.leewyatt.fxtools.utils.ResourcesUtil;
import com.leewyatt.fxtools.utils.ToolSettingsUtil;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

/**
 * @author LeeWyatt
 */
public class PaintPickerStage extends Stage {

    private final PaintPicker paintPicker = new PaintPicker(new PaintPicker.Delegate() {
        @Override
        public void handleError(String s, Object... objects) {
            System.out.println(s);
        }
    });
    private Scene scene;

    public PaintPicker getPaintPicker() {
        return paintPicker;
    }

    private PaintPickerStage(Paint paint) {
        EventBusUtil.getDefault().register(this);
        if (paint != null) {
            paintPicker.setPaintProperty(paint);
        }
        BorderPane rootPane = new BorderPane();
        rootPane.getStyleClass().add("paint-root-pane");
        rootPane.setPadding(new Insets(6));
        rootPane.setCenter(paintPicker);

       getIcons().addAll(ResourcesUtil.getIconImages());

        scene = new Scene(rootPane);
        String skinStyle = ToolSettingsUtil.getInstance().getSkin();
        scene.getStylesheets().add(getClass().getResource("/css/picker-stage-"+ skinStyle +".css").toExternalForm());

        this.setScene(scene);
        this.setTitle("Paint Picker SceneBuilder");
        //this.initModality(Modality.APPLICATION_MODAL);
        this.setResizable(false);
        this.setAlwaysOnTop(true);
    }

    private PaintPickerStage() {
        this(null);
    }

    public void showOnTop() {
        if (isIconified()) {
            setIconified(false);
        }
        toFront();
        show();
    }

    private static volatile PaintPickerStage singleton;

    public static PaintPickerStage getInstance() {
        if (singleton == null) {
            synchronized (PaintPickerStage.class) {
                if (singleton == null) {
                    singleton = new PaintPickerStage();
                }
            }
        }
        return singleton;
    }

    @Subscribe
    public void skinChangedHandler(SkinChangedEvent event) {
        String skinStyle = ToolSettingsUtil.getInstance().getSkin();
        scene.getStylesheets().setAll(getClass().getResource("/css/picker-stage-"+ skinStyle +".css").toExternalForm());
    }
}
