package com.leewyatt.fxtools.utils;

import javafx.scene.image.Image;

/**
 * @author LeeWyatt
 */
public interface Constants {

    String LAST_VERSION_GITHUB = "https://github.com/leewyatt/FXTools/releases";
    String LAST_VERSION_GITEE = "https://gitee.com/leewyatt/FXTools/releases";

    String NotFount = "null";
    String CopySVG = "M11,3 L4,3 L4,11 L2,11 L2,1 L11,1 L11,3 Z M5,4 L14,4 L14,14 L5,14 L5,4 Z M7,6 L7,7 L12,7 L12,6 L7,6 Z M7,10 L7,11 L12,11 L12,10 L7,10 Z M7,8 L7,9 L12,9 L12,8 L7,8 Z";

    Image selectedImg = new Image(Constants.class.getResource("/images/selected.png").toExternalForm());
    /**
     * prefix+jdkVersion+suffix ==> Page
     * jdk 8,11-16
     */
    String openjfxPrefix = "https://openjfx.io/javadoc/";
    String CssDocSuffix = "/javafx.graphics/javafx/scene/doc-files/cssref.html";
    String fxmlDocSuffix = "/javafx.fxml/javafx/fxml/doc-files/introduction_to_fxml.html";
    String css8 = "https://docs.oracle.com/javase/8/javafx/api/javafx/scene/doc-files/cssref.html";
    String fxml8 = "https://docs.oracle.com/javase/8/javafx/api/javafx/fxml/doc-files/introduction_to_fxml.html";
    String api8 = "https://docs.oracle.com/javase/8/javafx/api/";

    String iconfont = "https://www.iconfont.cn/";

    String icoMoon = "https://icomoon.io/";
    /**
     * javafx scene_builder
     */
    String Gluonhq = "https://gluonhq.com/";

    String DEFAULT_CSS_URL_8 = "https://note.youdao.com/s/FqLVm4vu";

    String DEFAULT_CSS_URL_11 = "https://note.youdao.com/s/WTZUhWo3";

    String SCENE_BUILDER = "https://gluonhq.com/products/scene-builder/";

    String SCENE_BUILDER_TUTORIAL = "https://www.bilibili.com/video/BV1tt4y1W7nY";



}

