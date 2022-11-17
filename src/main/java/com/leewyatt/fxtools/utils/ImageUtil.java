package com.leewyatt.fxtools.utils;

import com.github.gino0631.icns.IcnsBuilder;
import com.github.gino0631.icns.IcnsIcons;
import com.github.gino0631.icns.IcnsType;
import com.leewyatt.fxtools.model.ImageSize;
import com.leewyatt.fxtools.model.SvgXml;
import com.luciad.imageio.webp.WebPReadParam;
import com.luciad.imageio.webp.WebPWriteParam;
import com.madgag.gif.fmsware.AnimatedGifEncoder;
import com.madgag.gif.fmsware.GifDecoder;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.filters.Canvas;
import net.coobird.thumbnailator.geometry.Positions;
import net.sf.image4j.codec.bmp.BMPDecoder;
import net.sf.image4j.codec.ico.ICOEncoder;
import org.apache.batik.transcoder.*;
import org.apache.batik.transcoder.image.JPEGTranscoder;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;

import javax.imageio.*;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.FileImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.ref.SoftReference;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @author LeeWyatt
 */
public class ImageUtil {
    public static final String FORMAT_PNG = "png";
    public static final String FORMAT_JPG = "jpg";

    public static final String FORMAT_JPEG = "jpeg";
    public static final String FORMAT_SVG = "svg";
    public static final String FORMAT_GIF = "gif";
    public static final String FORMAT_WEBP = "webp";
    public static final String FORMAT_ICO = "ico";
    public static final String FORMAT_BMP = "bmp";

    //JPG编码质量
    public static final float QUALITY_HIGHEST = 1.0F;
    public static final float QUALITY_HIGHER = 0.85F;
    public static final float QUALITY_MEDIA = 0.75F;

    //如果设置不生成预览图,那么使用这个默认图片
    public static final Image DEFAULT_IMAGE = new Image(ImageUtil.class.getResource("/images/default_img.png").toExternalForm());
    //如果图片不能正确解码,或者出现异常,那么显示这个图片
    public static final Image ERROR_IMAGE = new Image(ImageUtil.class.getResource("/images/error.png").toExternalForm());

    public static final Map<String, SoftReference<Image>> IMAGE_CACHE = new HashMap<>();

    private ImageUtil() {

    }

    /**
     *
     * @param node
     * @param imageName
     * @param extensionName
     * @param x2Selected
     * @param x3Selected
     * @param graySelected
     */
    public static void copyNodeSnapshotImagesToClipboard(Node node, String imageName, String extensionName, boolean x2Selected, boolean x3Selected, boolean graySelected) {
        Path tempDirPath = FileUtil.getInstance().getCacheDirectoryPath().resolve(StringUtil.randomStr(32, true, true));
        File tempDir = null;
        try {
            tempDir = Files.createDirectories(tempDirPath).toFile();
        } catch (IOException e) {
            Logger logger = Logger.getLogger("com.leewyatt.fxtools.utils.ImageUtil");
            logger.severe("IOException: SVG Page=>Copy Svg Node as Image.\t"+e);
            e.printStackTrace();
        }
        if (tempDir == null) {
            return;
        }
        tempDir.deleteOnExit();
        List<File> fileList = new ArrayList<>();
        //在临时文件目录创建
        WritableImage image1x = node.snapshot(getSnapshotParameters(), null);
        int w = (int) Math.ceil(image1x.getWidth());
        int h = (int) Math.ceil(image1x.getHeight());
        writeNodeSnapshotToFile(node, extensionName, graySelected, 1, imageName, tempDirPath, fileList, w, h);
        if (x2Selected) {
            writeNodeSnapshotToFile(node, extensionName, graySelected, 2, imageName, tempDirPath, fileList, w, h);
        }
        if (x3Selected) {
            writeNodeSnapshotToFile(node, extensionName, graySelected, 3, imageName, tempDirPath, fileList, w, h);
        }

        ClipboardContent clipboardContent = new ClipboardContent();
        Clipboard fxClipboard = Clipboard.getSystemClipboard();
        fxClipboard.clear();
        clipboardContent.putFiles(fileList);
        fxClipboard.setContent(clipboardContent);

    }

