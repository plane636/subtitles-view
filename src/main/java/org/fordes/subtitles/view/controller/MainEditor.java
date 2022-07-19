package org.fordes.subtitles.view.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Font;
import lombok.extern.slf4j.Slf4j;
import org.fordes.subtitles.view.enums.EditToolEventEnum;
import org.fordes.subtitles.view.event.EditToolEvent;
import org.fordes.subtitles.view.event.FileOpenEvent;
import org.fordes.subtitles.view.event.ToastConfirmEvent;
import org.fordes.subtitles.view.model.ApplicationInfo;
import org.fordes.subtitles.view.model.DTO.Subtitle;
import org.fordes.subtitles.view.utils.SubtitleUtil;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * 语音转换 控制器
 *
 * @author fordes on 2022/4/8
 */
@Slf4j
@Component
public class MainEditor extends DelayInitController {

    @FXML
    private TextArea editor;
    @FXML
    private HBox toolbarPanel;
    @FXML
    private RowConstraints toolbarRow;

    private Subtitle subtitle;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ApplicationInfo.stage.addEventHandler(FileOpenEvent.FILE_OPEN_EVENT, fileOpenEvent -> {
            if (fileOpenEvent.getRecord().getFormat().subtitle) {
                subtitle = (Subtitle) fileOpenEvent.getRecord();
                log.debug("主编辑器 => {}", subtitle.getFile().getPath());
                try {
                    //TODO 需要loading
                    SubtitleUtil.readSubtitleFile(subtitle);
                }catch (Exception e) {
                    ApplicationInfo.stage.fireEvent(new ToastConfirmEvent("读取失败！","字幕文件已经损坏"));
                }
                root.setVisible(true);
            }
        });

        //载入设置
        root.visibleProperty().addListener((observableValue, aBoolean, t1) -> {
            if (t1) {
                editor.setFont(new Font(ApplicationInfo.config.getFontFace(), ApplicationInfo.config.getFontSize()));
                editor.setText(SubtitleUtil.subtitleDisplay(subtitle.getTimedTextFile(), false));
            }
        });

        super.initialize(url, resourceBundle);
    }

    @Override
    public void delayInit() {

        //工具栏按钮，点击按钮发送编辑工具事件 唤起编辑工具
        toolbarPanel.getChildren().forEach(node -> {
            if (node.getUserData() != null) {
                node.setOnMouseClicked(event -> {
                    if (node.getUserData() != null) {
                        EditToolEventEnum type = EditToolEventEnum.valueOf((String) node.getUserData());
                        ApplicationInfo.stage.fireEvent(new EditToolEvent(editor, subtitle, type));
                    }
                });
            }
        });

        //快捷键
        KeyCodeCombination ctrlT = new KeyCodeCombination(KeyCode.T, KeyCodeCombination.CONTROL_DOWN);
        ApplicationInfo.stage.getScene().getAccelerators().put(ctrlT, this::ctrlToolbar);

        KeyCodeCombination ctrlF = new KeyCodeCombination(KeyCode.F, KeyCodeCombination.CONTROL_DOWN);
        ApplicationInfo.stage.getScene().getAccelerators().put(ctrlF, ()
                -> ApplicationInfo.stage.fireEvent(new EditToolEvent(editor, subtitle, EditToolEventEnum.SEARCH)));

        KeyCodeCombination ctrlR = new KeyCodeCombination(KeyCode.R, KeyCodeCombination.CONTROL_DOWN);
        ApplicationInfo.stage.getScene().getAccelerators().put(ctrlR, ()
                -> ApplicationInfo.stage.fireEvent(new EditToolEvent(editor, subtitle, EditToolEventEnum.REPLACE)));

        super.delayInit();
    }

    @FXML
    private void hideToolbar(ActionEvent actionEvent) {
        ctrlToolbar(false);
        actionEvent.consume();
    }

    /**
     * 控制工具栏显示/隐藏
     *
     * @param state 状态
     */
    private void ctrlToolbar(boolean state) {
        toolbarRow.setMaxHeight(state ? 60 : 0);
        toolbarRow.setMinHeight(state ? 60 : 0);
        toolbarRow.setPrefHeight(state ? 60 : 0);
        toolbarPanel.setVisible(state);
    }

    private void ctrlToolbar() {
        ctrlToolbar(!toolbarPanel.isVisible());
    }
}
