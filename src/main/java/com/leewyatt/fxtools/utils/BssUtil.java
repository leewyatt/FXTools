package com.leewyatt.fxtools.utils;

import javafx.css.Stylesheet;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class BssUtil {
    public static boolean convertToBss(List<File> files) throws IOException {
        files = files.stream().filter(file -> file.getAbsolutePath().endsWith("css")).collect(Collectors.toList());
        for (File it : files) {

            File bssFile = new File(it.getAbsolutePath().replace(".css", ".bss"));
            Stylesheet.convertToBinary(it, bssFile);
            it.delete();
        }

        return true;
    }

    public static boolean convertToBss(File file) throws IOException {
        File bssFile = new File(file.getAbsolutePath().replace(".css", ".bss"));
        Stylesheet.convertToBinary(file, bssFile);
        file.delete();

        return true;
    }
}