    /**
     *  TODO 可以考虑改成参数缩放, 而不是真实缩放
     *     WritableImage writableImage = new WritableImage((int)Math.rint(pixelScale*canvas.getWidth()), (int)Math.rint(pixelScale*canvas.getHeight()));
     *     SnapshotParameters parame = new SnapshotParameters();
     *     parame.setTransform(Transform.scale(pixelScale, pixelScale));
     *     return canvas.snapshot(parame, writableImage);
     */
    private static void writeNodeSnapshotToFile(Node node, String extensionName, boolean disabledIcon, int magnification, String fileName, Path tempDirPath, List<File> fileList, int w, int h) {
        double initScale = node.getScaleX();
        String ratio = magnification == 1 ? "." : "@" + magnification + "x.";
        File tempFile = tempDirPath.resolve(fileName + ratio + extensionName).toFile();
        fileList.add(tempFile);
        tempFile.deleteOnExit();
        node.setScaleX(initScale * magnification);
        node.setScaleY(initScale * magnification);
        WritableImage fxImage = node.snapshot(getSnapshotParameters(), null);
        node.setScaleX(initScale);
        node.setScaleY(initScale);
        BufferedImage awtImage = SwingFXUtils.fromFXImage(fxImage, null);
        writeNodeSnapshotToFile(extensionName, tempFile, awtImage, w * magnification, h * magnification);
        if (disabledIcon) {
            File disabledTempFile = tempDirPath.resolve(fileName + "_gray" + ratio + extensionName).toFile();
            fileList.add(disabledTempFile);
            disabledTempFile.deleteOnExit();
            writeNodeSnapshotToFile(extensionName, disabledTempFile, convertToGrayscaleImage(awtImage), w * magnification, h * magnification);
        }
    }

    /**
     * 图片写入到文件
     *
     * @param extensionName 后缀
     * @param imgFile       图片文件
     * @param image         图像
     */
    public static void writeNodeSnapshotToFile(String extensionName, File imgFile, BufferedImage image, int w, int h) {
        boolean isJpgType = "jpg".equalsIgnoreCase(extensionName);
        int colorType = isJpgType ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
        BufferedImage destImage = new BufferedImage(w, h, colorType);
        Color bgColor = isJpgType ? Color.WHITE : new Color(0, 0, 0, 0);
        Graphics2D graphics = destImage.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        //不同插值效果. graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR)
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        //ImageUtil.applyQualityRenderingHints(graphics);
        graphics.drawImage(image, 0, 0, w, h, bgColor, null);
        graphics.dispose();
        try {
            OutputStream out = new FileOutputStream(imgFile);
            ImageIO.write(destImage, extensionName, out);
            out.close();
        } catch (IOException e) {
            Logger logger = Logger.getLogger("com.leewyatt.fxtools.utils.ImageUtil");
            logger.severe("IOException: write snapshot image to file failed.\t"+e);
            e.printStackTrace();
        }
    }

