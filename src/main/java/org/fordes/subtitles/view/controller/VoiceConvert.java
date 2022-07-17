package org.fordes.subtitles.view.controller;

import lombok.extern.slf4j.Slf4j;
import org.fordes.subtitles.view.event.FileOpenEvent;
import org.fordes.subtitles.view.model.ApplicationInfo;
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
public class VoiceConvert extends DelayInitController {

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ApplicationInfo.stage.addEventHandler(FileOpenEvent.FILE_OPEN_EVENT, fileOpenEvent -> {
            root.setVisible(fileOpenEvent.getType().media);
        });
        super.initialize(url, resourceBundle);
    }
}
