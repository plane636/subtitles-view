package org.fordes.subtitles.view.controller;

import javafx.scene.input.MouseEvent;
import org.fordes.subtitles.view.event.FileOpenEvent;
import org.fordes.subtitles.view.model.ApplicationInfo;
import org.springframework.stereotype.Component;

/**
 * 语音转换 控制器
 *
 * @author fordes on 2022/4/8
 */
@Component
public class MainEditor {
    public void test(MouseEvent event) {
        ApplicationInfo.stage.fireEvent(new FileOpenEvent(""));
    }
}
