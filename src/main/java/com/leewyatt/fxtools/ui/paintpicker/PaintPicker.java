/*
 * Copyright (c) 2012, 2014, Oracle and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 *
 * This file is available and licensed under the following license:
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  - Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the distribution.
 *  - Neither the name of Oracle Corporation nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.leewyatt.fxtools.ui.paintpicker;

import com.leewyatt.fxtools.ui.paintpicker.colorpicker.ColorPicker;
import com.leewyatt.fxtools.ui.stages.ScreenColorPickerStage;
import com.leewyatt.fxtools.utils.OSUtil;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;

/**
 * Paint editor control.
 */
public class PaintPicker extends Pane {
    private VBox root_vbox;
    private ToggleButton colorToggleButton;
    private ToggleButton linearToggleButton;
    private ToggleButton radialToggleButton;

    private final PaintPickerController controller;

    public PaintPicker(Delegate delegate) {

        ToggleGroup modeSwitch = new ToggleGroup();

        colorToggleButton = new ToggleButton("Color");
        colorToggleButton.getStyleClass().add("left-pill");
        HBox.setHgrow(colorToggleButton, Priority.ALWAYS);

        linearToggleButton = new ToggleButton("Linear Gradient");
        linearToggleButton.getStyleClass().add("center-pill");
        HBox.setHgrow(linearToggleButton, Priority.ALWAYS);

        radialToggleButton = new ToggleButton("Radial Gradient");
        radialToggleButton.getStyleClass().add("right-pill");
        HBox.setHgrow(radialToggleButton, Priority.ALWAYS);

        modeSwitch.getToggles().addAll(colorToggleButton, linearToggleButton, radialToggleButton);
        Region region = new Region();
        region.setPrefSize(16, 16);
        region.setMaxSize(16, 16);
        region.setMinSize(16, 16);
        Button button = new Button("", region);
        button.getStyleClass().addAll("region-btn", "picker-btn");
        HBox hBox = new HBox(button,colorToggleButton, linearToggleButton, radialToggleButton);
        HBox.setMargin(button,new Insets(0,10,0,0));
        hBox.setAlignment(Pos.CENTER_LEFT);
        button.setOnAction(event -> {
            new ScreenColorPickerStage().showStage();
        });
        //如果不是windows操作系统,那么不显示
        if (OSUtil.getOS() != OSUtil.OS.WINDOWS) {
            button.setVisible(false);
            button.setManaged(false);
        }
        root_vbox = new VBox(hBox);
        //*<VBox fx:id="root_vbox" alignment="CENTER" minHeight="-1.0" prefHeight="-1.0" prefWidth="-1.0" spacing="5.0" xmlns="http://javafx.com/javafx/8" xmlns:fx="http://javafx.com/fxml/1" fx:controller="com.oracle.javafx.scenebuilder.kit.util.control.paintpicker.PaintPickerController">
        root_vbox.setAlignment(Pos.CENTER);
        root_vbox.setMinHeight(-1.0);
        root_vbox.setPrefHeight(-1.0);
        root_vbox.setPrefWidth(-1.0);
        root_vbox.setSpacing(5.0);
        getChildren().add(root_vbox);


        controller = new PaintPickerController(root_vbox,colorToggleButton, linearToggleButton, radialToggleButton);
        this.controller.setDelegate(delegate);
        controller.getColorPicker().getBottomPane().setPaintPickerController(controller);
    }


    public PaintPicker(Delegate delegate, Mode mode) {
        this(delegate);
        controller.setSingleMode(mode);
    }

    public final ObjectProperty<Paint> paintProperty() {
        return controller.paintProperty();
    }

    public final void setPaintProperty(Paint value) {
        // Update model
        controller.setPaintProperty(value);
        // Update UI
        controller.updateUI(value);
    }

    public final Paint getPaintProperty() {
        return controller.getPaintProperty();
    }

    public final ReadOnlyBooleanProperty liveUpdateProperty() {
        return controller.liveUpdateProperty();
    }

    public boolean isLiveUpdate() {
        return controller.isLiveUpdate();
    }

    public static interface Delegate {
        public void handleError(String warningKey, Object... arguments);
    }

    public ColorPicker getColorPicker() {
        return controller.getColorPicker();
    }
}
