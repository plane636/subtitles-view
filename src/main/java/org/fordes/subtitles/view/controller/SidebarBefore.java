package org.fordes.subtitles.view.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import lombok.Getter;

/**
 * @author fordes on 2022/1/27
 */
public class SidebarBefore {

    @FXML
    @Getter
    private ToggleButton quickStart, SubtitleSearch, toolBox;

    @FXML
    @Getter
    private ToggleGroup itemGroup;

}
