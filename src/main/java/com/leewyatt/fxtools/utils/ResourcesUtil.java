/*
 * MIT License
 *
 * Copyright (c) 2021 LeeWyatt
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */
package com.leewyatt.fxtools.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.image.Image;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;

/**
 * @author LeeWyatt
 * 加载资源工具类
 */
public final class ResourcesUtil {

    public static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("language/language");

    private ResourcesUtil() {
    }

    /**
     * 获取对应语言的文本
     */
    public static String message(String key) {
        return RESOURCE_BUNDLE.getString(key);
    }

    /**
     * 文件路径转url地址
     */
    public static URL pathToUrl(String path) {
        return ResourcesUtil.class.getResource(path);
    }

    /**
     * 从fxml文件加载ui节点
     */
    public static Parent loadFXML(String fxmlName) {
        try {
            return FXMLLoader.load(pathToUrl("/fxml/" + fxmlName + ".fxml"), RESOURCE_BUNDLE);
        } catch (IOException e) {
            Logger logger = Logger.getLogger("com.leewyatt.fxtools.utils.ResourcesUtil");
            logger.severe("IOException: Load FXML("+fxmlName+") Failed.\t" + e);
            throw new RuntimeException(e);
        }
    }

    public static String toExternalForm(String path) {
        return ResourcesUtil.class.getResource(path).toExternalForm();
    }

    public static String fontExternalForm(String fileName) {
        return toExternalForm("/font/" + replaceFolderSeparator(fileName));
    }

    public static String fxmlExternalForm(String fileName) {
        return toExternalForm("/fxml/" + replaceFolderSeparator(fileName));
    }

    public static String imgExternalForm(String fileName) {
        return toExternalForm("/images/" + replaceFolderSeparator(fileName));
    }

    public static String cssExternalForm(String fileName) {
        return toExternalForm("/css/" + replaceFolderSeparator(fileName));
    }

    private static String replaceFolderSeparator(String str) {
        return str.startsWith("/") ? str.substring(1) : str;
    }

    public static List<Image> getIconImages() {
        return List.of(new Image(imgExternalForm("/logo/16.png")),
                new Image(imgExternalForm("/logo/32.png")),
                new Image(imgExternalForm("/logo/64.png")),
                new Image(imgExternalForm("/logo/128.png")),
                new Image(imgExternalForm("/logo/256.png")),
                new Image(imgExternalForm("/logo/512.png")),
                new Image(imgExternalForm("/logo/1024.png")));
    }
}