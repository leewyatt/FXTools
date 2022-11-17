package com.leewyatt.fxtools.uicontroller;

import com.leewyatt.fxtools.model.Project;
import com.leewyatt.fxtools.services.LoadFXProjectService;
import com.leewyatt.fxtools.ui.cells.ProjectCell;
import com.leewyatt.fxtools.utils.OSUtil;
import com.leewyatt.rxcontrols.controls.RXTextField;
import com.leewyatt.rxcontrols.event.RXActionEvent;
import com.leewyatt.rxcontrols.event.RXCarouselEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import org.controlsfx.control.MaskerPane;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
/**
 * @author LeeWyatt
 */
public class TutorialPageController {

    @FXML
    private ResourceBundle resources;
    @FXML
    private RXTextField searchFontField;

    @FXML
    private MaskerPane maskerPane;

    @FXML
    private ListView<Project> projectListView;
    private ObservableList<Project> projectObservableList;
    private FilteredList<Project> projectFilteredList;
    private boolean isLoad;

    @FXML
    void onOpeningAction(RXCarouselEvent event) {
        if (isLoad) {
            return;
        }
        maskerPane.setVisible(true);
        isLoad = true;

        LoadFXProjectService service = new LoadFXProjectService();
        maskerPane.visibleProperty().bind(service.runningProperty());
        service.valueProperty().addListener((ob, ov, nv) ->{
            if (nv.size() > 0) {
                projectObservableList.setAll(nv);
            }
        });
        service.start();
    }

    @FXML
    void onClickClearTextBtn(RXActionEvent event) {
        searchFontField.setText("");
    }

    @FXML
    void initialize() {
        projectListView.setCellFactory(param -> new ProjectCell());
        projectListView.getStyleClass().add("project-lv");
        projectObservableList = FXCollections.observableArrayList();
        projectFilteredList = projectObservableList.filtered(param -> true);
        projectListView.setItems(projectFilteredList);
        searchFontField.textProperty().addListener((ob, ov, nv) -> {
            boolean empty = nv.trim().isEmpty();

            projectFilteredList.setPredicate(item -> {
                if (empty) {
                    return true;
                } else {
                    String searchWord = nv.toLowerCase();
                    List<String> keywords = item.getKeywords();
                    for (String keyword : keywords) {
                        String kw = keyword.toLowerCase(Locale.ROOT);
                        String name = item.getName().toLowerCase(Locale.ROOT);
                        String desc = OSUtil.isEnglish() ? item.getDescEn().toLowerCase(Locale.ROOT) : item.getDescZh().toLowerCase(Locale.ROOT);
                        if (kw.contains(searchWord)
                                || name.contains(searchWord)
                                || desc.contains(searchWord) ) {
                            return true;
                        }
                    }
                    return false;
                }
            });
        });
    }

}
