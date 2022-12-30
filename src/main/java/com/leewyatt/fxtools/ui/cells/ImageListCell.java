package com.leewyatt.fxtools.ui.cells;

import com.leewyatt.fxtools.model.ImageInfo;
import com.leewyatt.fxtools.ui.alert.ConfirmationAlert;
import com.leewyatt.fxtools.utils.ImageUtil;
import com.leewyatt.fxtools.utils.OSUtil;
import com.leewyatt.rxcontrols.utils.StyleUtil;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.util.StringConverter;

import java.util.Optional;

import static com.leewyatt.fxtools.utils.ResourcesUtil.message;

/**
 * @author LeeWyatt
 */
public class ImageListCell extends TextFieldListCell<ImageInfo> {

    private final AnchorPane pane;
    private final ImageView iv;
    private final Label nameLabel;
    private final StackPane typeIcon;
    private final Label lenLabel;
    private final Label sizeLabel;

    /**
     * 默认预览图片的尺寸宽50 高50
     */
    private static final int SIZE = 50;

    public ImageListCell() {
        pane = new AnchorPane();
        iv = new ImageView();
        iv.setPreserveRatio(true);
        iv.setFitWidth(SIZE);
        iv.setFitHeight(SIZE);

        StackPane imgPane = new StackPane(iv);
        imgPane.setCursor(Cursor.HAND);

        imgPane.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            if (getItem() == null) {
                return;
            }
            if (event.isControlDown()) {
                OSUtil.openAndSelectedFile(getItem().getPath());
            } else {
                OSUtil.showDoc(getItem().getPath());
            }
            event.consume();
        });
        imgPane.getStyleClass().add("image-preview-pane");
        imgPane.setPrefSize(50, 50);
        AnchorPane.setTopAnchor(imgPane, 2.5d);
        AnchorPane.setLeftAnchor(imgPane, 0d);
        pane.getChildren().add(imgPane);

        Region nameRegion = new Region();
        nameLabel = new Label("", nameRegion);
        nameRegion.setPrefSize(16, 12);
        nameRegion.setMaxSize(16, 12);
        nameLabel.setAlignment(Pos.CENTER_LEFT);
        nameLabel.setGraphicTextGap(2);
        nameLabel.getStyleClass().add("image-name-label");
        nameLabel.setTextOverrun(OverrunStyle.CENTER_ELLIPSIS);

        AnchorPane.setTopAnchor(nameLabel, 2d);
        AnchorPane.setLeftAnchor(nameLabel, 60d);
        AnchorPane.setRightAnchor(nameLabel, 40d);
        pane.getChildren().add(nameLabel);

        Region region = new Region();
        typeIcon = new StackPane(region);
        typeIcon.getStyleClass().add("type-icon");
        typeIcon.setPrefSize(32, 15);
        typeIcon.setMaxSize(32, 15);
        StackPane.setMargin(region, new Insets(3, 3, 3, 3));
        region.getStyleClass().add("type-shape");
        AnchorPane.setTopAnchor(typeIcon, 6d);
        AnchorPane.setRightAnchor(typeIcon, 5d);
        pane.getChildren().add(typeIcon);

        Region sizeRegion = new Region();
        sizeRegion.setPrefSize(10, 10);
        sizeLabel = new Label("", sizeRegion);
        sizeLabel.setGraphicTextGap(2);
        sizeLabel.setAlignment(Pos.CENTER_LEFT);
        sizeLabel.getStyleClass().add("image-size-label");
        Region lenRegion = new Region();
        lenRegion.setPrefSize(10, 10);
        lenLabel = new Label("", lenRegion);
        lenLabel.setGraphicTextGap(2);
        lenLabel.getStyleClass().add("image-len-label");
        lenLabel.setAlignment(Pos.CENTER_RIGHT);
        BorderPane bp = new BorderPane();
        bp.getStyleClass().add("image-detail-pane");

        bp.setLeft(sizeLabel);
        bp.setRight(lenLabel);
        AnchorPane.setTopAnchor(bp, 30d);
        AnchorPane.setLeftAnchor(bp, 60d);
        AnchorPane.setRightAnchor(bp, 5d);
        pane.getChildren().add(bp);
        pane.setPrefSize(220, 54);
        pane.setMinSize(220, 54);
        setPrefWidth(220);
        getStyleClass().add("image-cell");

        setConverter(new StringConverter<ImageInfo>() {
            @Override
            public String toString(ImageInfo object) {
                ImageInfo item = getItem();
                String name = "";
                if (item != null) {
                    name = item.getName();
                }
                return name;
            }

            @Override
            public ImageInfo fromString(String string) {
                ImageInfo item = getItem();
                Optional<ImageInfo> first = getListView().getItems().stream().filter(temp -> temp != item && temp.getName().equals(string)).findFirst();
                if (first.isPresent()) {
                    boolean b = new ConfirmationAlert("已存在同名图片.确定要修改吗").showAndGetResult();
                    if (b) {
                        if (item != null) {
                            item.setName(string);
                        }
                    }
                } else {
                    if (item != null) {
                        item.setName(string);
                    }
                }
                return item;
            }
        });
        MenuItem deleteBtn = new MenuItem(message("listCell.menu.delete"), getRegion("delete-region"));
        deleteBtn.setOnAction(event -> {
            getListView().getItems().remove(getItem());
        });
        MenuItem editBtn = new MenuItem(message("listCell.menu.edit"), getRegion("edit-region"));
        editBtn.setOnAction(event -> {
            startEdit();
        });
        setContextMenu(new ContextMenu(editBtn, deleteBtn));
    }

    private Region getRegion(String styleClass) {
        Region region = new Region();
        region.setPrefSize(12, 12);
        region.setMaxSize(12, 12);
        region.getStyleClass().add(styleClass);
        return region;
    }

    @Override
    public void updateItem(ImageInfo item, boolean empty) {
        super.updateItem(item, empty);
        if (item == null || empty) {
            setText(null);
            setGraphic(null);
        } else {
            iv.setImage(null);
            Image image;
            if (item.getWidth() < 0 || item.getHeight() < 0) {
                iv.setFitWidth(SIZE);
                iv.setFitHeight(SIZE);
                image = ImageUtil.ERROR_IMAGE;
                sizeLabel.setText(message("imageError"));
                sizeLabel.setVisible(true);
            } else {
                if (item.getWidth() <= SIZE && item.getHeight() <= SIZE) {
                    iv.setFitWidth(item.getWidth());
                    iv.setFitHeight(item.getHeight());
                } else {
                    iv.setFitWidth(SIZE);
                    iv.setFitHeight(SIZE);
                }
                image = ImageUtil.getCacheImage(item.getCacheUrl());
                sizeLabel.setText(item.getWidth() + "x" + item.getHeight());
                sizeLabel.setVisible(item.getWidth() != 0 && item.getHeight() != 0);
            }
            iv.setImage(image);

            StyleUtil.clearClass(typeIcon);
            StyleUtil.addClass(typeIcon, "type-icon", item.getFormat() + "-icon");
            nameLabel.setText(item.getName());
            lenLabel.setText(item.getFileLen());
            setGraphic(pane);
            setText(null);
        }
    }

}
