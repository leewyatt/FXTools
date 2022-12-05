package com.leewyatt.fxtools.utils;

/**
 * @author LeeWyatt
 */
public class ToolSettings {

    public static final String DEFAULT_SKIN = "dark";

    public static final String DEFAULT_DOC_VERSION = "17";

    public static final boolean DEFAULT_SCREENSHOT_HIDE_WINDOW = false;

    public static final boolean DEFAULT_TASK_COMPLETE_OPEN_FILE = true;

    public static final boolean DEFAULT_SHOW_IMAGE_TIPS = true;
    public static final boolean DEFAULT_SHOW_PREVIEW_IMG = true;

    public static final boolean DEFAULT_PARSE_IMAGE_SIZE = true;

    public static final boolean DEFAULT_SCREENSHOT_OPEN_IMG = true;
    public static final boolean DEFAULT_SCREENSHOT_OPEN_FILE = false;

    public static final boolean DEFAULT_AUTO_SWITCH = true;

    public static final boolean DEFAULT_ALWAYS_TOP = false;

    public static final int DEFAULT_THREAD_NUM = 2;

    /**
     * 默认皮肤
     */
    private String skin;
    /**
     * 自动切换
     */
    private boolean autoSwitch;
    /**
     * 默认文档版本
     */
    private String docVersion;
    /**
     * 截图时是否隐藏窗口
     * true 隐藏窗口
     * false 不隐藏窗口
     */
    private boolean screenshotHideWindow;
    /**
     * 图片任务处理完成后
     * true 显示文件/文件夹
     */
    private boolean taskCompleteOpenFile;

    /**
     * 当列表为空是否显示使用提示/说明
     * true 显示提示
     * false 隐藏提示
     */
    private boolean showImageTips;
    /**
     * 是否显示预览图片
     * true 显示
     * false 不显示
     */
    private boolean generatePreviewImg;
    /**
     * 是否生成预览图片
     * ture 生成
     * false 不生成
     */
    private boolean parseImageSize;

    /**
     * 截图保存完毕,是否显示图片
     * true 显示图片
     * false 不显示
     */
    private boolean screenshotOpenImg;

    /**
     * 截图保存完毕,是否打开文件
     * true 打开文件
     * false 不打开文件
     */
    private boolean screenshotOpenFile;

    /**
     * 图片任务处理线程数量
     */
    private int threadNum;

    private boolean alwaysTop;

    public ToolSettings() {

    }

    /**
     *
     * @return dark /light
     */
    public String getSkin() {
        return skin;
    }

    public void setSkin(String skin) {
        this.skin = skin;
    }

    public String getDocVersion() {
        return docVersion;
    }

    public void setDocVersion(String docVersion) {
        this.docVersion = docVersion;
    }

    public boolean isScreenshotHideWindow() {
        return screenshotHideWindow;
    }

    public void setScreenshotHideWindow(boolean screenshotHideWindow) {
        this.screenshotHideWindow = screenshotHideWindow;
    }

    public boolean isShowImageTips() {
        return showImageTips;
    }

    public void setShowImageTips(boolean showImageTips) {
        this.showImageTips = showImageTips;
    }

    public boolean isGeneratePreviewImg() {
        return generatePreviewImg;
    }

    public void setGeneratePreviewImg(boolean generatePreviewImg) {
        this.generatePreviewImg = generatePreviewImg;
    }

    public boolean isParseImageSize() {
        return parseImageSize;
    }

    public void setParseImageSize(boolean parseImageSize) {
        this.parseImageSize = parseImageSize;
    }

    public boolean isScreenshotOpenImg() {
        return screenshotOpenImg;
    }

    public void setScreenshotOpenImg(boolean screenshotOpenImg) {
        this.screenshotOpenImg = screenshotOpenImg;
    }

    public boolean isScreenshotOpenFile() {
        return screenshotOpenFile;
    }

    public void setScreenshotOpenFile(boolean screenshotOpenFile) {
        this.screenshotOpenFile = screenshotOpenFile;
    }

    public int getThreadNum() {
        return threadNum;
    }

    public void setThreadNum(int threadNum) {
        this.threadNum = threadNum;
    }

    public boolean isTaskCompleteOpenFile() {
        return taskCompleteOpenFile;
    }

    public void setTaskCompleteOpenFile(boolean taskCompleteOpenFile) {
        this.taskCompleteOpenFile = taskCompleteOpenFile;
    }

    public boolean isAutoSwitch() {
        return autoSwitch;
    }

    public void setAutoSwitch(boolean autoSwitch) {
        this.autoSwitch = autoSwitch;
    }

    public boolean isAlwaysTop() {
        return alwaysTop;
    }

    public void setAlwaysTop(boolean alwaysTop) {
        this.alwaysTop = alwaysTop;
    }
}
