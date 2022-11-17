package com.leewyatt.fxtools.uicontroller;

import com.google.common.eventbus.Subscribe;
import com.leewyatt.fxtools.FXToolsApp;
import com.leewyatt.fxtools.event.AppIconCreatedEvent;
import com.leewyatt.fxtools.event.EventBusUtil;
import com.leewyatt.fxtools.event.PageJumpEvent;
import com.leewyatt.fxtools.services.TaskCreateAndroidIcon;
import com.leewyatt.fxtools.services.TaskCreateAppleIcon;
import com.leewyatt.fxtools.services.TaskCreateLinuxIcon;
import com.leewyatt.fxtools.services.TaskCreateWinIcon;
import com.leewyatt.fxtools.ui.alert.InformationAlert;
import com.leewyatt.fxtools.utils.ImageUtil;
import com.leewyatt.fxtools.utils.ToolSettingsUtil;
import com.leewyatt.fxtools.utils.OSUtil;
import com.leewyatt.rxcontrols.controls.RXToggleButton;
import com.leewyatt.rxcontrols.utils.StyleUtil;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import org.controlsfx.control.MaskerPane;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static com.leewyatt.fxtools.utils.ResourcesUtil.message;

/**
 * @author LeeWyatt
 */
public class AppIconController {

    public static final String WIN_10 = "win10+";
    public static final String WIN_VISTA = "vista-win8";
    public static final String WIN_XP = "winXP";

    @FXML
    private ComboBox<String> winosComboBox;
    @FXML
    private RadioButton linux512Rbtn;

    @FXML
    private RadioButton linuxAllRbtn;

    @FXML
    private Button addImagesBtn;

    @FXML
    private CheckBox androidCheckBox;

    @FXML
    private TextField androidIconNameField;

    @FXML
    private VBox androidPane;

    @FXML
    private CheckBox appleCheckBox;

    @FXML
    private Pane centerPane;

    @FXML
    private ImageView imagView;

    @FXML
    private Pane imageViewWrapper;

    @FXML
    private VBox iosPane;

    @FXML
    private CheckBox ipadCheckBox;

    @FXML
    private CheckBox iphoneCheckBox;

    @FXML
    private CheckBox linuxCheckBox;

    @FXML
    private VBox linuxPane;

    @FXML
    private CheckBox macOSCheckBox;

    @FXML
    private CheckBox macosIcnsCheckBox;

    @FXML
    private MaskerPane maskerPane;

    @FXML
    private CheckBox watchOSCheckBox;

    @FXML
    private VBox winPane;

    @FXML
    private CheckBox windowsCheckBox;

    @FXML
    private StackPane rxBox;

    @FXML
    private RXToggleButton androidBtn;
    @FXML
    private RXToggleButton appleBtn;
    @FXML
    private RXToggleButton winBtn;

    @FXML
    private RXToggleButton linuxBtn;

    @FXML
    private ToggleGroup osGroup;

    @FXML
    private ToggleGroup linuxGroup;

    @FXML
    private CheckBox winIcoCheckBox;

    @FXML
    private CheckBox winPngsCheckBox;

    @FXML
    private ToggleGroup iconBgGroup;

    @FXML
    private RXToggleButton iconBgTransparentBtn;


    private String lastOpenDirPath;

    private String lastSaveDirPath;
    protected String resultPath;

    public static final String[] SUPPORTED_EXTENSIONS = {"*.webp", "*.svg", "*.png", "*.jpg", "*.bmp", "*.gif", "*.jpeg"};
    /**
     * 图片与wrapper之间 在四周稍微留出空间 252-248=4
     */
    public static final int FIT_SIZE = 248;
    /**
     * wrapper的宽高
     */
    public static final int WRAPPER_SIZE = 252;
    private String imagePath;

    private static final int BEST_SIZE = 1024;
    private BufferedImage awtImage;

    private int taskSize;

    private int completedSize;
    private long now;

    public AppIconController() {
        EventBusUtil.getDefault().register(this);
    }

