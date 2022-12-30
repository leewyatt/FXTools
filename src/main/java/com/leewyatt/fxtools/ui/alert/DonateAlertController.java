package com.leewyatt.fxtools.ui.alert;


import com.leewyatt.fxtools.utils.OSUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
/**
 * @author LeeWyatt
 */
public class DonateAlertController {

    @FXML
    void onClickDonorListBtn(ActionEvent event) {
        OSUtil.showDoc("https://gitee.com/leewyatt/FXTools#%E6%89%93%E8%B5%8F%E5%88%97%E8%A1%A8");
    }

}
