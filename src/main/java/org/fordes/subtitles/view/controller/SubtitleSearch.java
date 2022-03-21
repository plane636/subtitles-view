package org.fordes.subtitles.view.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXNodesList;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import lombok.extern.slf4j.Slf4j;
import org.fordes.subtitles.view.constant.StyleClassConstant;
import org.fordes.subtitles.view.model.ApplicationInfo;
import org.fordes.subtitles.view.model.PO.SearchCases;
import org.fordes.subtitles.view.model.search.Result;
import org.fordes.subtitles.view.service.SearchService;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author fordes on 2022/2/6
 */
@Slf4j
public class SubtitleSearch implements Initializable {

    @FXML
    private JFXSpinner loading;

    @FXML
    private JFXListView<StackPane> listView;

    @FXML
    private JFXTextField searchField;

    @FXML
    private JFXNodesList nodesList;

    private ToggleGroup engineGroup;

    private static final SearchService SERVICE = new SearchService();

    //TODO 分页待适配

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //读取字幕搜索接口
        engineGroup = new ToggleGroup();
        ApplicationInfo.searchCases.forEach(e -> {
            ToggleButton engine = new ToggleButton();
            engine.getStyleClass().addAll(StyleClassConstant.SUBTITLE_SEARCH_ENGINE,
                    StyleClassConstant.SUBTITLE_SEARCH_ITEM);
            engine.setToggleGroup(engineGroup);
            engine.setUserData(e);
            engine.setTooltip(new Tooltip(e.getName()));
            engine.setText(e.getIcon());
            engine.setOnAction(this::engineActionHandle);
            nodesList.addAnimatedNode(engine);
        });
        //监听搜索服务运行状态，控制loading
        SERVICE.runningProperty().addListener((observableValue, aBoolean, t1) -> loading.setVisible(t1));
        //搜索完成，载入新结果
        SERVICE.setOnSucceeded(event -> {
            if (!SERVICE.getValue().isEmpty()) {
                listView.getItems().clear();
                SERVICE.getValue().forEach(result -> listView.getItems().add(buildItem(result)));
            }
        });
        //选择默认接口
        if (!engineGroup.getToggles().isEmpty()) {
            Toggle engine = CollUtil.getFirst(engineGroup.getToggles());
            engine.setSelected(true);
            searchField.setPromptText(StrUtil.format("从{}搜索", ((SearchCases) engine.getUserData()).getName()));
        }
    }


    /**
     * 搜索引擎（选中）切换事件
     * @param event source
     */
    private void engineActionHandle(ActionEvent event) {
        ToggleButton source = (ToggleButton) event.getSource();
        SearchCases cases = (SearchCases) source.getUserData();
        source.setSelected(true);
        nodesList.animateList(false);
        searchField.setPromptText(StrUtil.format("从{}搜索", cases.getName()));
    }

    /**
     * 输入框监听，提交新的搜索
     * @param event source
     */
    @FXML
    private void searchBeginHandle(ActionEvent event) {
        JFXTextField field = (JFXTextField) event.getSource();
        if (StrUtil.isNotBlank(field.getText())) {
            SearchCases cases = (SearchCases) engineGroup.getSelectedToggle().getUserData();
            SERVICE.search(cases.getCases(),field.getText());
        }
    }


    private StackPane buildItem(Result result) {
        StackPane root = new StackPane();
        root.getStyleClass().add("search-item");
        Label caption = new Label(result.getCaption());
        caption.getStyleClass().add("caption");
        Label text = new Label(result.getText());
        text.getStyleClass().add("text");
        root.getChildren().addAll(caption, text);

        StackPane.setAlignment(caption, Pos.TOP_LEFT);
        StackPane.setMargin(caption, new Insets(5, 0, 0, 0));
        StackPane.setAlignment(text, Pos.BOTTOM_LEFT);
        StackPane.setMargin(caption, new Insets(0, 0, 5, 0));
        root.setUserData(result);
        root.setOnMouseClicked(e -> {
            if (e.getButton().equals(MouseButton.PRIMARY) && e.getClickCount() == 2) {
                StackPane item = (StackPane) e.getSource();
                Result data = (Result)item.getUserData();
                if (ObjectUtil.isEmpty(data.getNext())) {
                    //TODO 没有继续搜索即为文件，发送初始化事件，启动编辑器
                }else {
                    SERVICE.search(data.getNext(), data.getParams());
                }
            }
        });
        return root;
    }
}
