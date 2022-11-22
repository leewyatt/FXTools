package com.leewyatt.fxtools.ui.alert;

import com.leewyatt.fxtools.utils.OSUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author LeeWyatt
 */
public class AboutAlertController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Label qqGroupLabel;

    @FXML
    private Label qqLabel;

    @FXML
    private VBox qqContactBox;
    @FXML
    private Label bilibiliLabel;

    @FXML
    void initialize() {
        qqContactBox.setVisible(!OSUtil.isEnglish());
        qqContactBox.setManaged(!OSUtil.isEnglish());
    }

    @FXML
    void onClickGMailBtn(MouseEvent event) {
        OSUtil.showDoc("mailto:leewyatt7788@gmail.com?subject=PCJavaFXTools");
    }

    @FXML
    void onClickCopyQQ(MouseEvent event) {
        OSUtil.writeToClipboard(qqLabel.getText().trim());
    }

    @FXML
    void onClickCopyQQGroup(MouseEvent event) {
        OSUtil.writeToClipboard(qqGroupLabel.getText().trim());
    }

    @FXML
    void onClickBilibiliBtn(MouseEvent event) {
        OSUtil.showDoc("https://space.bilibili.com/397562730");
    }

    @FXML
    void onClickGithubBtn(MouseEvent event) {
        OSUtil.showDoc("https://github.com/leewyatt");
    }

    @FXML
    void onClickTwitterBtn(MouseEvent event) {
        OSUtil.showDoc("https://twitter.com/LeeWyatt_7788");
    }

    @FXML
    void onClickYoutubeBtn(MouseEvent event) {
        OSUtil.showDoc("https://www.youtube.com/channel/UCWx2meI62ept14Gb8rzq7Vw");
    }

    @FXML
    void onClickCheckUpdateButton(MouseEvent event) {
        OSUtil.showDoc("https://github.com/leewyatt/FXTools");
    }

}
