package com.leewyatt.fxtools.utils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * @author LeeWyatt
 * 配置用json 感觉比 Properties 更方便
 */
public class ToolSettingsUtil {
    private static final ToolSettingsUtil instance = new ToolSettingsUtil();

    private final ToolSettings settings = new ToolSettings();

    private ToolSettings defaultSettings;
    private static File propertiesFile;
    /**
     * 系统线程数
     */
    public static final int PROCESSORS = Runtime.getRuntime().availableProcessors();

    private ToolSettingsUtil() {
        initDefaultSettings();
        try {
            propertiesFile = FileUtil.getInstance().getProjectConfigDirectoryPath().resolve("fxtools.properties").toFile();
            if (!propertiesFile.exists()) {
                saveSettings(defaultSettings);
            } else {
                Properties props = new Properties();
                FileReader reader = new FileReader(propertiesFile);
                props.load(reader);
                settings.setSkin(props.getProperty("skin", defaultSettings.getSkin()));
                settings.setDocVersion(props.getProperty("docVersion", defaultSettings.getDocVersion()));
                settings.setScreenshotHideWindow(Boolean.parseBoolean(props.getProperty("screenshotHideWindow", defaultSettings.isScreenshotHideWindow() + "")));
                settings.setScreenshotOpenImg(Boolean.parseBoolean(props.getProperty("screenshotOpenImg", defaultSettings.isScreenshotOpenImg() + "")));
                settings.setScreenshotOpenFile(Boolean.parseBoolean(props.getProperty("screenshotOpenFile", defaultSettings.isScreenshotOpenFile() + "")));
                settings.setShowImageTips(Boolean.parseBoolean(props.getProperty("showImgTips", defaultSettings.isShowImageTips() + "")));
                settings.setGeneratePreviewImg(Boolean.parseBoolean(props.getProperty("generatePreviewImg", defaultSettings.isGeneratePreviewImg() + "")));
                settings.setParseImageSize(Boolean.parseBoolean(props.getProperty("parseImageSize", defaultSettings.isParseImageSize() + "")));
                settings.setThreadNum(Integer.parseInt(props.getProperty("threadNum", defaultSettings.getThreadNum() + "")));
                settings.setTaskCompleteOpenFile(Boolean.parseBoolean(props.getProperty("taskCompleteOpenFile", defaultSettings.isTaskCompleteOpenFile() + "")));
                settings.setAutoSwitch(Boolean.parseBoolean(props.getProperty("autoSwitch", defaultSettings.isAutoSwitch() + "")));
                settings.setAlwaysTop(Boolean.parseBoolean(props.getProperty("alwaysTop", defaultSettings.isAlwaysTop() + "")));
                settings.setUpdateNotify(Boolean.parseBoolean(props.getProperty("updateNotify", defaultSettings.isUpdateNotify() + "")));
                settings.setSkipBootAnimation(Boolean.parseBoolean(props.getProperty("skipBootAnimation", defaultSettings.isSkipBootAnimation() + "")));
                reader.close();
            }
        } catch (IOException e) {
            Logger logger = Logger.getLogger("com.leewyatt.fxtools.utils.ToolSettingsUtil");
            logger.severe("IOException: load settings failed.\t"+e);
            throw new RuntimeException(e);
        }
    }

    private void initDefaultSettings() {
        defaultSettings = new ToolSettings();
        defaultSettings.setSkin(ToolSettings.DEFAULT_SKIN);
        defaultSettings.setDocVersion(ToolSettings.DEFAULT_DOC_VERSION);
        defaultSettings.setScreenshotHideWindow(ToolSettings.DEFAULT_SCREENSHOT_HIDE_WINDOW);
        defaultSettings.setScreenshotOpenImg(ToolSettings.DEFAULT_SCREENSHOT_OPEN_IMG);
        defaultSettings.setScreenshotOpenFile(ToolSettings.DEFAULT_SCREENSHOT_OPEN_FILE);
        defaultSettings.setShowImageTips(ToolSettings.DEFAULT_SHOW_IMAGE_TIPS);
        defaultSettings.setGeneratePreviewImg(ToolSettings.DEFAULT_SHOW_PREVIEW_IMG);
        defaultSettings.setParseImageSize(ToolSettings.DEFAULT_PARSE_IMAGE_SIZE);
        defaultSettings.setThreadNum(ToolSettings.DEFAULT_THREAD_NUM);
        defaultSettings.setTaskCompleteOpenFile(ToolSettings.DEFAULT_TASK_COMPLETE_OPEN_FILE);
        defaultSettings.setAutoSwitch(ToolSettings.DEFAULT_AUTO_SWITCH);
        defaultSettings.setAlwaysTop(ToolSettings.DEFAULT_ALWAYS_TOP);
        defaultSettings.setUpdateNotify(ToolSettings.DEFAULT_UPDATE_NOTIFY);
        defaultSettings.setSkipBootAnimation(ToolSettings.DEFAULT_SKIP_BOOT_ANIMATION);
    }

