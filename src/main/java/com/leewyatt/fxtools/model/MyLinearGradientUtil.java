package com.leewyatt.fxtools.model;

import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;

import java.util.List;

/**
 * @author LeeWyatt
 */
public class MyLinearGradientUtil {
    public static LinearGradient convertToFXPatin(MyLinearGradient mg) {
        List<MyStop> myStops = mg.getStops();
        Stop[] stops =new Stop[myStops.size()];
        for (int i = 0; i < myStops.size(); i++) {
            MyStop myStop = myStops.get(i);
            MyColor mc = myStop.getMyColor();
            stops[i] = new Stop(myStop.getOffset(),new Color(mc.getRed(), mc.getGreen(), mc.getBlue(), mc.getOpacity()));
        }
        return new LinearGradient(mg.getStartX(), mg.getStartY(),
                mg.getEndX(), mg.getEndY(), true, CycleMethod.NO_CYCLE,stops);
    }
}
