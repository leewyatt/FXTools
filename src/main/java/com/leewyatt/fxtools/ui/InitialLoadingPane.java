package com.leewyatt.fxtools.ui;

import com.leewyatt.fxtools.event.AnimFinishedEvent;
import com.leewyatt.fxtools.event.EventBusUtil;
import javafx.animation.*;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;

/**
 * @author LeeWyatt
 */
public class InitialLoadingPane extends Pane {

    public InitialLoadingPane(String skinStyle) {
        int w = 316;
        int h = 660;
        int radius = 64;
        EventBusUtil.getDefault().register(this);
        setPrefSize(w, h);
        SVGPath icon = new SVGPath();
        icon.setScaleX(2);
        icon.setScaleY(2);
        icon.setContent("M10.3,7.3L8.2,5.2l0,0L7.8,4.5L7,3.9L6.1,4.8l0.6,0.8L7.4,6l0,0l2.1,2.1L9.2,8.4l1.4,1.4  c0.3,0.3,0.7,0.3,1,0L12,9.5c0.3-0.3,0.3-0.7,0-1l-1.4-1.4C10.6,7,10.3,7.3,10.3,7.3zM11.8,6.3c0.5-0.5,0.5-1.2,0.3-1.7l-0.9,1L10.6,5l0.9-1C11,3.7,10.3,3.8,9.8,4.3C9.4,4.6,9.2,5.2,9.4,5.7  L6.2,8.8C6.1,9,6.1,9.3,6.2,9.5l0,0l0.4,0.3c0.2,0.2,0.5,0.2,0.7,0l3.2-3.1C10.9,6.8,11.4,6.7,11.8,6.3zM1.4,2.4h1.4v7.5H1.4V2.4z M1.9,2.4h4.4v1.3H1.9C1.9,3.8,1.9,2.4,1.9,2.4z M1.9,5.6h3.8v1.3H1.9V5.6zM10.3,13H2.7C1.2,13,0,11.8,0,10.3V2.7C0,1.2,1.2,0,2.7,0h7.6C11.8,0,13,1.2,13,2.7v7.6C13,11.8,11.8,13,10.3,13z M2.7,0.3  c-1.3,0-2.4,1.1-2.4,2.4v7.6c0,1.3,1.1,2.4,2.4,2.4h7.6c1.3,0,2.4-1.1,2.4-2.4V2.7c0-1.3-1.1-2.4-2.4-2.4C10.3,0.3,2.7,0.3,2.7,0.3z");
        icon.setFill(new LinearGradient(
                0.0, 0.0, 1.0, 0.0, true, CycleMethod.NO_CYCLE,
                new Stop(0.0, new Color(0.11, 0.52, 0.93, 1.0)),
                new Stop(1.0, new Color(0.68, 0.05, 0.93, 1.0))));

        StackPane iconPane = new StackPane(icon);
        iconPane.setPrefSize(radius, radius);
        iconPane.setStyle("-fx-background-color: "+("light".equalsIgnoreCase(skinStyle)?"white":"#2b2b2b")+";-fx-background-radius: "+radius/2.0+"px");
        iconPane.setEffect(new DropShadow());
        iconPane.setLayoutX((w-radius)/2.0);
        iconPane.setLayoutY((h-radius)/2.0);
        getChildren().add(iconPane);
        iconPane.setOpacity(0.2);
        //Color Animation 颜色动画, 暂时没有搭配得非常满意的颜色
        //DoubleProperty dp = new SimpleDoubleProperty(0.001);
        //Timeline colorAnim = new Timeline(new KeyFrame(Duration.seconds(1.2),new KeyValue(dp, 0.98)));
        //Random random = new Random();
        //int choose = random.nextInt(2);
        //Color c1;
        //Color c2;
        //Color c3;
        //if (choose == 0) {
        //    c1 = new Color(0.11, 0.52, 0.93, 1.0);
        //    c2= new Color(0.0549, 0.9294, 0.6353, 1.0);
        //    c3 = new Color(0.68, 0.05, 0.93, 1.0);
        //}else {
        //    c1 = new Color(0.9735, 0.0, 0.99, 1.0);
        //    c2 = new Color(0.209, 0.5178, 0.95, 1.0);
        //    c3 = new Color(0.0174, 0.87, 0.87, 1.0);
        //}
        //
        //dp.addListener((observable, oldValue, newValue) -> {
        //    LinearGradient p = new LinearGradient(
        //            0.0, 0.0, 1.0, 1.0, true, CycleMethod.NO_CYCLE,
        //            new Stop(0.0, c1),
        //            new Stop(newValue.doubleValue(), c2),
        //            new Stop(1.0, c3));
        //    icon.setFill(p);
        //});

        Circle circle = new Circle(70);
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(1.5), iconPane);
        fadeTransition.setFromValue(0.2);
        fadeTransition.setToValue(1.0);
        PathTransition pathTransition = new PathTransition(Duration.seconds(2), circle,iconPane);
        ScaleTransition scaleTransition = scaleTransition(1.2, iconPane, 2, 2);
        //ParallelTransition pt = new ParallelTransition(scaleTransition,colorAnim);
        SequentialTransition  animation = new SequentialTransition(fadeTransition,pathTransition,scaleTransition);
        animation.setInterpolator(Interpolator.EASE_IN);
        animation.setOnFinished(event -> {
            EventBusUtil.getDefault().post(new AnimFinishedEvent());
        });
        animation.play();


    }



    public ScaleTransition scaleTransition(double duration, Node node, double byX, double byY) {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(duration), node);
        scaleTransition.setByX(byX);
        scaleTransition.setByY(byY);
        return scaleTransition;
    }


}
