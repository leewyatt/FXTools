package com.leewyatt.fxtools.utils;

import com.leewyatt.fxtools.FXToolsApp;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.logging.Logger;

/**
 * @author LeeWyatt
 */
public class OSUtil {

    private OSUtil() {
    }
    public enum OS {
        //操作系统
        WINDOWS,
        LINUX,
        MAC,
        UNKNOWN
    }

    private static OS os;

    public static OS getOS() {
        if (os == null) {
            String systemStr = System.getProperty("os.name").toLowerCase();
            if (systemStr.contains("win")) {
                os = OS.WINDOWS;
            } else if (systemStr.contains("nix") || systemStr.contains("nux") || systemStr.contains("aix")) {
                os = OS.LINUX;
            } else if (systemStr.contains("mac")) {
                os = OS.MAC;
            } else {
                os = OS.UNKNOWN;
            }
        }
        return os;
    }

    /**
     * 程序准备显示中文 还是 英语
     * <p>
     * 只要是非中文, 就显示英语
     */
    private static Boolean isEnglish;

    public static boolean isEnglish() {
        if (isEnglish == null) {
            isEnglish = !Locale.CHINESE.getLanguage().equalsIgnoreCase(Locale.getDefault().getLanguage());
        }
        return isEnglish;
    }




    /**
     * 图片写入剪切板
     */
    public static void writeToClipboard(WritableImage writableImage) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putImage(writableImage);
        clipboard.setContent(content);
    }

    /**
     * 文本写入到剪切板
     */
    public static void writeToClipboard(String contentStr) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(contentStr);
        clipboard.setContent(content);
    }

    /**
     * 获得剪切板的文字
     */
    public static String getClipboardString() {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        return clipboard.getString();
    }

    /**
     * 系统默认软件显示文档
     */
    public static void showDoc(String fileUri) {
        FXToolsApp.hostServices.showDocument(fileUri);
    }

    /**
     * win mac linux 系统直接打开文件夹并选中文件
     * 其余系统打开文件夹
     */
    public static void openAndSelectedFile(String filePath) {
        //未知系统, 打开字体文件所在文件夹
        OS currentOS = getOS();
        if (currentOS == OS.UNKNOWN) {
            File dir = new File(filePath).getParentFile();
            showDoc(dir.toURI().toString());
            return;
        }
        File file = new File(filePath);
        //已知系统,用命令行打开文件夹,并选中文件
        filePath = "\"" + filePath + "\"";
        String cmd = "";
        if (currentOS == OS.WINDOWS) {
            if (file.exists() && file.isDirectory()) {
                cmd = "explorer " + filePath;
            } else {
                cmd = "explorer /select," + filePath;
            }
        } else if (currentOS ==OS.MAC) {
            cmd = "xdg-open " + filePath;
        } else if (currentOS == OS.LINUX) {
            cmd = "open -R " + filePath;
        }
        try {
            Runtime.getRuntime().exec(cmd);
        } catch (IOException exception) {
            Logger logger = Logger.getLogger("com.leewyatt.fxtools.utils.OSUtil");
            logger.severe("IOException: openAndSelectedFile failed.\t"+exception);
            exception.printStackTrace();
        }
    }

    public static void openAndSelectedFile(File file) {
        openAndSelectedFile(file.getPath());
    }



}


