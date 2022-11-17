package com.leewyatt.fxtools.uicontroller;

import javafx.scene.control.TextFormatter;

import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

/**
 * 文本框格式化器-让文本框只能输入数字
 */
public class IntegerFilter implements UnaryOperator<TextFormatter.Change> {
    private final static Pattern NUMBER_PATTERN = Pattern.compile("\\d*");

    @Override
    public TextFormatter.Change apply(TextFormatter.Change change) {
        return NUMBER_PATTERN.matcher(change.getText()).matches() ? change : null;
    }

}