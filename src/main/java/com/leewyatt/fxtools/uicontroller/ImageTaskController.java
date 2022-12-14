package com.leewyatt.fxtools.uicontroller;

import com.google.common.eventbus.Subscribe;
import com.leewyatt.fxtools.FXToolsApp;
import com.leewyatt.fxtools.event.*;
import com.leewyatt.fxtools.model.ImageInfo;
import com.leewyatt.fxtools.services.TaskAnalyzeImageInfo;
import com.leewyatt.fxtools.ui.ImageListViewTips;
import com.leewyatt.fxtools.ui.alert.ConfirmationAlert;
import com.leewyatt.fxtools.ui.alert.InformationAlert;
import com.leewyatt.fxtools.ui.paintpicker.IntegerTextField;
import com.leewyatt.fxtools.utils.AlphanumComparator;
import com.leewyatt.fxtools.utils.ImageUtil;
import com.leewyatt.fxtools.utils.OSUtil;
import com.leewyatt.fxtools.utils.ToolSettingsUtil;
import com.leewyatt.rxcontrols.pane.RXCarouselPane;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Region;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import org.controlsfx.control.MaskerPane;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static com.leewyatt.fxtools.utils.ResourcesUtil.message;

/**
 * @author LeeWyatt
 */
public class ImageTaskController {
    protected String lastImportDirPath;
    protected String lastExportDirPath;

    protected String resultPath;

    protected ImageEventType importEventType;
    protected ImageEventType exportEventType;

    protected int taskSize;
    protected int completedSize;

    @FXML
    protected ListView<ImageInfo> imageListView;

    @FXML
    protected MaskerPane maskerPane;

    @FXML
    private RXCarouselPane carouselPane;

    protected IntegerTextField colNumField;

    protected String[] supportedExtensions;

    protected boolean isExportFileOpened;

    protected void addImagesFromChooser() {
        List<File> files = chooseImportFiles();
        if (files == null || files.isEmpty()) {
            return;
        }
        lastImportDirPath = files.get(0).getParent();
        startLoadImageService(files);
    }