    //TODO 加载完图片, 就显示 图片的宽高
    @FXML
    void initialize() {
        winosComboBox.getItems().addAll(WIN_10, WIN_VISTA, WIN_XP);
        winosComboBox.getSelectionModel().select(0);

        //winBtn.setOnAction(event -> osCarousel.setSelectedIndex(0));
        //appleBtn.setOnAction(event -> osCarousel.setSelectedIndex(1));
        //androidBtn.setOnAction(event -> osCarousel.setSelectedIndex(2));
        //linuxBtn.setOnAction(event -> osCarousel.setSelectedIndex(3));

        osGroup.selectedToggleProperty().addListener((ob, ov, nv) -> {
            int selectedIndex = osGroup.getToggles().indexOf(nv);
            ObservableList<Node> nodes = rxBox.getChildren();
            for (int i = 0; i < nodes.size(); i++) {
                Node node = rxBox.getChildren().get(i);
                node.setVisible(i == selectedIndex);
            }
        });
        appleCheckBox.setOnAction(event -> appleBtn.setSelected(true));
        androidCheckBox.setOnAction(event -> androidBtn.setSelected(true));
        linuxCheckBox.setOnAction(event -> linuxBtn.setSelected(true));
        windowsCheckBox.setOnAction(event -> winBtn.setSelected(true));
        winBtn.setSelected(true);

        imageViewWrapper.setOnDragDropped(this::onDragDroppedImage);
        imageViewWrapper.setOnDragOver(this::onDragOverImage);

        iconBgGroup.selectedToggleProperty().addListener((ob, ov, nv) ->{
            ObservableList<String> styleClass = ((RXToggleButton)nv).getStyleClass();
            String bgCss = "";
            for (String aClass : styleClass) {
                if (aClass.startsWith("svg-bg")) {
                    bgCss = aClass;
                    break;
                }
            }
            StyleUtil.removeClass(imageViewWrapper, "svg-bg-translucent", "svg-bg-transport", "svg-bg-white", "svg-bg-black", "svg-bg-gray");
            StyleUtil.addClass(imageViewWrapper, bgCss);
        });
        iconBgTransparentBtn.setSelected(true);
    }

    private void onDragDroppedImage(DragEvent event) {
        Dragboard db = event.getDragboard();
        boolean success = false;
        if (db.hasFiles()) {
            List<File> files = event.getDragboard().getFiles();
            files = files.stream().filter(this::isImageCompliantFormat).collect(Collectors.toList());
            if (!files.isEmpty()) {
                loadImage(files.get(0));
            }
            success = true;
        }
        event.setDropCompleted(success);
        event.consume();
    }

