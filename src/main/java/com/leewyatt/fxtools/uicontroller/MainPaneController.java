package com.leewyatt.fxtools.uicontroller;

import com.google.common.eventbus.Subscribe;
import com.leewyatt.fxtools.FXToolsApp;
import com.leewyatt.fxtools.event.EventBusUtil;
import com.leewyatt.fxtools.event.ScreenTaskEndEvent;
import com.leewyatt.fxtools.event.ShowColorPickerEvent;
import com.leewyatt.fxtools.event.SkinChangedEvent;
import com.leewyatt.fxtools.ui.alert.AboutAlert;
import com.leewyatt.fxtools.ui.alert.DonateAlert;
import com.leewyatt.fxtools.ui.stages.ScreenColorPickerStage;
import com.leewyatt.fxtools.ui.stages.ScreenshotStage;
import com.leewyatt.fxtools.utils.Constants;
import com.leewyatt.fxtools.utils.OSUtil;
import com.leewyatt.fxtools.utils.ResourcesUtil;
import com.leewyatt.fxtools.utils.ToolSettingsUtil;
import com.leewyatt.rxcontrols.animation.carousel.AnimHorMove;
import com.leewyatt.rxcontrols.controls.RXCarousel;
import com.leewyatt.rxcontrols.controls.RXToggleButton;
import com.leewyatt.rxcontrols.enums.DisplayMode;
import com.leewyatt.rxcontrols.pane.RXCarouselPane;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static com.leewyatt.fxtools.utils.ResourcesUtil.message;

/**
 * @author LeeWyatt
 */