    /**
     * ????????????
     */
    protected List<File> chooseImportFiles() {
        FileChooser fileChooser = new FileChooser();
        if (lastImportDirPath != null) {
            fileChooser.setInitialDirectory(new File(lastImportDirPath));
        }
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(message("imagePage.format.chooserDes"), supportedExtensions));
        return fileChooser.showOpenMultipleDialog(FXToolsApp.mainStage);
    }

    /**
     * ??????????????????????????????????????????,????????????????????????
     */
    protected List<File> filterUniqueImageFiles(ObservableList<ImageInfo> items, List<File> files) {
        //?????????????????????, ?????????????????????????????????????????????????????????
        List<File> resultFileList = new ArrayList<>();
        //??????????????????????????????
        for (File f : files) {
            boolean containFlag = false;
            for (ImageInfo info : items) {
                if (f.getAbsolutePath().equals(info.getPath())) {
                    containFlag = true;
                    break;
                }
            }
            if (!containFlag) {
                resultFileList.add(f);
            }
        }
        return resultFileList;
    }

    /**
     * ?????????????????? ??????????????????
     */
    protected File chooseExportDir(int size) {
        //????????????, ??????, ????????????
        if (size == 0) {
            new InformationAlert(message("alert.addImage")).showAndWait();
            return null;
        }
        DirectoryChooser dirChooser = new DirectoryChooser();

        if (lastExportDirPath != null) {
            File file = new File(lastExportDirPath);
            if (file.exists() && file.isDirectory()) {
                dirChooser.setInitialDirectory(new File(lastExportDirPath));
            }
        }
        File file = dirChooser.showDialog(imageListView.getScene().getWindow());
        if (file == null) {
            //???????????????????????????
            return null;
        }
        //???????????????
        //?????????????????????
        File realExportDir;
        int x = 0;
        do {
            realExportDir = new File(file.getAbsolutePath() + File.separator + "ImageSets" + (x == 0 ? "" : "(" + x + ")"));
            x++;
        } while (realExportDir.exists());
        boolean mkdir = realExportDir.mkdir();
        if (!mkdir) {
            new InformationAlert(message("markDirectoryFailed")).showAndWait();
            return null;
        }
        return realExportDir;
    }

    /**
     * ?????????????????????
     */
    protected void onDragDroppedImage(DragEvent event) {
        Dragboard db = event.getDragboard();
        boolean success = false;
        List<File> result = null;
        if (db.hasFiles()) {
            List<File> files = event.getDragboard().getFiles();
            result = files.stream().filter(this::isImageCompliantFormat).collect(Collectors.toList());
            success = true;
        }
        event.setDropCompleted(success);
        event.consume();
        if (result != null) {
            startLoadImageService(result);
        }
    }

    /**
     * ??????????????????
     */
    protected void onDragOverImage(DragEvent event) {
        if (event.getGestureSource() != imageListView
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
        for (String supportedFormat : supportedExtensions) {
            if (name.endsWith(supportedFormat.substring(1))) {
                return true;
            }
        }
        return false;
    }

    private void startLoadImageService(List<File> files) {
        List<File> resultFiles = filterUniqueImageFiles(imageListView.getItems(), files);
        taskSize = resultFiles.size();
        if (taskSize == 0) {
            return;
        }
        completedSize = 0;
        maskerPane.visibleProperty().unbind();
        maskerPane.progressProperty().unbind();
        maskerPane.setProgress(0);
        maskerPane.setVisible(true);
        int threadNum = ToolSettingsUtil.getInstance().getThreadNum();
        ExecutorService exec = Executors.newFixedThreadPool(threadNum);
        for (int i = 0; i < taskSize; i++) {
            exec.submit(new TaskAnalyzeImageInfo(files.get(i), importEventType));
        }
        exec.shutdown();
    }

    @FXML
    void addImageFilesAction(ActionEvent event) {
        addImagesFromChooser();
    }

    /**
     * ??????????????????
     */
    @FXML
    void clearImageListAction(MouseEvent event) {
        // ??????????????????,??????????????????
        ObservableList<ImageInfo> items = imageListView.getItems();
        if (items.isEmpty()) {
            return;
        }
        ConfirmationAlert alert = new ConfirmationAlert(message("alert.deleteAll"));
        if (!alert.showAndGetResult()) {
            return;
        }

        List<String> cacheUrlList = items.stream().map(ImageInfo::getCacheUrl).collect(Collectors.toList());
        items.clear();
        imageListView.getSelectionModel().clearSelection();
        for (String url : cacheUrlList) {
            //??????????????????
            ImageUtil.IMAGE_CACHE.remove(url);
        }
    }

    /**
     * ???????????????????????????
     */
    @FXML
    void deleteImageAction(MouseEvent event) {
        ObservableList<ImageInfo> items = imageListView.getSelectionModel().getSelectedItems();
        List<String> cacheList = items.stream().map(ImageInfo::getCacheUrl).collect(Collectors.toList());
        //????????????????????????
        imageListView.getItems().removeAll(items);
        for (String cacheUrl : cacheList) {
            ImageUtil.IMAGE_CACHE.remove(cacheUrl);
        }
    }

    @FXML
    void onClickBackBtn(ActionEvent event) {
        EventBusUtil.getDefault().post(new PageJumpEvent(0));
    }

    @Subscribe
    public void settingsChangedHandler(ShowImgTipsEvent event) {
        if (ToolSettingsUtil.getInstance().getShowImgTips()) {
            imageListView.setPlaceholder(new ImageListViewTips());
        } else {
            imageListView.setPlaceholder(new Region());
        }
    }

    @Subscribe
    public void imageEventHandler(ImageEvent event) {
        ImageEventType taskType = event.getTaskType();
        if (taskType == importEventType || taskType == exportEventType) {
            completedSize++;
            Platform.runLater(() -> {
                maskerPane.setProgress(completedSize * 1.0 / taskSize);
                if (taskType == importEventType) {
                    imageListView.getItems().add(event.getImageInfo());
                }
                if (completedSize >= taskSize) {
                    maskerPane.setVisible(false);
                    if (taskType == exportEventType) {
                        imageListView.refresh();
                    } else {
                        List<ImageInfo> imageInfos = imageListView.getItems().stream().sorted((a, b) -> new AlphanumComparator().compare(a.getName(), b.getName())).collect(Collectors.toList());
                        imageListView.getItems().setAll(imageInfos);
                        if (colNumField != null) {
                            colNumField.setText(imageInfos.size() + "");
                            colNumField.requestFocus();
                            colNumField.selectAll();
                        }
                    }
                    if (taskType == exportEventType
                            && resultPath != null
                            && ToolSettingsUtil.getInstance().getTaskCompleteOpenFile()
                            && !isExportFileOpened
                    ) {
                        isExportFileOpened = true;
                        OSUtil.openAndSelectedFile(resultPath);
                    }
                }
            });
        }
    }
}