    public static ToolSettingsUtil getInstance() {
        return instance;
    }

    public ToolSettings getSettings() {
        return settings;
    }

    public void saveSettings(ToolSettings newSettings) {
        settings.setSkin(newSettings.getSkin());
        settings.setDocVersion(newSettings.getDocVersion());
        settings.setScreenshotHideWindow(newSettings.isScreenshotHideWindow());
        settings.setScreenshotOpenImg(newSettings.isScreenshotOpenImg());
        settings.setScreenshotOpenFile(newSettings.isScreenshotOpenFile());
        settings.setShowImageTips(newSettings.isShowImageTips());
        settings.setGeneratePreviewImg(newSettings.isGeneratePreviewImg());
        settings.setParseImageSize(newSettings.isParseImageSize());
        settings.setThreadNum(newSettings.getThreadNum());
        settings.setTaskCompleteOpenFile(newSettings.isTaskCompleteOpenFile());
        settings.setAutoSwitch(newSettings.isAutoSwitch());
        settings.setAlwaysTop(newSettings.isAlwaysTop());
        settings.setUpdateNotify(newSettings.isUpdateNotify());
        settings.setSkipBootAnimation(newSettings.isSkipBootAnimation());
        saveSettings();
    }

    public void saveSettings() {
        Properties props = new Properties();
        props.setProperty("skin", settings.getSkin());
        props.setProperty("docVersion", settings.getDocVersion());
        props.setProperty("screenshotHideWindow", settings.isScreenshotHideWindow() + "");
        props.setProperty("screenshotOpenImg", settings.isScreenshotOpenImg() + "");
        props.setProperty("screenshotOpenFile", settings.isScreenshotOpenFile() + "");
        props.setProperty("showImgTips", settings.isShowImageTips() + "");
        props.setProperty("generatePreviewImg", settings.isGeneratePreviewImg() + "");
        props.setProperty("parseImageSize", settings.isParseImageSize() + "");
        props.setProperty("threadNum", settings.getThreadNum() + "");
        props.setProperty("taskCompleteOpenFile", settings.isTaskCompleteOpenFile() + "");
        props.setProperty("autoSwitch", settings.isAutoSwitch() + "");
        props.setProperty("alwaysTop", settings.isAlwaysTop() + "");
        props.setProperty("updateNotify",settings.isUpdateNotify()+"");
        props.setProperty("skipBootAnimation",settings.isSkipBootAnimation()+"");
        try {
            FileWriter writer = new FileWriter(propertiesFile);
            props.store(writer, "Change settings");
            writer.close();
        } catch (IOException e) {
            Logger logger = Logger.getLogger("com.leewyatt.fxtools.utils.ToolSettingsUtil");
            logger.severe("IOException: write settings failed.\t"+e);
            throw new RuntimeException(e);
        }
    }

    /*
     * ***************
     * 皮肤样式
     * ***************
     */
    public String getSkin() {
        return settings.getSkin();
    }

    public void saveSkin(String skin) {
        settings.setSkin(skin);
        saveSettings();
    }

    /*
     * ***************
     * 文档的版本
     * ***************
     */
    public String getDocVersion() {
        return settings.getDocVersion();
    }

    public void saveDocVersion(String docVersion) {
        settings.setDocVersion(docVersion);
        saveSettings();
    }

