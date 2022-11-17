package com.leewyatt.fxtools.uicontroller;

import com.leewyatt.fxtools.event.EventBusUtil;
import com.leewyatt.fxtools.event.PageJumpEvent;
import com.leewyatt.fxtools.model.ImageSize;
import com.leewyatt.fxtools.services.GifDecoderService;
import com.leewyatt.fxtools.ui.alert.InformationAlert;
import com.leewyatt.fxtools.ui.paintpicker.DoubleTextField;
import com.leewyatt.fxtools.utils.FileUtil;
import com.leewyatt.fxtools.utils.ImageUtil;
import com.leewyatt.fxtools.utils.StringUtil;
import com.leewyatt.fxtools.utils.OSUtil;
import com.leewyatt.rxcontrols.controls.RXToggleButton;
import com.leewyatt.rxcontrols.utils.StyleUtil;
import com.madgag.gif.fmsware.GifDecoder;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import org.controlsfx.control.MaskerPane;

import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static com.leewyatt.fxtools.utils.ResourcesUtil.message;
/**
 * @author LeeWyatt
 */
public class ImageGifDecoderPageController {

    @FXML
    private Pane centerPane;

    @FXML
    private StackPane doublePane;
    @FXML
    private Pane gifViewWrapper;
    @FXML
    private ImageView gifImagView;

    @FXML
    private GridPane gridPane;

    @FXML
    private Label frameCountLabel;

    @FXML
    private Label sizeLabel;

    @FXML
    private TextField prefixField;

    @FXML
    private TextField suffixField;

    @FXML
    private Label lengthLabel;

    @FXML
    private MaskerPane maskerPane;

    @FXML
    private RadioButton singleImageRbtn;
    private DoubleTextField scaleField;

    @FXML
    private Button addImagesBtn;

    @FXML
    private ToggleGroup gifBgGroup;

    @FXML
    private RXToggleButton gifBgTransparentBtn;

    /**
     * gif的最佳宽高252*252
     */
    private static  final int FIT_SIZE = 248;


    public static final int WRAPPER_SIZE = 252;

    private String lastOpenDirPath;

    private String lastSaveDirPath;

    private String gifPath;
    private ChangeListener<Number> numberChangeListener;

    @FXML
    void initialize() {
        scaleField = new DoubleTextField();
        scaleField.setText("1.0");
        gridPane.add(scaleField, 1, 4);
        gifViewWrapper.setOnDragDropped(this::onDragDroppedImage);
        gifViewWrapper.setOnDragOver(this::onDragOverImage);
        gifBgGroup.selectedToggleProperty().addListener((ob, ov, nv) ->{
            ObservableList<String> styleClass = ((RXToggleButton)nv).getStyleClass();
            String bgCss = "";
            for (String aClass : styleClass) {
                if (aClass.startsWith("svg-bg")) {
                    bgCss = aClass;
                    break;
                }
            }
            StyleUtil.removeClass(gifViewWrapper, "svg-bg-translucent", "svg-bg-transport", "svg-bg-white", "svg-bg-black", "svg-bg-gray");
            StyleUtil.addClass(gifViewWrapper, bgCss);
        });
        gifBgTransparentBtn.setSelected(true);

    }

    private void onDragDroppedImage(DragEvent event) {
        Dragboard db = event.getDragboard();
        boolean success = false;
        if (db.hasFiles()) {
            List<File> files = event.getDragboard().getFiles();
            files = files.stream().filter(this::isImageCompliantFormat).collect(Collectors.toList());
            if (!files.isEmpty()) {
                loadGifImage(files.get(0));
            }
            success = true;
        }
        event.setDropCompleted(success);
        event.consume();
    }

    private void onDragOverImage(DragEvent event) {
        if (event.getGestureSource() != gifViewWrapper
                && event.getDragboard().hasFiles()) {
            List<File> files = event.getDragboard().getFiles();
            for (File file : files) {
                if (isImageCompliantFormat(file)) {
                    event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                    break;
                }
            }
        }
        event.consume();
    }

    private boolean isImageCompliantFormat(File file) {
        String name = file.getName().toLowerCase(Locale.ROOT);
        return file.isFile() && (name.endsWith(".gif"));
    }

