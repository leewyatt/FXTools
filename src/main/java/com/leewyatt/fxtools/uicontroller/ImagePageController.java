package com.leewyatt.fxtools.uicontroller;

import com.google.common.eventbus.Subscribe;
import com.leewyatt.fxtools.event.EventBusUtil;
import com.leewyatt.fxtools.event.PageJumpEvent;
import com.leewyatt.fxtools.utils.ResourcesUtil;
import com.leewyatt.rxcontrols.animation.carousel.AnimFlip;
import com.leewyatt.rxcontrols.controls.RXCarousel;
import com.leewyatt.rxcontrols.enums.DisplayMode;
import com.leewyatt.rxcontrols.pane.RXCarouselPane;
import javafx.fxml.FXML;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author LeeWyatt
 */
public class ImagePageController {
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private RXCarousel imageToolsCarousel;

    public ImagePageController() {
        EventBusUtil.getDefault().register(this);
    }

    @FXML
    void initialize() {
        imageToolsCarousel.setAutoSwitch(false);
        imageToolsCarousel.setHoverPause(false);
        imageToolsCarousel.setCarouselAnimation(new AnimFlip(RXCarousel.RXDirection.VER));
        imageToolsCarousel.setAnimationTime(Duration.millis(228));
        imageToolsCarousel.setArrowDisplayMode(DisplayMode.HIDE);
        imageToolsCarousel.setNavDisplayMode(DisplayMode.HIDE);

        RXCarouselPane imageMenuPane = (RXCarouselPane) ResourcesUtil.loadFXML("image-menu-page");
        RXCarouselPane imageFormat = (RXCarouselPane) ResourcesUtil.loadFXML("image-format-page");
        RXCarouselPane imageCutter = (RXCarouselPane) ResourcesUtil.loadFXML("image-cutter-page");
        RXCarouselPane imageStitching = (RXCarouselPane) ResourcesUtil.loadFXML("image-stitching-page");
        RXCarouselPane imageGifDecoder = (RXCarouselPane) ResourcesUtil.loadFXML("image-gif-decoder-page");
        RXCarouselPane appIconPane = (RXCarouselPane) ResourcesUtil.loadFXML("image-appicon-page");
        imageToolsCarousel.getPaneList().addAll(imageMenuPane, imageFormat, imageCutter, imageStitching, imageGifDecoder,appIconPane);
    }


    @Subscribe
    public void pageJumpHandler(PageJumpEvent pageJumpEvent) {
        imageToolsCarousel.setSelectedIndex(pageJumpEvent.getPageIndex());
    }



}