public class MainPaneController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private RXCarousel carousel;
    @FXML
    private BorderPane contentPane;
    @FXML
    private Button donateBtn;
    @FXML
    private ToggleGroup menuGroup;
    @FXML
    private ImageView skinImageView;

    @FXML
    private Button snapshotBtn;

    @FXML
    private Button colorPickerBtn;

    @FXML
    private ToggleButton toggleBtn;

    @FXML
    private ToggleButton alwaysTopBtn;

    @FXML
    private ComboBox<String> versionCombobox;

    private Stage stage;

    public MainPaneController() {
        EventBusUtil.getDefault().register(this);
    }

    @FXML
    void initialize() throws IOException {
        Font basicFont = FXToolsApp.BASIC_FONT;
        Font font14 = Font.font(basicFont.getFamily(), 14);
        Tooltip pickColorTooltip = new Tooltip(message("pickColor") + ": ctrl+shift+c");
        pickColorTooltip.setFont(font14);
        colorPickerBtn.setTooltip(pickColorTooltip);
        Tooltip screenshotToolTip = new Tooltip(message("screenshot") + ": ctrl+shift+x");
        screenshotToolTip.setFont(font14);
        snapshotBtn.setTooltip(screenshotToolTip);
        ToolSettingsUtil settingsUtil = ToolSettingsUtil.getInstance();
        toggleBtn.setSelected(!"light".equalsIgnoreCase(settingsUtil.getSkin()));
        alwaysTopBtn.setSelected(settingsUtil.getAlwaysTop());
        alwaysTopBtn.selectedProperty().addListener((ob, ov, nv) -> {
            stage.setAlwaysOnTop(nv);
            settingsUtil.saveAlwaysTop(nv);
        });
        ObservableList<String> versionList = FXCollections.observableArrayList("8");
        for (int i = 11; i <= 30; i++) {
            versionList.add(i + "");
        }
        versionCombobox.setItems(versionList);
        versionCombobox.getSelectionModel().select(ToolSettingsUtil.getInstance().getDocVersion());
        versionCombobox.getSelectionModel().selectedItemProperty().addListener((ob, ov, nv) -> {
            ToolSettingsUtil.getInstance().saveDocVersion(nv);
        });

        carousel.setAutoSwitch(false);
        carousel.setHoverPause(false);
        carousel.setCarouselAnimation(new AnimHorMove(true));

        carousel.setAnimationTime(Duration.millis(128));
        carousel.setArrowDisplayMode(DisplayMode.HIDE);
        carousel.setNavDisplayMode(DisplayMode.HIDE);
        RXCarouselPane imagePage = (RXCarouselPane) ResourcesUtil.loadFXML("image-page");
        carousel.getPaneList().add(imagePage);
        RXCarouselPane colorPage = (RXCarouselPane) ResourcesUtil.loadFXML("color-page");
        carousel.getPaneList().add(colorPage);
        RXCarouselPane svgPage = (RXCarouselPane) ResourcesUtil.loadFXML("svg-page");
        carousel.getPaneList().add(svgPage);
        RXCarouselPane fontPage = (RXCarouselPane) ResourcesUtil.loadFXML("font-page");
        carousel.getPaneList().add(fontPage);
        RXCarouselPane tutorialPage = (RXCarouselPane) ResourcesUtil.loadFXML("tutorial-page");
        carousel.getPaneList().add(tutorialPage);
        RXCarouselPane settingsPage = (RXCarouselPane) ResourcesUtil.loadFXML("settings-page");
        carousel.getPaneList().add(settingsPage);

        ObservableList<Toggle> menuBtns = menuGroup.getToggles();
        int size = menuBtns.size();
        for (int i = 0; i < size; i++) {
            int index = i;
            RXToggleButton btn = (RXToggleButton) menuBtns.get(i);
            btn.setOnAction(event -> carousel.setSelectedIndex(index));
        }
        menuGroup.selectedToggleProperty().addListener((ob, ov, nv) -> {
            if (nv == null) {
                return;
            }
            carousel.setSelectedIndex(menuGroup.getToggles().indexOf(nv));
        });
        if (OSUtil.getOS() != OSUtil.OS.WINDOWS) {
            snapshotBtn.setManaged(false);
            colorPickerBtn.setManaged(false);
            snapshotBtn.setVisible(false);
            colorPickerBtn.setVisible(false);
        }
    }

    @FXML
    void onClickCloseBtn(MouseEvent event) {
        Platform.exit();
        System.exit(0);
    }

    @FXML
    void onClickMiniBtn(MouseEvent event) {
        stage.setIconified(true);
    }

    public void setStage(Stage stage) {
        this.stage = stage;
        KeyCodeCombination kc1 = new KeyCodeCombination(KeyCode.X, KeyCombination.CONTROL_DOWN, KeyCodeCombination.SHIFT_DOWN);
        stage.getScene().getAccelerators().put(kc1, () -> snapshotBtn.fire());
        KeyCodeCombination kc2 = new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN, KeyCodeCombination.SHIFT_DOWN);
        stage.getScene().getAccelerators().put(kc2, () -> colorPickerBtn.fire());
    }


    @FXML
    void showApiAction(ActionEvent event) {
        String linkUrl = "";
        String jdkVersion = versionCombobox.getSelectionModel().getSelectedItem();
        if ("8".equals(jdkVersion)) {
            linkUrl = Constants.api8;
        } else {
            linkUrl = Constants.openjfxPrefix + jdkVersion;
        }
        OSUtil.showDoc(linkUrl);
    }

    @FXML
    void showCssAction(ActionEvent event) {
        String linkUrl = "";
        String jdkVersion = versionCombobox.getSelectionModel().getSelectedItem();
        if ("8".equals(jdkVersion)) {
            linkUrl = Constants.css8;
        } else {
            linkUrl = Constants.openjfxPrefix + jdkVersion + Constants.CssDocSuffix;
        }
        OSUtil.showDoc(linkUrl);
    }

    @FXML
    void showFXMLAction(ActionEvent event) {
        String linkUrl = "";
        String jdkVersion = versionCombobox.getSelectionModel().getSelectedItem();
        if ("8".equals(jdkVersion)) {
            linkUrl = Constants.fxml8;
        } else {
            linkUrl = Constants.openjfxPrefix + jdkVersion + Constants.fxmlDocSuffix;
        }
        OSUtil.showDoc(linkUrl);
    }

    @FXML
    void onClickIvBtn(ActionEvent event) {
        toggleBtn.setDisable(true);
        Scene scene = FXToolsApp.mainStage.getScene();
        skinImageView.setFitWidth(scene.getWidth());
        skinImageView.setFitHeight(scene.getHeight());
        skinImageView.setVisible(false);
        skinImageView.setImage(scene.snapshot(scene.snapshot(null)));
        String skinStyle = toggleBtn.isSelected() ? "dark" : "light";
        scene.getStylesheets().setAll(getClass().getResource("/css/main-stage-" + skinStyle + ".css").toExternalForm());
        Circle circle1 = new Circle(0, scene.getHeight(), 20);
        contentPane.setClip(circle1);
        skinImageView.setVisible(true);
        Timeline tl = new Timeline(new KeyFrame(Duration.millis(500), new KeyValue(circle1.radiusProperty(), scene.getHeight() * 1.2),
                new KeyValue(toggleBtn.rotateProperty(), 360)));
        tl.setOnFinished(event1 -> {
            contentPane.setClip(null);
            toggleBtn.setRotate(0);
            toggleBtn.setDisable(false);
        });
        tl.play();

        ToolSettingsUtil.getInstance().saveSkin(skinStyle);
        EventBusUtil.getDefault().post(new SkinChangedEvent());
    }

    @FXML
    void showGluonAction(ActionEvent event) {
        OSUtil.showDoc(Constants.Gluonhq);
    }


    @FXML
    void onClickAboutBtn(MouseEvent event) {
        new AboutAlert().showAndWait();
    }

    @Subscribe
    public void screenTaskEndHandler(ScreenTaskEndEvent event) {
        alwaysTopBtn.fire();
        alwaysTopBtn.fire();
        stage.requestFocus();
    }

    @FXML
    void onClickDonateBtn(ActionEvent event) {
        if (OSUtil.isEnglish()) {
            OSUtil.showDoc("https://www.buymeacoffee.com/fxtools");
        }else {
            new DonateAlert().showAndWait();
        }
    }

    /**
     * 屏幕取色
     */
    @FXML
    void onClickScreenColorPickerBtn(ActionEvent event) {
        new ScreenColorPickerStage().showStage();
    }

    /**
     * 屏幕截图
     */
    @FXML
    void onClickSnapshotBtn(ActionEvent event) {
        boolean screenshotHideWindow = ToolSettingsUtil.getInstance().getScreenshotHideWindow();
        new ScreenshotStage(screenshotHideWindow).showStage();
    }

    /**
     * 打开SceneBuilder的颜色选择器
     */
    @FXML
    void onClickShowFXColorPickerBtn(ActionEvent event) {
        EventBusUtil.getDefault().post(new ShowColorPickerEvent());
    }

}