    @FXML
    void addImageFilesAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        if (lastOpenDirPath != null) {
            fileChooser.setInitialDirectory(new File(lastOpenDirPath));
        }
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(message("imagePage.format.chooserDes"),
                "*.gif", "*.GIF"));
        File file = fileChooser.showOpenDialog(gifImagView.getScene().getWindow());
        if (file == null) {
            return;
        }
        loadGifImage(file);

    }

    private void loadGifImage(File file) {
        if (gifImagView.getImage() != null) {
            gifImagView.getImage().progressProperty().removeListener(numberChangeListener);
        }
        gifPath = file.getPath();
        lastOpenDirPath = file.getParent();
        ImageSize imageSize = ImageUtil.computeImageSize(file);
        int width = imageSize.getWidth();
        int height = imageSize.getHeight();
        sizeLabel.setText(width + " x " + height);
        lengthLabel.setText(StringUtil.formatFileSize(file.length()));
        GifDecoder gifDecoder = new GifDecoder();
        gifDecoder.read(file.getPath());
        frameCountLabel.setText(String.valueOf(gifDecoder.getFrameCount()));
        Image image;

        if (width > FIT_SIZE || height > FIT_SIZE) {
            image = new Image(FileUtil.getUrl(file), FIT_SIZE, FIT_SIZE, true, true, true);
            gifImagView.setFitWidth(FIT_SIZE);
            gifImagView.setFitHeight(FIT_SIZE);
        } else {
            image = new Image(FileUtil.getUrl(file), true);
            gifImagView.setFitWidth(width);
            gifImagView.setFitHeight(height);
        }
        gifImagView.setImage(image);
        numberChangeListener = (ob, ov, nv) -> {
            if (nv.intValue() == 1) {
                gifImagView.setLayoutX((WRAPPER_SIZE - image.getWidth()) / 2.0);
                gifImagView.setLayoutY((WRAPPER_SIZE - image.getHeight()) / 2.0);
            }
        };
        image.progressProperty().addListener(numberChangeListener);
    }

    @FXML
    void clearImageListAction(MouseEvent event) {
        gifPath = null;
        gifImagView.setImage(null);
        lengthLabel.setText("");
        sizeLabel.setText("");
        frameCountLabel.setText("");
        prefixField.setText("");
        suffixField.setText("");
        scaleField.setText("1.0");
    }

    @FXML
    void onClickBackBtn(ActionEvent event) {
        EventBusUtil.getDefault().post(new PageJumpEvent(0));
    }

    @FXML
    void onGifDecoderAction(ActionEvent event) {
        if (gifPath == null) {
            new InformationAlert(message("alert.addImage")).showAndWait();
            return;
        }
        double scale = scaleField.getValue();
        if (scale <= 0) {
            new InformationAlert(message("alert.scaleGreaterThan0")).showAndWait();
            return;
        }

        GifDecoderService service;
        if (singleImageRbtn.isSelected()) {
            FileChooser fileChooser = new FileChooser();
            if (lastSaveDirPath != null) {
                fileChooser.setInitialDirectory(new File(lastSaveDirPath));
            }
            fileChooser.setInitialFileName(FileUtil.getNameWithoutExtension(gifPath));
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PNG File", "*.png"));
            File file = fileChooser.showSaveDialog(singleImageRbtn.getScene().getWindow());
            if (file == null) {
                return;
            }
            lastSaveDirPath = file.getParent();
            service = new GifDecoderService(gifPath, file.getPath(), prefixField.getText(), suffixField.getText(), scale, true);
        } else {
            DirectoryChooser dirChooser = new DirectoryChooser();
            if (lastSaveDirPath != null) {
                dirChooser.setInitialDirectory(new File(lastSaveDirPath));
            }
            File file = dirChooser.showDialog(singleImageRbtn.getScene().getWindow());
            if (file == null) {
                return;
            }
            lastSaveDirPath = file.getPath();
            service = new GifDecoderService(gifPath, file.getPath(), prefixField.getText(), suffixField.getText(), scale, false);
        }
        maskerPane.visibleProperty().unbind();
        maskerPane.progressProperty().unbind();
        maskerPane.visibleProperty().bind(service.runningProperty());
        maskerPane.progressProperty().bind(service.progressProperty());
        service.start();

    }

    @FXML
    void onClickGifOutPane(MouseEvent event) {
        if (event.isControlDown() && gifPath!=null) {
            OSUtil.openAndSelectedFile(gifPath);
        }else {
            addImagesBtn.fire();
        }
        event.consume();

    }
}