    private void onDragOverImage(DragEvent event) {
        if (event.getGestureSource() != imageViewWrapper
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
        if (!file.isFile()) {
            return false;
        }
        String name = file.getName().toLowerCase(Locale.ROOT);
        for (String supportedFormat : SUPPORTED_EXTENSIONS) {
            if (name.endsWith(supportedFormat.substring(1))) {
                return true;
            }
        }
        return false;
    }

    @FXML
    void addImageFilesAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        if (lastOpenDirPath != null) {
            fileChooser.setInitialDirectory(new File(lastOpenDirPath));
        }
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(message("imagePage.format.chooserDes"),
                SUPPORTED_EXTENSIONS));
        File file = fileChooser.showOpenDialog(imagView.getScene().getWindow());
        if (file == null) {
            return;
        }
        loadImage(file);
    }

    private void loadImage(File file) {
        imagePath = file.getPath();
        lastOpenDirPath = file.getParent();
        awtImage = ImageUtil.loadImage(imagePath, BEST_SIZE, BEST_SIZE);
        if (awtImage == null) {
            imagView.setImage(null);
            new InformationAlert(message("appicon.loadFailed")).showAndWait();
            return;
        }

        WritableImage fxImage = SwingFXUtils.toFXImage(awtImage, null);
        double width = fxImage.getWidth();
        double height = fxImage.getHeight();
        if (width > FIT_SIZE || height > FIT_SIZE) {
            imagView.setFitWidth(FIT_SIZE);
            imagView.setFitHeight(FIT_SIZE);
        } else {
            imagView.setFitWidth(width);
            imagView.setFitHeight(height);
        }
        imagView.setImage(fxImage);
        Bounds boundsInParent = imagView.getBoundsInParent();
        imagView.setLayoutX((WRAPPER_SIZE - boundsInParent.getWidth()) / 2.0);
        imagView.setLayoutY((WRAPPER_SIZE - boundsInParent.getHeight()) / 2.0);

    }

    /**
     * 选择导出路径,创建文件夹
     */
    private File chooseExportDir() {
        DirectoryChooser dirChooser = new DirectoryChooser();

        if (lastSaveDirPath != null) {
            File file = new File(lastSaveDirPath);
            if (file.exists() && file.isDirectory()) {
                dirChooser.setInitialDirectory(new File(lastSaveDirPath));
            }
        }
        File file = dirChooser.showDialog(FXToolsApp.mainStage);
        if (file == null) {
            //没有选择任何文件夹
            return null;
        }
        //创建文件夹
        //真实的输出路径
        File realExportDir;
        int x = 0;
        do {
            realExportDir = new File(file.getAbsolutePath() + File.separator + "AppIcons" + (x == 0 ? "" : "(" + x + ")"));
            x++;
        } while (realExportDir.exists());
        boolean mkdir = realExportDir.mkdir();
        if (!mkdir) {
            new InformationAlert(message("markDirectoryFailed")).showAndWait();
            return null;
        }
        return realExportDir;
    }

    @FXML
    void clearImageListAction(MouseEvent event) {
        imagePath = null;
        imagView.setImage(null);
    }

    @FXML
    void onClickBackBtn(ActionEvent event) {
        EventBusUtil.getDefault().post(new PageJumpEvent(0));
    }

    @FXML
    void onClickImageOutPane(MouseEvent event) {
        if (event.isControlDown() && imagePath!=null) {
            OSUtil.openAndSelectedFile(imagePath);
        }else {
            addImagesBtn.fire();
        }
        event.consume();
    }

    @FXML
    void onGenerateAppIconAction(ActionEvent event) {

        if (awtImage == null) {
            new InformationAlert(message("alert.addImage")).showAndWait();
            return;
        }
        File realExportDir = chooseExportDir();
        if (realExportDir == null) {
            return;
        }
        Path path = realExportDir.toPath();
        resultPath = realExportDir.getPath();
        lastSaveDirPath = realExportDir.getParent();
        int threadNum = ToolSettingsUtil.getInstance().getThreadNum();
        taskSize = 0;
        completedSize = 0;
        maskerPane.setVisible(true);
        maskerPane.setProgress(completedSize);
        ExecutorService exec = Executors.newFixedThreadPool(threadNum);
        if (appleCheckBox.isSelected()) {
            taskSize++;
            exec.submit(new TaskCreateAppleIcon(path, awtImage, macosIcnsCheckBox.isSelected()
                    , macOSCheckBox.isSelected(), iphoneCheckBox.isSelected()
                    , ipadCheckBox.isSelected(), watchOSCheckBox.isSelected()));
        }
        if (androidCheckBox.isSelected()) {
            taskSize++;
            String fileName = androidIconNameField.getText().trim();
            exec.submit(new TaskCreateAndroidIcon(path, awtImage, fileName));
        }
        if (linuxCheckBox.isSelected()) {
            taskSize++;
            exec.submit(new TaskCreateLinuxIcon(path, awtImage, linuxAllRbtn.isSelected()));
        }
        if (windowsCheckBox.isSelected()) {
            taskSize++;
            exec.submit(new TaskCreateWinIcon(path, awtImage, winosComboBox.getSelectionModel().getSelectedItem()
                    , winPngsCheckBox.isSelected(), winIcoCheckBox.isSelected()));
        }
        exec.shutdown();

    }

    @Subscribe
    public void appIconCreatedHandler(AppIconCreatedEvent event) {
        completedSize++;
        Platform.runLater(()->{
            maskerPane.setProgress(completedSize * 1.0 / taskSize);
            if (completedSize >= taskSize) {
                maskerPane.setVisible(false);
                if (resultPath != null
                        && ToolSettingsUtil.getInstance().getTaskCompleteOpenFile()) {
                    OSUtil.openAndSelectedFile(resultPath);
                }
            }
        });
    }

}