    public static BufferedImage convertToGrayscaleImage(BufferedImage image) {
        BufferedImage resultImg = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                int rgb = image.getRGB(i, j);
                int r = (rgb >> 16) & 0xff;
                int g = (rgb >> 8) & 0xff;
                int b = rgb & 0xff;
                int gray = (r * 299 + g * 587 + b * 114 + 500) / 1000;
                rgb = 0xff << 24 | (gray & 0xff) << 16 | (gray & 0xff) << 8 | gray & 0xff;
                resultImg.setRGB(i, j, rgb);
            }
        }
        return resultImg;
    }

    public static WritableImage webpToPngFXImg(String webpPath) {
        BufferedImage bufferedImage = webpToPngImg(webpPath);
        return bufferedImage == null ? null : SwingFXUtils.toFXImage(bufferedImage, null);
    }

    public static BufferedImage webpToPngImg(String webpPath) {
        try {
            ImageReader reader = ImageIO.getImageReadersByMIMEType("image/webp").next();
            WebPReadParam readParam = new WebPReadParam();
            readParam.setBypassFiltering(true);
            FileImageInputStream fileImageInputStream = new FileImageInputStream(new File(webpPath));
            reader.setInput(fileImageInputStream);
            BufferedImage bufferedImage = reader.read(0, readParam);
            fileImageInputStream.flush();
            fileImageInputStream.close();
            reader.dispose();
            return bufferedImage;
        } catch (IOException e) {
            Logger logger = Logger.getLogger("com.leewyatt.fxtools.utils.ImageUtil");
            logger.severe("IOException: webp convert to png image failed.\t"+e);
            e.printStackTrace();
        }
        return null;
    }

    public static WritableImage svgToPngFXImg(String svgPath, int width, int height) {
        BufferedImage bufferedImage = svgToPngImg(svgPath, width, height);
        return bufferedImage == null ? null : SwingFXUtils.toFXImage(bufferedImage, null);
    }

    public static BufferedImage svgToPngImg(String svgPath, int width, int height) {
        Transcoder transcoder = new PNGTranscoder();
        transcoder.addTranscodingHint(PNGTranscoder.KEY_WIDTH, (float) width);
        transcoder.addTranscodingHint(PNGTranscoder.KEY_HEIGHT, (float) height);
        try (FileInputStream inputStream = new FileInputStream(svgPath)) {
            TranscoderInput input = new TranscoderInput(inputStream);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            TranscoderOutput output = new TranscoderOutput(outputStream);
            transcoder.transcode(input, output);
            outputStream.flush();
            outputStream.close();
            return ImageIO.read(new ByteArrayInputStream(outputStream.toByteArray()));
        } catch (IOException e) {
            Logger logger = Logger.getLogger("com.leewyatt.fxtools.utils.ImageUtil");
            logger.severe("IOException: svg to png Img failed.\t"+e);
            e.printStackTrace();
            //TODO fill="" 时会有异常.但能使用...
        } catch (TranscoderException e) {
            Logger logger = Logger.getLogger("com.leewyatt.fxtools.utils.ImageUtil");
            logger.severe("TranscoderException: svg to png Img failed.\t"+e);
        }
        return null;
    }

    private static SnapshotParameters getSnapshotParameters() {
        SnapshotParameters parameters = new SnapshotParameters();
        parameters.setFill(javafx.scene.paint.Color.TRANSPARENT);
        return parameters;
    }

    /**
     * 无法正确解析webp的动图
     * 异常 java.io.IOException: Decode returned code VP8_STATUS_UNSUPPORTED_FEATURE
     */
    public static boolean writeWEBP2File(String webpFilePath, File destFile, float quality, double scale) {
        String destFormat = FileUtil.getFileExtension(destFile.getName());
        ImageReader reader = ImageIO.getImageReadersByMIMEType("image/webp").next();
        WebPReadParam readParam = new WebPReadParam();
        readParam.setBypassFiltering(true);
        try {
            FileImageInputStream fileImageInputStream = new FileImageInputStream(new File(webpFilePath));
            reader.setInput(fileImageInputStream);
            BufferedImage img = reader.read(0, readParam);
            fileImageInputStream.flush();
            fileImageInputStream.close();
            reader.dispose();
            Thumbnails.Builder<BufferedImage> imgBuilder = Thumbnails.of(img)
                    .scale(scale)
                    .allowOverwrite(true);
            if (destFormat.equalsIgnoreCase(FORMAT_ICO)) { //ico
                ICOEncoder.write(imgBuilder.outputFormat("png").asBufferedImage(), destFile);
            } else { //jpg png gif
                if (destFormat.equalsIgnoreCase(FORMAT_JPG)) {
                    imgBuilder
                            .addFilter(new Canvas(img.getWidth(), img.getHeight(), Positions.CENTER, Color.WHITE))
                            .outputQuality(quality)
                            .outputFormat(destFormat)
                            .toFile(destFile);
                } else {
                    imgBuilder
                            .outputFormat(destFormat)
                            .toFile(destFile);
                }
            }
            return true;
        } catch (Exception e) {
            //WEBP动画格式错误
            //System.out.println(webpFilePath);
            //e.printStackTrace();
            Logger logger = Logger.getLogger("com.leewyatt.fxtools.utils.ImageUtil");
            logger.severe("Exception: write WEBP to File failed. webp animation not supported.\t"+e);
            return false;
        }

    }

    /**
     * 写入svg图片到文件
     *
     * @param svgFilePath svg路径
     * @param destFile    输出路径
     * @param quality     只针对JPG编码器有效
     * @return 是否写入成功
     */
    public static boolean writeSVG2File(String svgFilePath, File destFile, float quality, double scale) {
        SvgXml svgXml = SVGUtil.getSvgPath(new File(svgFilePath));
        float width = Math.round(Double.parseDouble(svgXml.getWidth()) * scale);
        float height = Math.round(Double.parseDouble(svgXml.getHeight()) * scale);

        //获得后缀
        String destFormat = FileUtil.getFileExtension(destFile.getName());
        boolean isJPGFormat = destFormat.equalsIgnoreCase(ImageUtil.FORMAT_JPG);
        Transcoder transcoder;
        if (isJPGFormat) {
            transcoder = new JPEGTranscoder();
            transcoder.addTranscodingHint(JPEGTranscoder.KEY_QUALITY, quality);// 默认中等是0.75;
        } else {
            transcoder = new PNGTranscoder();
        }
        transcoder.addTranscodingHint(SVGAbstractTranscoder.KEY_WIDTH, width);
        transcoder.addTranscodingHint(SVGAbstractTranscoder.KEY_HEIGHT, height);
        try (FileInputStream inputStream = new FileInputStream(svgFilePath)) {
            TranscoderInput input = new TranscoderInput(inputStream);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            TranscoderOutput output = new TranscoderOutput(outputStream);
            transcoder.transcode(input, output);
            outputStream.flush();
            outputStream.close();
            byte[] imgData = outputStream.toByteArray();
            if (destFormat.equalsIgnoreCase(FORMAT_ICO)) { //ico
                ICOEncoder.write(ImageIO.read(new ByteArrayInputStream(imgData)), destFile);
            } else if (destFormat.equalsIgnoreCase(FORMAT_WEBP)) { //"webp"
                ImageWriter writer = ImageIO.getImageWritersByMIMEType("image/webp").next();
                WebPWriteParam writeParam = new WebPWriteParam(writer.getLocale());
                writeParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                writeParam.setCompressionType(writeParam.getCompressionTypes()[WebPWriteParam.LOSSLESS_COMPRESSION]);
                FileImageOutputStream fileImageOutputStream = new FileImageOutputStream(destFile);
                writer.setOutput(fileImageOutputStream);
                writer.write(null, new IIOImage(ImageIO.read(new ByteArrayInputStream(imgData)), null, null), writeParam);
                fileImageOutputStream.flush();
                fileImageOutputStream.close();
                writer.dispose();
            } else if (destFormat.equalsIgnoreCase(FORMAT_BMP)) {
                Thumbnails
                        .of(new ByteArrayInputStream(imgData))
                        .scale(1.0)
                        .addFilter(new Canvas((int) width, (int) height, Positions.CENTER, Color.WHITE))
                        .toFile(destFile);
                //BMPEncoder.write(ImageIO.read(new ByteArrayInputStream(imgData)),destFile);
            } else { //png gif jpg
                ImageIO.write(ImageIO.read(new ByteArrayInputStream(imgData)), destFormat, destFile);
            }
            return true;
            //webp 格式

        } catch (Exception e) {
            Logger logger = Logger.getLogger("com.leewyatt.fxtools.utils.ImageUtil");
            logger.warning("Exception: writeSVG2File failed.Usually caused by fill=\"\".\t"+e);
            e.printStackTrace();
            return true;
            //TODO fill="" 时会有异常 (但是也能用..!!)
        }
    }

    public static boolean writePNG2File(String srcFilePath, File destFile, float quality, double scale) {
        String destFormat = FileUtil.getFileExtension(destFile.getName());
        try {
            if (destFormat.equalsIgnoreCase(FORMAT_ICO)) { //ico
                ICOEncoder.write(Thumbnails.of(srcFilePath)
                        .scale(scale)
                        .asBufferedImage(), destFile);
            } else { //jpg png gif
                if (destFormat.equalsIgnoreCase(FORMAT_JPG)) {
                    BufferedImage image = ImageIO.read(new File(srcFilePath));
                    Thumbnails.of(srcFilePath)
                            .scale(scale)
                            .allowOverwrite(true)
                            .addFilter(new Canvas(image.getWidth(), image.getHeight(), Positions.CENTER, Color.WHITE))
                            .outputQuality(quality)
                            .outputFormat(destFormat)
                            .toFile(destFile);
                } else {
                    Thumbnails.of(srcFilePath)
                            .scale(scale)
                            .allowOverwrite(true)
                            .outputFormat(destFormat)
                            .toFile(destFile);
                }
            }
            return true;
        } catch (Exception e) {
            Logger logger = Logger.getLogger("com.leewyatt.fxtools.utils.ImageUtil");
            logger.severe("Exception: writePNG2File failed.\t"+e);
            e.printStackTrace();
            return false;
        }
    }

    public static boolean writeGIF2File(String srcFilePath, File destFile, float quality, double scale) {
        String destFormat = FileUtil.getFileExtension(destFile.getName());
        try {
            GifDecoder decoder = new GifDecoder();
            decoder.read(srcFilePath);
            //第一帧的image 如果没有就是null
            BufferedImage firstImage = decoder.getImage();
            if (firstImage == null) {
                throw new Exception();
            }
            int w = firstImage.getWidth();
            int h = firstImage.getHeight();
            if (destFormat.equalsIgnoreCase(FORMAT_ICO)) { //ico
                BufferedImage image = Thumbnails.of(firstImage)
                        .scale(scale)
                        .imageType(BufferedImage.TYPE_INT_ARGB)
                        .asBufferedImage();
                ICOEncoder.write(image, destFile);
            } else if (destFormat.equalsIgnoreCase(FORMAT_JPG)) { //jpg
                Thumbnails.of(firstImage)
                        .scale(scale)
                        .imageType(BufferedImage.TYPE_INT_ARGB)
                        .addFilter(new Canvas((int) (w * scale), (int) (h * scale), Positions.CENTER, Color.WHITE))
                        .outputQuality(quality)
                        .outputFormat(destFormat)
                        .allowOverwrite(true)
                        .toFile(destFile);
            } else if (destFormat.equalsIgnoreCase(FORMAT_GIF)) { //gif 部分gif 正常读取宽高都是 -1x-1
                Thumbnails.of(firstImage)
                        .scale(scale)
                        .imageType(BufferedImage.TYPE_INT_ARGB)
                        .outputFormat(destFormat)
                        .allowOverwrite(true)
                        .toFile(destFile);
            } else {// png
                Thumbnails.of(firstImage)
                        .scale(scale)
                        .imageType(BufferedImage.TYPE_INT_ARGB)
                        .outputFormat(destFormat)
                        .allowOverwrite(true)
                        .toFile(destFile);
            }
            return true;
        } catch (Exception e) {
            Logger logger = Logger.getLogger("com.leewyatt.fxtools.utils.ImageUtil");
            logger.severe("Exception: writeGIF2File failed.\t"+e);
            e.printStackTrace();
            return false;
        }
    }

    public static boolean writeJPG2File(String srcFilePath, File destFile, float quality, double scale) {
        String destFormat = FileUtil.getFileExtension(destFile.getName());
        try {
            if (destFormat.equalsIgnoreCase(FORMAT_ICO)) { //ico
                BufferedImage image = Thumbnails.of(srcFilePath)
                        .scale(scale)
                        .asBufferedImage();
                ICOEncoder.write(image, destFile);
            } else {
                Thumbnails.of(srcFilePath)
                        .scale(scale)
                        .imageType(BufferedImage.TYPE_INT_ARGB)
                        .outputFormat(destFormat)
                        .outputQuality(quality)
                        .allowOverwrite(true)
                        .toFile(destFile);
            }
            return true;
        } catch (Exception e) {
            Logger logger = Logger.getLogger("com.leewyatt.fxtools.utils.ImageUtil");
            logger.severe("Exception: writeJPG2File failed.\t"+e);
            e.printStackTrace();
            return false;
        }
    }

    public static boolean writeBMP2File(String srcFilePath, File destFile, float quality, double scale) {
        String destFormat = FileUtil.getFileExtension(destFile);
        try {
            BufferedImage image = BMPDecoder.read(new File(srcFilePath));
            if (destFormat.equalsIgnoreCase(FORMAT_ICO)) { //ico
                BufferedImage scaledImage = Thumbnails.of(image)
                        .scale(scale)
                        .allowOverwrite(true)
                        .asBufferedImage();
                ICOEncoder.write(scaledImage, destFile);
            } else {
                Thumbnails.of(image)
                        .scale(scale)
                        .outputFormat(destFormat)
                        .outputQuality(quality)
                        .allowOverwrite(true)
                        .toFile(destFile);
            }
            return true;
        } catch (Exception e) {
            Logger logger = Logger.getLogger("com.leewyatt.fxtools.utils.ImageUtil");
            logger.severe("Exception: writeBMP2File failed.\t"+e);
            e.printStackTrace();
            return false;
        }
    }

    public static ImageSize computeImageSize(File imageFile) {
        String format = FileUtil.getFileExtension(imageFile);
        if (ImageUtil.FORMAT_SVG.equals(format)) {
            SvgXml svgPath = SVGUtil.getSvgPath(imageFile);
            if ("Error".equalsIgnoreCase(svgPath.getWidth()) || "Error".equalsIgnoreCase(svgPath.getHeight())) {
                return new ImageSize(-1, -1);
            }            //FXHandler.LOGGER.log(Level.SEVERE,e.getMessage());

            int width = (int) Math.ceil(Double.parseDouble(svgPath.getWidth()));
            int height = (int) Math.ceil(Double.parseDouble(svgPath.getHeight()));
            return new ImageSize(width, height);
        } else if (ImageUtil.FORMAT_WEBP.equals(format)) {
            try {
                BufferedImage bufferedImage = ImageIO.read(imageFile);
                if (bufferedImage == null) {
                    return new ImageSize(-1, -1);
                } else {
                    return new ImageSize(bufferedImage.getWidth(), bufferedImage.getHeight());
                }
            } catch (Exception e) {
                Logger logger = Logger.getLogger("com.leewyatt.fxtools.utils.ImageUtil");
                logger.severe("Exception: computeImageSize failed.\t"+e);
                return new ImageSize(-1, -1);
            }
        } else {
            try {
                Dimension imageSize = Imaging.getImageSize(imageFile);
                return new ImageSize((int) imageSize.getWidth(), (int) imageSize.getHeight());
            } catch (ImageReadException | IOException e) {
                if (imageFile.getName().toLowerCase().endsWith(".gif")) {
                    return computeGifSize(imageFile);
                }
                //异常时,-1 -1 图片不会被缩小 (会占满较大的布局空间)
                return new ImageSize(-1, -1);
            }
        }
    }

    public static ImageSize computeGifSize(File gifImageFile) {
        if (gifImageFile.getName().toLowerCase().endsWith(".gif")) {
            GifDecoder decoder = new GifDecoder();
            int read = decoder.read(gifImageFile.getPath());
            int width = (int) decoder.getFrameSize().getWidth();
            int height = (int) decoder.getFrameSize().getHeight();
            return new ImageSize(width, height);
        }
        //异常时, -1 -1
        return new ImageSize(-1, -1);
    }

    public static boolean writeImage2File(String srcPath, File destFile, float jpgQuality, double scale) {
        String format = FileUtil.getFileExtension(srcPath);
        String destFormat = FileUtil.getFileExtension(destFile);
        if (destFormat.equalsIgnoreCase(format) && Double.compare(1.0, scale) == 0) {
            FileUtil.copyFile(new File(srcPath), destFile);
            return true;
            //文件copy 过去
        } else if (FORMAT_SVG.equalsIgnoreCase(format)) {
            return writeSVG2File(srcPath, destFile, jpgQuality, scale);
        } else if (FORMAT_WEBP.equalsIgnoreCase(format)) {
            return writeWEBP2File(srcPath, destFile, jpgQuality, scale);
        } else if (FORMAT_PNG.equalsIgnoreCase(format)) {
            return writePNG2File(srcPath, destFile, jpgQuality, scale);
        } else if (FORMAT_GIF.equalsIgnoreCase(format)) {
            return writeGIF2File(srcPath, destFile, jpgQuality, scale);
        } else if (FORMAT_JPG.equalsIgnoreCase(format) || FORMAT_JPEG.equalsIgnoreCase(format)) {
            return writeJPG2File(srcPath, destFile, jpgQuality, scale);
        } else if (FORMAT_BMP.equalsIgnoreCase(format)) {
            return writeBMP2File(srcPath, destFile, jpgQuality, scale);
        }
        return false;
    }

    /**
     * webp 和 svg 直接保存缩略图; 避免每次都要转换一次
     * png jpg bmp gif 如果图片尺寸大于50*50,那么也保存一张缩略图
     * 如果尺寸小于50*50, 那么原图的路径,就是缓存图片的路径
     *
     * @return 缓存图片的url
     */
    public static String writeCacheImage(int imageWidth, int imageHeight, String format, String srcPath) {
        double scale = NumberUtil.computeScaleRate(imageWidth, imageHeight, 50);
        // SVG 和 WEBP 不能直接显示在FX中,需要一张缓存图片
        if (FORMAT_SVG.equals(format) || FORMAT_WEBP.equals(format)) {
            //保存一张缩小的缓存图片
            File cacheFile = FileUtil.getInstance().getCacheDirectoryPath().resolve(StringUtil.randomStr(32, true, true) + ".png").toFile();
            cacheFile.deleteOnExit();
            if (FORMAT_SVG.equals(format)) {
                writeSVG2File(srcPath, cacheFile, 1.0F, scale);
            } else {
                writeWEBP2File(srcPath, cacheFile, 1.0F, scale);
            }
            return FileUtil.getUrl(cacheFile);
        } else {
            if (scale < 1.0) {
                File cacheFile = FileUtil.getInstance().getCacheDirectoryPath().resolve(StringUtil.randomStr(32, true, true) + "." + format).toFile();
                cacheFile.deleteOnExit();
                writeImage2File(srcPath, cacheFile, 1.0F, scale);
                return FileUtil.getUrl(cacheFile);
            } else {
                return FileUtil.getUrl(new File(srcPath));
            }
        }
    }

    /**
     * gif图片 拆分成png图片
     *
     * @param gifPath    gif的路径
     * @param outDirPath 输出文件夹 (文件夹下要存放多个解码出来的图片)
     */
    public static void splitGif(String gifPath, String outDirPath) {
        GifDecoder decoder = new GifDecoder();
        int read = decoder.read(gifPath);
        if (read != 0) {
            return;
        }
        int n = decoder.getFrameCount(); //得到帧数
        try {
            for (int i = 0; i < n; i++) {
                BufferedImage frame = decoder.getFrame(i); //得到帧
                //int delay = decoder.getDelay(i); //获取当前帧的显示时间
                String outPath = outDirPath + File.separator + i + ".png";
                FileOutputStream out = new FileOutputStream(outPath);
                ImageIO.write(frame, "png", out);
                out.flush();
                out.close();
            }
        } catch (Exception e) {
            Logger logger = Logger.getLogger("com.leewyatt.fxtools.utils.ImageUtil");
            logger.severe("Exception: splitGif failed.\t"+e);
            e.printStackTrace();
        }
    }

    /**
     * TODO 背景色似乎不能做到透明
     * 把多张png图片合成一张
     *
     * @param imagesPath  多个png文件路径
     * @param outFilePath 生成的gif文件路径
     */
    public static void imagesToGif(String[] imagesPath, String outFilePath) {
        AnimatedGifEncoder gifEncoder = new AnimatedGifEncoder();
        //尝试背景透明好像无效
        //gifEncoder.setBackground(null);
        //gifEncoder.setBackground(new java.awt.Color(0,0,0,0));
        //动画播放次数, 默认是1次,0代表无限次,循环播放
        gifEncoder.setRepeat(0);
        gifEncoder.start(outFilePath);
        int len = imagesPath.length;
        BufferedImage[] src = new BufferedImage[len];
        try {
            for (int i = 0; i < len; i++) {
                //设置播放下一帧的延迟时间 毫秒
                gifEncoder.setDelay(160);
                src[i] = Thumbnails.of(imagesPath[i])
                        .scale(1.0)
                        .imageType(BufferedImage.TYPE_INT_ARGB)
                        .asBufferedImage();
                gifEncoder.addFrame(src[i]);  //添加帧到gif编码器
            }

        } catch (Exception e) {
            Logger logger = Logger.getLogger("com.leewyatt.fxtools.utils.ImageUtil");
            logger.severe("Exception: imagesToGif failed.\t"+e);
            e.printStackTrace();
        }
        gifEncoder.finish();
    }

    //public static BufferedImage readImage(String srcPath, int width, int height) {
    //    String destFormat = FileUtil.getFileExtension(srcPath);
    //    //GIF
    //    if (destFormat.equals(FORMAT_GIF)) {
    //        GifDecoder decoder = new GifDecoder();
    //        decoder.read(srcPath);
    //        return decoder.getImage();
    //    }
    //    //webp
    //    if (destFormat.equals(FORMAT_WEBP)) {
    //        try {
    //            return ImageIO.read(new File(srcPath));
    //        } catch (IOException e) {
    //            Logger logger = Logger.getLogger("com.leewyatt.fxtools.utils.ImageUtil");
    //            logger.severe("IOException: readImage failed.\t"+e);
    //            throw null;
    //        }
    //    }
    //    //png
    //    if (destFormat.equals(FORMAT_PNG)) {
    //
    //    }
    //
    //    return null;
    //}

    public static Image getCacheImage(String imgUrl) {
        SoftReference<Image> compute = ImageUtil.IMAGE_CACHE.compute(imgUrl, (key, value) -> {
            Image image = null;
            if (value != null) {
                image = value.get();
            }
            if (image == null) {
                if (key != null) {
                    image = new Image(key, true);
                    value = new SoftReference<>(image);
                } else {
                    return new SoftReference<>(DEFAULT_IMAGE);
                }
            }
            return value;
        });
        return compute.get();
    }

    public static BufferedImage loadImage(String filePath, int requestWidth, int requestHeight) {
        String destFormat = FileUtil.getFileExtension(filePath);
        BufferedImage image;
        if (ImageUtil.FORMAT_WEBP.equals(destFormat)) {
            BufferedImage img = webpToPngImg(filePath);
            image = img == null ? null : scaleToSize(img, requestWidth, requestHeight);
        } else {
            if (ImageUtil.FORMAT_SVG.equals(destFormat)) {
                BufferedImage img = svgToPngImg(filePath, requestWidth, requestHeight);
                image = img == null ? null : scaleToSize(img, requestWidth, requestHeight);
            } else if (ImageUtil.FORMAT_GIF.equals(destFormat)) {
                GifDecoder gifDecoder = new GifDecoder();
                int read = gifDecoder.read(filePath);
                image = scaleToSize(gifDecoder.getImage(), requestWidth, requestHeight);
            } else {
                try {
                    image = scaleToSize(ImageIO.read(new File(filePath)), requestWidth, requestHeight);
                } catch (IOException e) {
                    image = null;
                    Logger logger = Logger.getLogger("com.leewyatt.fxtools.utils.ImageUtil");
                    logger.severe("IOException: loadImage failed.\t"+e);
                    e.printStackTrace();
                }
            }
        }
        return image;
    }

    private static BufferedImage scaleToSize(BufferedImage img, int requestWidth, int requestHeight) {
        int width = img.getWidth();
        int height = img.getHeight();
        if (requestWidth == width || requestHeight == height) {
            return img;
        }
        try {
            return Thumbnails.of(img).width(requestWidth).height(requestHeight).outputFormat(FORMAT_PNG).asBufferedImage();
        } catch (IOException e) {
            Logger logger = Logger.getLogger("com.leewyatt.fxtools.utils.ImageUtil");
            logger.severe("IOException: scaleToSize failed.\t"+e);
            throw new RuntimeException(e);
        }
    }

    public static InputStream scaleImageToStream(BufferedImage image, int size) throws IOException {
        BufferedImage bufferedImage = Thumbnails.of(image)
                .width(size)
                .height(size)
                .asBufferedImage();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", os);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(os.toByteArray());
        os.flush();
        os.close();
        return inputStream;
    }

    public static void writeScaleImage2File(File file, BufferedImage srcImg, int size) throws IOException {
        Thumbnails.of(srcImg)
                .width(size)
                .height(size)
                .toFile(file);
    }

    public static BufferedImage resizeImageBySize(BufferedImage srcImg, int size) throws IOException {
        return Thumbnails.of(srcImg)
                .width(size)
                .height(size)
                .asBufferedImage();
    }

    public static boolean writeIcns(Path path, BufferedImage awtImage) {
        try (IcnsBuilder builder = IcnsBuilder.getInstance()) {
            builder.add(IcnsType.ICNS_16x16_JPEG_PNG_IMAGE, scaleImageToStream(awtImage, 16))
                    .add(IcnsType.ICNS_32x32_JPEG_PNG_IMAGE, scaleImageToStream(awtImage, 32))
                    .add(IcnsType.ICNS_128x128_JPEG_PNG_IMAGE, scaleImageToStream(awtImage, 128))
                    .add(IcnsType.ICNS_256x256_JPEG_PNG_IMAGE, scaleImageToStream(awtImage, 256))
                    .add(IcnsType.ICNS_512x512_JPEG_PNG_IMAGE, scaleImageToStream(awtImage, 512))
                    .add(IcnsType.ICNS_16x16_2X_JPEG_PNG_IMAGE, scaleImageToStream(awtImage, 16 * 2))
                    .add(IcnsType.ICNS_32x32_2X_JPEG_PNG_IMAGE, scaleImageToStream(awtImage, 32 * 2))
                    .add(IcnsType.ICNS_128x128_2X_JPEG_PNG_IMAGE, scaleImageToStream(awtImage, 128 * 2))
                    .add(IcnsType.ICNS_256x256_2X_JPEG_PNG_IMAGE, scaleImageToStream(awtImage, 256 * 2))
                    .add(IcnsType.ICNS_1024x1024_2X_JPEG_PNG_IMAGE, scaleImageToStream(awtImage, 512 * 2));
            try (IcnsIcons builtIcons = builder.build()) {
                try (OutputStream os = Files.newOutputStream(path)) {
                    builtIcons.writeTo(os);
                    return true;
                }
            }
        } catch (IOException e) {
            Logger logger = Logger.getLogger("com.leewyatt.fxtools.utils.ImageUtil");
            logger.severe("IOException: writeIcns failed.\t"+e);
            e.printStackTrace();
            return false;
        }
    }

}
