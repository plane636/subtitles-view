package org.fordes.subtitles.view.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import lombok.extern.slf4j.Slf4j;
import org.fordes.subtitles.view.constant.CommonConstant;
import org.fordes.subtitles.view.model.ApplicationInfo;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author fordes on 2022/1/19
 */
@Slf4j
public class MainController implements Initializable {

    @FXML
    private SidebarBefore sidebarBeforeController;

    @FXML
    private Pane quickStart, subtitleSearch, toolBox;

    private static double xOffset = 0;
    private static double yOffset = 0;
    private static int bit = 0;
    private final static double RESIZE_WIDTH = 10.00;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //绑定侧边按键和对应面板显示
        sidebarBeforeController.getQuickStart().selectedProperty()
                .addListener((observableValue, aBoolean, t1) -> quickStart.setVisible(t1));
        sidebarBeforeController.getSubtitleSearch().selectedProperty()
                .addListener((observableValue, aBoolean, t1) -> subtitleSearch.setVisible(t1));
        sidebarBeforeController.getToolBox().selectedProperty()
                .addListener((observableValue, aBoolean, t1) -> toolBox.setVisible(t1));
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
}
