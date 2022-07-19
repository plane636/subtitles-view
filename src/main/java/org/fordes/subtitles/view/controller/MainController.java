package org.fordes.subtitles.view.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import lombok.extern.slf4j.Slf4j;
import org.fordes.subtitles.view.constant.CommonConstant;
import org.fordes.subtitles.view.constant.StyleClassConstant;
import org.fordes.subtitles.view.enums.FontIcon;
import org.fordes.subtitles.view.event.FileOpenEvent;
import org.fordes.subtitles.view.model.ApplicationInfo;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author fordes on 2022/1/19
 */
@Slf4j
@Component
public class MainController implements Initializable {

    @FXML
    private ColumnConstraints sidebarColumn;

    @FXML
    private Label drawer;

    @FXML
    private SidebarBefore sidebarBeforeController;

    @FXML
    private SidebarAfter sidebarAfterController;

    @FXML
    private SidebarBottom sidebarBottomController;

    @FXML
    private GridPane content;

    @FXML
    private Parent quickStart, subtitleSearch, toolBox, setting, export, mainEditor, syncEditor, voiceConvert,
            sidebarBefore, sidebarAfter;

    private static double xOffset = 0;
    private static double yOffset = 0;
    private static int bit = 0;
    private final static double RESIZE_WIDTH = 5.00;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //绑定侧边按键和对应面板显示
        sidebarBeforeController.getQuickStart().setOnAction(event -> {
            sidebarBeforeController.getQuickStart().setSelected(true);
            quickStart.setVisible(true);
        });
        sidebarBeforeController.getSubtitleSearch().setOnAction(event -> {
            sidebarBeforeController.getSubtitleSearch().setSelected(true);
            subtitleSearch.setVisible(true);
        });
        sidebarBeforeController.getToolBox().setOnAction(event -> {
            sidebarBeforeController.getToolBox().setSelected(true);
            toolBox.setVisible(true);
        });
        sidebarAfterController.getMainEditor().setOnAction(event -> {
            sidebarAfterController.getMainEditor().setSelected(true);
            mainEditor.setVisible(true);
        });
        sidebarAfterController.getSyncEditor().setOnAction(event -> {
            sidebarAfterController.getSyncEditor().setSelected(true);
            syncEditor.setVisible(true);
        });
        sidebarAfterController.getExport().setOnAction(event -> {
            sidebarAfterController.getExport().setSelected(true);
            export.setVisible(true);
        });
        sidebarBottomController.getSetting().setOnAction(event -> {
            setting.setVisible(true);
            sidebarAfterController.getItemGroup().selectToggle(null);
            sidebarBeforeController.getItemGroup().selectToggle(null);
        });

        content.getChildren().forEach(node ->
                node.visibleProperty().addListener((observableValue, aBoolean, t1) -> {
                    if (t1) {
                        content.getChildren().forEach(e -> e.setVisible(e.equals(node)));
                    }
                }));


        ApplicationInfo.stage.addEventHandler(FileOpenEvent.FILE_OPEN_EVENT, fileOpenEvent -> {
            if (fileOpenEvent.getRecord().getFormat().media) {
                sidebarAfterController.getItemGroup().selectToggle(null);
                sidebarBeforeController.getItemGroup().selectToggle(null);
            }else {
                sidebarBefore.setVisible(false);
                sidebarAfter.setVisible(true);
            }
        });
    }

    @FXML
    private void mousePressedHandle(MouseEvent event) {
        event.consume();
        xOffset = event.getSceneX();
        yOffset = event.getSceneY();
    }

    @FXML
    private void mouseMoveHandle(MouseEvent event) {
        event.consume();
        double x = event.getSceneX();
        double y = event.getSceneY();
        double width = ApplicationInfo.stage.getWidth() - 20;
        double height = ApplicationInfo.stage.getHeight() - 20;
        Cursor cursorType = Cursor.DEFAULT;
        bit = 0;
        if (y >= height - RESIZE_WIDTH) {
            if (x <= RESIZE_WIDTH) {
                bit |= 1 << 3;
            } else if (x >= width - RESIZE_WIDTH) {
                bit |= 1;
                bit |= 1 << 2;
                cursorType = Cursor.SE_RESIZE;
            } else {
                bit |= 1;
                cursorType = Cursor.S_RESIZE;
            }
        } else if (x >= width - RESIZE_WIDTH) {
            bit |= 1 << 2;
            cursorType = Cursor.E_RESIZE;
        }
        ApplicationInfo.root.setCursor(cursorType);
    }

    @FXML
    private void mouseDraggedHandle(MouseEvent event) {
        event.consume();
        double x = event.getSceneX();
        double y = event.getSceneY();
        double nextX = ApplicationInfo.stage.getX();
        double nextY = ApplicationInfo.stage.getY();
        double nextWidth = ApplicationInfo.stage.getWidth();
        double nextHeight = ApplicationInfo.stage.getHeight();
        if ((bit & 1 << 2) != 0) {
            nextWidth = x;
        }
        if ((bit & 1) != 0) {
            nextHeight = y;
        }
        if (nextWidth <= CommonConstant.SCENE_MIN_WIDTH) {
            nextWidth = CommonConstant.SCENE_MIN_WIDTH;
        }
        if (nextHeight <= CommonConstant.SCENE_MIN_HEIGHT) {
            nextHeight = CommonConstant.SCENE_MIN_HEIGHT;
        }
        ApplicationInfo.stage.setX(nextX);
        ApplicationInfo.stage.setY(nextY);
        ApplicationInfo.stage.setWidth(nextWidth);
        ApplicationInfo.stage.setHeight(nextHeight);
    }

    @FXML
    private void titleBarDraggedHandle(MouseEvent event) {
        event.consume();
        ApplicationInfo.stage.setX(event.getScreenX() - xOffset);
        ApplicationInfo.stage.setY(event.getScreenY() - yOffset);
    }

    @FXML
    private void onDrawer(MouseEvent event) {
        if (sidebarColumn.getPrefWidth() > 0) {
            sidebarColumn.setPrefWidth(0);
            drawer.setText(FontIcon.PLACE_THE_LEFT.toString());
            content.getStyleClass().add(StyleClassConstant.CONTENT_EXCLUSIVE);
        } else {
            sidebarColumn.setPrefWidth(CommonConstant.SIDE_BAR_WIDTH);
            drawer.setText(FontIcon.PLACE_THE_RIGHT.toString());
            content.getStyleClass().remove(StyleClassConstant.CONTENT_EXCLUSIVE);
        }
        event.consume();
    }
}
