package com.leewyatt.fxtools.event;

/**
 * @author LeeWyatt
 */
public enum ImageEventType {

    /**
     * 格式转换
     */
    FORMAT_EXPORT,
    /**
     * 格式转换页加载分析图片
     */
    FORMAT_IMPORT,

    /**
     * 图片拼接
     */
    STITCHING_EXPORT,
    /**
     * 图片拼接页加载分析图片
     */
    STITCHING_IMPORT,
    /**
     * 多倍图切图1x 2x 3x ..
     */
    CUTTER_EXPORT,
    /**
     * 多倍图切图页加载分析图片
     */
    CUTTER_IMPORT
}
