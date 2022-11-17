package com.leewyatt.fxtools.utils;

import com.google.common.io.Files;

import java.io.*;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author LeeWyatt
 */
public class FileUtil {
    private static final String USER_HOME_PATH = System.getProperty("user.home");
    public Path projectConfigDirectoryPath = Paths.get(USER_HOME_PATH, ".FXTools_PC");
    public Path cacheDirectoryPath = projectConfigDirectoryPath.resolve("temp_images");
    public Path logsDirectoryPath = projectConfigDirectoryPath.resolve("logs");
    private String lastScreenshotSaveLocation;

    private static FileUtil instance = new FileUtil();

    private FileUtil() {
        try {
            java.nio.file.Files.createDirectories(cacheDirectoryPath);
            java.nio.file.Files.createDirectories(logsDirectoryPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static FileUtil getInstance() {
        return instance;
    }

    public void init() {

    }

    public String getLastScreenshotSaveLocation() {
        return lastScreenshotSaveLocation;
    }

    public void setLastScreenshotSaveLocation(String location) {
        this.lastScreenshotSaveLocation = location;
    }

    public Path getProjectConfigDirectoryPath() {
        return projectConfigDirectoryPath;
    }

    public Path getCacheDirectoryPath() {
        return cacheDirectoryPath;
    }

    public Path getLogsDirectoryPath() {
        return logsDirectoryPath;
    }

    public static String getNameWithoutExtension(File file) {
        return getNameWithoutExtension(file.getAbsolutePath());
    }

    public static String getNameWithoutExtension(String filePath) {
        return Files.getNameWithoutExtension(filePath);
    }

    public static String getFileExtension(File file) {
        return getFileExtension(file.getName());
    }

    public static String getFileExtension(String fileName) {
        return Files.getFileExtension(fileName).toLowerCase();
    }

    public static void copyFile(File srcFile, File destFile) {
        try {
            Files.copy(srcFile, destFile);
        } catch (IOException e) {
            Logger logger = Logger.getLogger("com.leewyatt.fxtools.utils.FileUtil");
            logger.severe("IOException: Copy File Failed.\t"+e);
            e.printStackTrace();
        }
    }

    public static String getUrl(File file) {
        try {
            return file.toURI().toURL().toExternalForm();
        } catch (MalformedURLException e) {
            Logger logger = Logger.getLogger("com.leewyatt.fxtools.utils.FileUtil");
            logger.severe("MalformedURLException: getUrl Failed.\t"+e);
            e.printStackTrace();
            return null;
        }
    }

    public static String readString(File file) {
        try {
            return Files.readLines(file, StandardCharsets.UTF_8)
                    .stream().collect(Collectors.joining());
        } catch (IOException e) {
            Logger logger = Logger.getLogger("com.leewyatt.fxtools.utils.FileUtil");
            logger.severe("IOException: readString "+file.getAbsolutePath()+" failed.\t"+e);
            //throw new RuntimeException(e);
            return "";
        }
    }

    public static String readString(String resourceName) {
        InputStream resourceAsStream = FileUtil.class.getResourceAsStream(resourceName);
        String result = "";
        try {
            result = new String(resourceAsStream.readAllBytes(), StandardCharsets.UTF_8);
            resourceAsStream.close();
        } catch (IOException e) {
            Logger logger = Logger.getLogger("com.leewyatt.fxtools.utils.FileUtil");
            logger.severe("IOException: readString "+resourceName+" failed.\t"+e);
            throw new RuntimeException(e);
        }
        return result;
    }

    public static void writeString(File descFile, String content) {
        try {
            Files.write(content, descFile, StandardCharsets.UTF_8);
        } catch (IOException e) {
            Logger logger = Logger.getLogger("com.leewyatt.fxtools.utils.FileUtil");
            logger.severe("IOException: writeString "+descFile.getAbsolutePath()+" failed.\t"+e);
            throw new RuntimeException(e);
        }
    }

}
