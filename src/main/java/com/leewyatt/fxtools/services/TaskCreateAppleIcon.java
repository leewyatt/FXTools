package com.leewyatt.fxtools.services;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.leewyatt.fxtools.event.AppIconCreatedEvent;
import com.leewyatt.fxtools.event.EventBusUtil;
import com.leewyatt.fxtools.model.AppleIcon;
import com.leewyatt.fxtools.model.AppleIconList;
import com.leewyatt.fxtools.utils.FileUtil;
import com.leewyatt.fxtools.utils.ImageUtil;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author LeeWyatt
 */
public class TaskCreateAppleIcon implements Runnable {
    private Path path;
    private BufferedImage awtImage;
    private boolean macosIncsSelected;
    private boolean macOSSelected;
    private boolean iphoneSelected;
    private boolean ipadSelected;
    private boolean watchOSSelected;

    public TaskCreateAppleIcon(Path path, BufferedImage awtImage, boolean macosIncsSelected, boolean macOSSelected, boolean iphoneSelected, boolean ipadSelected, boolean watchOSSelected) {
        this.path = path;
        this.awtImage = awtImage;
        this.macosIncsSelected = macosIncsSelected;
        this.macOSSelected = macOSSelected;
        this.iphoneSelected = iphoneSelected;
        this.ipadSelected = ipadSelected;
        this.watchOSSelected = watchOSSelected;
    }

    @Override
    public void run() {
        try {
            Path applePath = path.resolve("apple");
            Files.createDirectories(applePath);
            if (macosIncsSelected) {
                //icns图片
                Path icnsPath = applePath.resolve("macos_appicon.icns");
                ImageUtil.writeIcns(icnsPath, awtImage);
            }
            if (macOSSelected || iphoneSelected || ipadSelected || watchOSSelected) {
                ArrayList<AppleIcon> resultApple = new ArrayList<>();
                //appstore 图片
                Path playstorePath = applePath.resolve("appstore.png");
                ImageUtil.writeScaleImage2File(playstorePath.toFile(), awtImage, 1024);
                Path appiconsetPath = applePath.resolve("Assets.xcassets").resolve("AppIcon.appiconset");
                Files.createDirectories(appiconsetPath);
                HashMap<String, Boolean> appleSizeMap = new HashMap<>();
                if (macOSSelected) {
                    writeAppleIcons("macos", resultApple, appleSizeMap, appiconsetPath);
                }
                if (watchOSSelected) {
                    writeAppleIcons("watchos", resultApple, appleSizeMap, appiconsetPath);
                }
                if (ipadSelected) {
                    writeAppleIcons("ipad", resultApple, appleSizeMap, appiconsetPath);
                }
                if (iphoneSelected) {
                    writeAppleIcons("iphone", resultApple, appleSizeMap, appiconsetPath);
                }
                AppleIconList appleIconList = new AppleIconList();
                appleIconList.setImages(resultApple);
                String jsonStr = new Gson().toJson(appleIconList);
                Files.writeString(appiconsetPath.resolve("Contents.json"), jsonStr);
            }
        } catch (IOException | URISyntaxException e) {
            Logger logger = Logger.getLogger("com.leewyatt.fxtools.services.TaskCreateAppleIcon");
            logger.severe("Exception: createAppleIcon failed.\t"+logger);
            throw new RuntimeException(e);
        }
        EventBusUtil.getDefault().post(new AppIconCreatedEvent());

    }
    private void writeAppleIcons(String name, ArrayList<AppleIcon> resultApple, HashMap<String, Boolean> appleSizeMap, Path appiconsetPath) throws IOException, URISyntaxException {
        //String content = Files.readString(Path.of(getClass().getResource("/data/" + name + ".json").toURI()));
        String content = FileUtil.readString("/data/" + name + ".json");
        List<AppleIcon> macosAppleIcons = new Gson().fromJson(content, new TypeToken<List<AppleIcon>>() {
        }.getType());
        resultApple.addAll(macosAppleIcons);
        for (AppleIcon appleIcon : macosAppleIcons) {
            String expectedSize = appleIcon.getExpectedSize();
            if (!appleSizeMap.containsKey(expectedSize)) {
                Path resolve = appiconsetPath.resolve(expectedSize + ".png");
                ImageUtil.writeScaleImage2File(resolve.toFile(), awtImage, Integer.parseInt(expectedSize));
                appleSizeMap.put(expectedSize, true);
            }
        }
    }
}
