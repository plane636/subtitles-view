package org.fordes.subtitles.view.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import lombok.extern.slf4j.Slf4j;
import org.fordes.subtitles.view.enums.FontIcon;
import org.fordes.subtitles.view.event.ToastConfirmEvent;
import org.fordes.subtitles.view.model.ApplicationInfo;

/**
 * @author fordes on 2022/1/19
 */
@Slf4j
public class TitleBar {

    @FXML
    private Button closed, maximize, minimize;

    @FXML
    private VBox root;

    @FXML
    private Label title;

    @FXML
    private void closed(ActionEvent actionEvent) {
        ApplicationInfo.stage.fireEvent(new ToastConfirmEvent("标题", "内容", "确认按钮", () -> {

        }));
    }

    @FXML
    private void maximize(ActionEvent actionEvent) {
        ApplicationInfo.stage.setFullScreen(!ApplicationInfo.stage.isFullScreen());
        maximize.setText(ApplicationInfo.stage.isFullScreen() ?
                FontIcon.EXIT_FULL_SCREEN.toString() : FontIcon.FULL_SCREEN.toString());
        actionEvent.consume();

    }

    @FXML
    private void minimize(ActionEvent actionEvent) {
        ApplicationInfo.stage.setIconified(true);
        actionEvent.consume();
    }
}
