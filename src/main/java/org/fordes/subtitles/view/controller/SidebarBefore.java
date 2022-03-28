package org.fordes.subtitles.view.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import lombok.Getter;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author fordes on 2022/1/27
 */
public class SidebarBefore implements Initializable {

    @FXML
    @Getter
    private ToggleButton quickStart, subtitleSearch, toolBox;

    @FXML
    @Getter
    private ToggleGroup itemGroup;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        itemGroup.selectedToggleProperty().addListener((observableValue, toggle, t1) -> {
            if (t1 == null) {
                toggle.setSelected(true);
            }
        });
    }
}
