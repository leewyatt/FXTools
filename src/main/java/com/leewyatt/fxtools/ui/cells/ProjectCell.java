package com.leewyatt.fxtools.ui.cells;

import com.leewyatt.fxtools.model.Project;
import com.leewyatt.fxtools.utils.LinearGradientConstants;
import com.leewyatt.fxtools.utils.OSUtil;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.text.Text;

import java.util.List;
import java.util.Random;

/**
 * @author LeeWyatt
 */
public class ProjectCell extends ListCell<Project> {

    private final Text nameText;
    private final Label descLabel;
    private final BorderPane pane;
    private final StackPane regionWrapper;

    public ProjectCell() {
        Region region = new Region();
        region.setPrefSize(14, 14);
        region.setMaxSize(14, 14);
        regionWrapper = new StackPane(region);
        regionWrapper.getStyleClass().add("url-btn");
        regionWrapper.setPrefSize(18, 18);
        regionWrapper.setMaxSize(18, 18);
        nameText = new Text();
        nameText.getStyleClass().add("name-text");

        regionWrapper.setOnMousePressed(this::onClickGotoGithubBtn);
        Region region1 = new Region();
        HBox.setHgrow(region1, Priority.ALWAYS);
        HBox.setMargin(regionWrapper, new Insets(0,4,0,0));
        HBox box = new HBox(nameText, region1, regionWrapper);
        box.setAlignment(Pos.CENTER_LEFT);
        descLabel = new Label();
        descLabel.getStyleClass().add("small-font");
        descLabel.getStyleClass().add("desc-label");
        descLabel.setWrapText(true);

        pane = new BorderPane();
        pane.getStyleClass().add("project-pane");
        pane.setTop(box);
        pane.setCenter(descLabel);

        BorderPane.setAlignment(descLabel, Pos.TOP_LEFT);
        pane.setPrefSize(220, 70);
        pane.setMinSize(220, 70);
        setPrefWidth(220);
    }

    public void onClickGotoGithubBtn(MouseEvent event) {
        if (getItem() == null) {
            return;
        }
        OSUtil.showDoc(getItem().getUrl());
    }

    @Override
    protected void updateItem(Project item, boolean empty) {
        super.updateItem(item, empty);
        if (item == null || empty) {
            setText("");
            setGraphic(null);
            return;
        }

        if (isSelected()) {
            nameText.setFill(Color.WHITE);
        }else {
            List<LinearGradient> list = LinearGradientConstants.LIST;
            nameText.setFill(list.get(new Random().nextInt(26)));
        }
        nameText.setText(item.getName());
        descLabel.setText((OSUtil.isEnglish() ? item.getDescEn() : item.getDescZh()));
        String type = "github".equalsIgnoreCase(item.getType())?"github":"webpage";
        regionWrapper.getStyleClass().setAll("url-btn", type + "-btn");
        setGraphic(pane);
    }
}
