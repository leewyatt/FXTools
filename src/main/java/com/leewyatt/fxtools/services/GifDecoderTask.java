package com.leewyatt.fxtools.services;

import com.leewyatt.fxtools.utils.ToolSettingsUtil;
import com.leewyatt.fxtools.utils.OSUtil;
import com.madgag.gif.fmsware.GifDecoder;
import javafx.application.Platform;
import javafx.concurrent.Task;
import net.coobird.thumbnailator.Thumbnails;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Logger;

/**
 * @author LeeWyatt
 */
public class GifDecoderTask extends Task<Void> {

    private String gifPath;
    private String exportPath;
    private String prefix;
    private String suffix;
    private double scale;

    private boolean isSingleImage;

    public GifDecoderTask(String gifPath, String exportPath, String prefix, String suffix, double scale, boolean isSingleImage) {
        this.gifPath = gifPath;
        this.exportPath = exportPath;
        this.prefix = prefix;
        this.suffix = suffix;
        this.scale = scale;
        this.isSingleImage = isSingleImage;
    }

    @Override
    protected Void call() throws Exception {
        GifDecoder decoder = new GifDecoder();
        //判断状态码 0 代表正常无错误
        int read = decoder.read(gifPath);
        if (read != 0) {
            return null;
        }
        //获取帧数
        int n = decoder.getFrameCount();
        int width = (int) (decoder.getFrameSize().width * scale);
        int height = (int) (decoder.getFrameSize().height * scale);

        if (isSingleImage) {
            int totalWidth = n * width;
            BufferedImage result = new BufferedImage(totalWidth, height, BufferedImage.TYPE_INT_ARGB);
            Graphics graphics = result.getGraphics();
            for (int i = 0; i < n; i++) {
                BufferedImage bfImg = Thumbnails.of(decoder.getFrame(i)).scale(scale).asBufferedImage();
                graphics.drawImage(bfImg, (i % n) * width, (i / n) * height, null);
                updateProgress(i, n);
            }
            graphics.dispose();
            try {
                OutputStream out = new FileOutputStream(exportPath);
                ImageIO.write(result, "png", out);
                out.flush();
                out.close();
            } catch (IOException e) {
                Logger logger = Logger.getLogger("com.leewyatt.fxtools.services.GifDecoderTask");
                logger.severe("IOException: GifDecoder failed.\t"+e);
                e.printStackTrace();
            }

            doTaskCompleteAction(exportPath);

        } else {
            //真实的输出路径
            File realExportDir = null;
            int x = 0;
            do {
                realExportDir = new File(exportPath + File.separator + "gif_pngs" + (x == 0 ? "" : "(" + x + ")"));
                x++;
            } while (realExportDir.exists());
            boolean mkdir = realExportDir.mkdir();
            if (!mkdir) {
                return null;
            }
            try {
                String outPath =null ;
                for (int i = 0; i < n; i++) {
                    //获取当前帧
                    BufferedImage frame = Thumbnails.of(decoder.getFrame(i)).scale(scale).asBufferedImage();
                    //输出路径
                    outPath = realExportDir.getPath() + File.separator + prefix + i + suffix + ".png";
                    FileOutputStream out = new FileOutputStream(outPath);
                    ImageIO.write(frame, "png", out);
                    out.flush();
                    out.close();
                    updateProgress(i, n);
                }
                if ( outPath==null) {
                    return null;
                }

                doTaskCompleteAction(outPath);

            } catch (Exception e) {
                Logger logger = Logger.getLogger("com.leewyatt.fxtools.services.GifDecoderTask");
                logger.severe("Exception: GifDecoder (multiple Pictures) failed.\t"+e);
                e.printStackTrace();
            }
        }

        return null;
    }

    private void doTaskCompleteAction(String path) {
        Platform.runLater(()->{
            boolean needOpen = ToolSettingsUtil.getInstance().getTaskCompleteOpenFile();
            if (!needOpen) {
                return;
            }
            if (isSingleImage) {
                OSUtil.showDoc(exportPath);
            }else {
                OSUtil.openAndSelectedFile(path);
            }
        });
    }
}