    /*
     * ***************
     * 截图时是否隐藏窗口
     * ***************
     */
    public boolean getScreenshotHideWindow() {
        return settings.isScreenshotHideWindow();
    }

    public void saveScreenshotHideWindow(boolean screenshotHideWindow) {
        settings.setScreenshotHideWindow(screenshotHideWindow);
        saveSettings();
    }

    /*
     * ***************
     * 图片处理任务完成时
     * true 打开文件/文件夹
     * ***************
     */
    public boolean getTaskCompleteOpenFile() {
        return settings.isTaskCompleteOpenFile();
    }

    public void saveTaskCompleteOpenFile(boolean openFile) {
        settings.setTaskCompleteOpenFile(openFile);
        saveSettings();
    }

    /*
     * ***************
     * 列表为空时,是否显示使用说明
     * ***************
     */
    public boolean getShowImgTips() {
        return settings.isShowImageTips();
    }

    public void saveShowImageTips(boolean showImageTips) {
        settings.setShowImageTips(showImageTips);
        saveSettings();
    }

    /*
     * ***************
     * 是否生成预览图片
     * ***************
     */
    public boolean getGeneratePreviewImg() {
        return settings.isGeneratePreviewImg();
    }

    public void saveGeneratePreviewImg(boolean generatePreviewImg) {
        settings.setGeneratePreviewImg(generatePreviewImg);
        saveSettings();
    }

    /*
     * ***************
     * 是否解析图片宽高
     * ***************
     */
    public boolean getParseImageSize() {
        return settings.isParseImageSize();
    }

    public void saveParseImageSize(boolean parseImageSize) {
        settings.setParseImageSize(parseImageSize);
        saveSettings();
    }

    /*
     * ***************
     * 截图完成是否显示(打开)图片
     * ***************
     */
    public boolean getScreenshotOpenImg() {
        return settings.isScreenshotOpenImg();
    }

    public void saveScreenshotOpenImg(boolean screenshotOpenImg) {
        settings.setScreenshotOpenImg(screenshotOpenImg);
        saveSettings();
    }

    /*
     * ***************
     * 截图完成是否显示(打开)文件夹
     * ***************
     */
    public boolean getScreenshotOpenFile() {
        return settings.isScreenshotOpenFile();
    }

    public void saveScreenshotOpenFile(boolean screenshotOpenFile) {
        settings.setScreenshotOpenFile(screenshotOpenFile);
        saveSettings();
    }

    /*
     * ***************
     * 轮播图自动切换
     * ***************
     */
    public boolean getAutoSwitch() {
        return settings.isAutoSwitch();
    }

    public void saveAutoSwitch(boolean autoSwitch) {
        settings.setAutoSwitch(autoSwitch);
        saveSettings();
    }


    /*
     * ***************
     * 总是置顶
     * ***************
     */
    public boolean getAlwaysTop() {
        return settings.isAlwaysTop();
    }

    public void saveAlwaysTop(boolean alwaysTop) {
        settings.setAlwaysTop(alwaysTop);
        saveSettings();
    }

    /*
     * ***************
     * 图片处理线程数量
     * ***************
     */
    public int getThreadNum() {
        return settings.getThreadNum();
    }

    public int saveThreadNum(int threadNum) {
        if (threadNum < 0) {
            threadNum = 1;
        }
        if (threadNum > PROCESSORS*2) {
            threadNum = PROCESSORS*2;
        }
        settings.setThreadNum(threadNum);
        saveSettings();
        return threadNum;
    }

    /*
     * ***************
     * 有新版本时是否通知
     * ***************
     */
    public boolean getUpdateNotify() {
        return  settings.isUpdateNotify();
    }

    public void saveUpdateNotify(boolean notify) {
        settings.setUpdateNotify(notify);
        saveSettings();
    }
    /*
     * ***************
     * 启动时是否跳过动画
     * ***************
     */
    public boolean getSKipBootAnimation() {
        return  settings.isSkipBootAnimation();
    }

    public void saveSkipBootAnimation(boolean skipBootAnimation) {
        settings.setSkipBootAnimation(skipBootAnimation);;
        saveSettings();
    }
}