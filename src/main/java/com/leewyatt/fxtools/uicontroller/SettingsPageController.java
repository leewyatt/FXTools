package com.leewyatt.fxtools.uicontroller;

import com.leewyatt.fxtools.event.AutoSwitchChangedEvent;
import com.leewyatt.fxtools.event.EventBusUtil;
import com.leewyatt.fxtools.event.ShowImgTipsEvent;
import com.leewyatt.fxtools.ui.paintpicker.IntegerTextField;
import com.leewyatt.fxtools.utils.Constants;
import com.leewyatt.fxtools.utils.ToolSettingsUtil;
import com.leewyatt.fxtools.utils.OSUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.ResourceBundle;
/**
 * @author LeeWyatt
 */
public class SettingsPageController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private CheckBox autoSwitchCheckBox;

    @FXML
    private CheckBox taskCompleteOpenFileCheckBox;
    @FXML
    private CheckBox screenshotOpenImgCheckbox;

    @FXML
    private CheckBox screenshotOpenFileCheckbox;

    @FXML
    private CheckBox showImageTipsCheckbox;

    @FXML
    private CheckBox showPreviewImageCheckbox;
    @FXML
    private CheckBox parseImageSizeCheckbox;

    @FXML
    private HBox threadBox;
    private IntegerTextField threadField;

    @FXML
    private CheckBox hideStageCheckBox;

    @FXML
    private CheckBox updateNotifyCheckBox;

    public SettingsPageController() {
        EventBusUtil.getDefault().register(this);
    }

    @FXML
    void onClickCheckUpdateButton(ActionEvent event) {
        if (OSUtil.isEnglish()) {
            OSUtil.showDoc(Constants.LAST_VERSION_GITHUB);
        } else {
            OSUtil.showDoc(Constants.LAST_VERSION_GITEE);
        }
    }

    @FXML
    void initialize() {
        threadField = new IntegerTextField();
        threadField.setPrefWidth(60);
        threadBox.getChildren().add(threadField);

        showSettingsOnUI();
        ToolSettingsUtil util = ToolSettingsUtil.getInstance();
        updateNotifyCheckBox.selectedProperty().addListener((ob, ov, nv) -> util.saveUpdateNotify(nv));
        hideStageCheckBox.selectedProperty().addListener((ob, ov, nv) -> util.saveScreenshotHideWindow(nv));
        autoSwitchCheckBox.selectedProperty().addListener((ob, ov, nv) -> {
            util.saveAutoSwitch(nv);
            EventBusUtil.getDefault().post(new AutoSwitchChangedEvent(nv));
        });
        screenshotOpenImgCheckbox.selectedProperty().addListener((ob, ov, nv) -> util.saveScreenshotOpenImg(nv));
        screenshotOpenFileCheckbox.selectedProperty().addListener((ob, ov, nv) -> util.saveScreenshotOpenFile(nv));
        taskCompleteOpenFileCheckBox.selectedProperty().addListener((ob, ov, nv) -> util.saveTaskCompleteOpenFile(nv));
        showImageTipsCheckbox.selectedProperty().addListener((ob, ov, nv) -> {
            util.saveShowImageTips(nv);
            EventBusUtil.getDefault().post(new ShowImgTipsEvent());
        });
        parseImageSizeCheckbox.selectedProperty().addListener((ob, ov, nv) -> util.saveParseImageSize(nv));
        showPreviewImageCheckbox.selectedProperty().addListener((ob, ov, nv) -> util.saveGeneratePreviewImg(nv));
        threadField.focusedProperty().addListener((ob, ov, nv) -> {
            if (!nv) {
                int i = util.saveThreadNum(threadField.getValue());
                threadField.setText(i+"");
            }
        });
    }

    private void showSettingsOnUI() {
        ToolSettingsUtil util = ToolSettingsUtil.getInstance();
        updateNotifyCheckBox.setSelected(util.getUpdateNotify());
        autoSwitchCheckBox.setSelected(util.getAutoSwitch());
        hideStageCheckBox.setSelected(util.getScreenshotHideWindow());
        screenshotOpenImgCheckbox.setSelected(util.getScreenshotOpenImg());
        screenshotOpenFileCheckbox.setSelected(util.getScreenshotOpenFile());
        taskCompleteOpenFileCheckBox.setSelected(util.getTaskCompleteOpenFile());
        showImageTipsCheckbox.setSelected(util.getShowImgTips());
        showPreviewImageCheckbox.setSelected(util.getGeneratePreviewImg());
        parseImageSizeCheckbox.setSelected(util.getParseImageSize());
        threadField.setText(util.getThreadNum() + "");
    }

}
