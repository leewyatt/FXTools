package com.leewyatt.fxtools.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.leewyatt.fxtools.model.MyLinearGradient;
import com.leewyatt.fxtools.model.MyLinearGradientUtil;
import javafx.scene.paint.LinearGradient;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author LeeWyatt
 */
public class LinearGradientConstants {
     static {
         Gson gson = new Gson();
         String content = FileUtil.readString("/data/linearGradient.json");
         List<MyLinearGradient> myLinearGradients = gson.fromJson(content, new TypeToken<List<MyLinearGradient>>() {
         }.getType());
         LIST  = myLinearGradients.stream().map(MyLinearGradientUtil::convertToFXPatin).collect(Collectors.toList());
     }
    public static final  List<LinearGradient> LIST;

}
