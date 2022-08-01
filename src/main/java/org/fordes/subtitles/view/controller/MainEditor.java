package org.fordes.subtitles.view.controller;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.StrUtil;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import lombok.extern.slf4j.Slf4j;
import org.fordes.subtitles.view.constant.CommonConstant;
import org.fordes.subtitles.view.constant.StyleClassConstant;
import org.fordes.subtitles.view.enums.EditToolEventEnum;
import org.fordes.subtitles.view.enums.FontIcon;
import org.fordes.subtitles.view.event.*;
import org.fordes.subtitles.view.model.ApplicationInfo;
import org.fordes.subtitles.view.model.DTO.Subtitle;
import org.fordes.subtitles.view.utils.SubtitleUtil;
import org.fxmisc.richtext.StyleClassedTextArea;
import org.fxmisc.richtext.model.TwoDimensional;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 语音转换 控制器
 *
 * @author fordes on 2022/4/8
 */
@Slf4j
@Component
public class MainEditor extends DelayInitController {

    @FXML
    private Label editModeIcon;
    @FXML
    private Label indicator;

    @FXML
    private ToggleButton editMode;

    @FXML
    private StyleClassedTextArea editor;

    @FXML
    private HBox toolbarPanel;

    @FXML
    private RowConstraints toolbarRow;

    private Subtitle subtitle;

    @Resource
    private ThreadPoolExecutor globalExecutor;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        ApplicationInfo.stage.addEventHandler(TranslateEvent.EVENT_TYPE, event -> {

            if (TranslateEvent.SUCCESS.equals(event.getMsg())) {
                editor.clear();
                editor.append(SubtitleUtil.toStr(subtitle.getTimedTextFile(),
                        editMode.isSelected()), StrUtil.EMPTY);
                editor.moveTo(0);
            }
            Platform.runLater(() -> {
                ApplicationInfo.stage.fireEvent(new ToastConfirmEvent(event.getMsg(), event.getDetail()));
                ApplicationInfo.stage.fireEvent(new LoadingEvent(false));
            });

        });

        globalExecutor.execute(() -> {
            ApplicationInfo.stage.addEventHandler(FileOpenEvent.FILE_OPEN_EVENT, fileOpenEvent -> {
                if (fileOpenEvent.getRecord().getFormat().subtitle) {
                    subtitle = (Subtitle) fileOpenEvent.getRecord();
                    log.debug("主编辑器 => {}", subtitle.getFile().getPath());
                    try {
                        ApplicationInfo.stage.fireEvent(new LoadingEvent(true));
                        SubtitleUtil.parse(subtitle);
                        root.setVisible(true);
                    } catch (Exception e) {
                        log.error(ExceptionUtil.stacktraceToString(e));
                        ApplicationInfo.stage.fireEvent(new ToastConfirmEvent("读取失败！", "字幕文件已经损坏"));
                    } finally {
                        ApplicationInfo.stage.fireEvent(new LoadingEvent(false));
                    }
                }
            });



            //载入设置
            root.visibleProperty().addListener((observableValue, aBoolean, t1) -> {
                if (t1) {
                    editor.setStyle(StrUtil.format(StyleClassConstant.FONT_STYLE_TEMPLATE,
                            ApplicationInfo.config.getFontSize(), ApplicationInfo.config.getFontFace()));
                    editor.clear();
                    editor.append(SubtitleUtil.toStr(subtitle.getTimedTextFile(), editMode.isSelected()), StrUtil.EMPTY);
                    //编辑器模式
                    ctrlEditMode(ApplicationInfo.config.getEditMode());
                }
            });
            //编辑模式监听
            editMode.selectedProperty().addListener((observableValue, aBoolean, t1) -> {
                ctrlEditMode(t1);
                editor.clear();
                editor.append(SubtitleUtil.toStr(subtitle.getTimedTextFile(), t1), "");
            });
            //行列号监听
            editor.caretPositionProperty().addListener((observable, oldValue, newValue) -> {
                TwoDimensional.Position position = editor.offsetToPosition(newValue, TwoDimensional.Bias.Backward);
                indicator.setText(StrUtil.format((String) indicator.getUserData(), position.getMajor(), position.getMinor()));
            });
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
                        ApplicationInfo.stage.fireEvent(new EditToolEvent(editor, subtitle, editMode, type));
                    }
                });
            }
        });

        //快捷键
        KeyCodeCombination ctrlT = new KeyCodeCombination(KeyCode.T, KeyCodeCombination.CONTROL_DOWN);
        ApplicationInfo.stage.getScene().getAccelerators().put(ctrlT, this::ctrlToolbar);

        KeyCodeCombination ctrlF = new KeyCodeCombination(KeyCode.F, KeyCodeCombination.CONTROL_DOWN);
        ApplicationInfo.stage.getScene().getAccelerators().put(ctrlF, ()
                -> ApplicationInfo.stage.fireEvent(new EditToolEvent(editor, subtitle, editMode, EditToolEventEnum.SEARCH)));

        KeyCodeCombination ctrlR = new KeyCodeCombination(KeyCode.R, KeyCodeCombination.CONTROL_DOWN);
        ApplicationInfo.stage.getScene().getAccelerators().put(ctrlR, ()
                -> ApplicationInfo.stage.fireEvent(new EditToolEvent(editor, subtitle, editMode, EditToolEventEnum.REPLACE)));


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

    private void ctrlEditMode(Boolean mode) {
        if (mode == null) {
            mode = ApplicationInfo.config.getEditMode();
        } else {
            ApplicationInfo.config.setEditMode(mode);
        }
        editModeIcon.setText(mode ?
                FontIcon.SWITCH_ON_DARK.toString() :
                FontIcon.SWITCH_OFF_DARK.toString());
        editMode.setText(mode ? CommonConstant.FULL_MODE : CommonConstant.CONCISE_MODE);
        editMode.setSelected(mode);
    }

    @FXML
    private void changeEditMode(ActionEvent actionEvent) {
        actionEvent.consume();
    }

    @FXML
    private void onIndicatorClicked(MouseEvent mouseEvent) {
        ApplicationInfo.stage.fireEvent(new EditToolEvent(editor, subtitle, editMode, EditToolEventEnum.JUMP));
        mouseEvent.consume();
    }
}
