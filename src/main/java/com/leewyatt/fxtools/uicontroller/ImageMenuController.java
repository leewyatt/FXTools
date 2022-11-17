package com.leewyatt.fxtools.uicontroller;

import com.google.common.eventbus.Subscribe;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.leewyatt.fxtools.event.AutoSwitchChangedEvent;
import com.leewyatt.fxtools.event.EventBusUtil;
import com.leewyatt.fxtools.event.PageJumpEvent;
import com.leewyatt.fxtools.model.Photo;
import com.leewyatt.fxtools.model.ResourceWebsite;
import com.leewyatt.fxtools.utils.FileUtil;
import com.leewyatt.fxtools.utils.ToolSettingsUtil;
import com.leewyatt.fxtools.utils.OSUtil;
import com.leewyatt.rxcontrols.animation.carousel.*;
import com.leewyatt.rxcontrols.controls.RXCarousel;
import com.leewyatt.rxcontrols.pane.RXCarouselPane;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;

import java.util.List;
import java.util.Random;

/**
 * @author LeeWyatt
 */
public class ImageMenuController {

    @FXML
    private RXCarousel carousel;
    private RXCarouselPane[] carouselPanes;

    @FXML
    private Label urlLabel;

    @FXML
    private Label descLabel;



    public ImageMenuController() {
        EventBusUtil.getDefault().register(this);
    }

    @FXML
    void initialize(){
        Random random = new Random();
        int x = random.nextInt(7);
        CarouselAnimation animation;
        if (x == 0) {
            animation = new AnimVerBlinds();
        } else if (x == 1) {
            animation = new AnimHorBlinds();
        } else if (x == 2) {
            animation = new AnimLinePair();
        } else if (x == 3) {
            animation = new AnimLinePair(RXCarousel.RXDirection.VER);
        }else if (x == 4) {
            animation = new AnimHorMove(true);
        }else if (x == 5) {
            animation =new AnimVerMove(true);
        } else {
            animation = new AnimVerBox();
        }
        carousel.setCarouselAnimation(animation);
        carousel.setAutoSwitch(ToolSettingsUtil.getInstance().getAutoSwitch());
        Gson gson = new Gson();
        String content = FileUtil.readString("/data/imageCarousel.json");
        List<ResourceWebsite> websiteList = gson.fromJson(content, new TypeToken<List<ResourceWebsite>>(){}.getType());
        carouselPanes = new RXCarouselPane[websiteList.size()];
        for (int i = 0; i < websiteList.size(); i++) {
            carouselPanes[i] = getPane(websiteList.get(i));
        }
        carousel.getPaneList().setAll(carouselPanes);
        carousel.setSelectedIndex(0);
        showWebsiteInfo(0);
        carousel.selectedIndexProperty().addListener((ob, ov, nv) ->{
            showWebsiteInfo(nv.intValue());
        });
        urlLabel.setOnMouseClicked(event -> OSUtil.showDoc(urlLabel.getText()));
    }

    private void showWebsiteInfo(int nv) {
        ResourceWebsite website = (ResourceWebsite) carouselPanes[nv].getUserData();
        urlLabel.setText(website.getUrl());
        descLabel.setText(OSUtil.isEnglish() ? website.getDescEn() : website.getDescZh());
    }

    private RXCarouselPane getPane(ResourceWebsite resourceWebsite) {
        RXCarouselPane pane = new RXCarouselPane();
        List<Photo> images = resourceWebsite.getPhotos();
        Random random = new Random();
        Photo photo = images.get(random.nextInt(images.size()));
        ImageView imageView = new ImageView(getClass().getResource("/images/carousel/" + photo.getPhotoName()).toExternalForm());
        pane.setUserData(resourceWebsite);
        Label authorLabel = new Label();
        authorLabel.setText("@ "+ photo.getPhotoAuthor());
        authorLabel.getStyleClass().add("gray-bg-label");
        StackPane.setAlignment(authorLabel, Pos.BOTTOM_RIGHT);
        authorLabel.setTranslateY(-15);
        StackPane sp = new StackPane(imageView, authorLabel);
        pane.setCenter(sp);
        return pane;
    }


    @FXML
    void onClickCutterBtn(MouseEvent event) {
        EventBusUtil.getDefault().post(new PageJumpEvent(2));

    }

    @FXML
    void onClickFormatConverterBtn(MouseEvent event) {
        EventBusUtil.getDefault().post(new PageJumpEvent(1));
    }


    @FXML
    void onClickGifEncoderBtn(MouseEvent event) {
        EventBusUtil.getDefault().post(new PageJumpEvent(4));

    }

    @FXML
    void onClickImageStitchingBtn(MouseEvent event) {
        EventBusUtil.getDefault().post(new PageJumpEvent(3));
    }

    @FXML
    void onClickAppIconBtn(MouseEvent event) {
        EventBusUtil.getDefault().post(new PageJumpEvent(5));
    }

    @Subscribe
    public void autoSwitchChangedEvent(AutoSwitchChangedEvent event) {
        carousel.setAutoSwitch(event.isAutoSwitch());
    }
}
