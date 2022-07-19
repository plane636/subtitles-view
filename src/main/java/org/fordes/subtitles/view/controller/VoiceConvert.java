package org.fordes.subtitles.view.controller;

import lombok.extern.slf4j.Slf4j;
import org.fordes.subtitles.view.event.FileOpenEvent;
import org.fordes.subtitles.view.model.ApplicationInfo;
import org.fordes.subtitles.view.model.DTO.Video;
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

    private Video video;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ApplicationInfo.stage.addEventHandler(FileOpenEvent.FILE_OPEN_EVENT, fileOpenEvent -> {
            if (fileOpenEvent.getRecord().getFormat().media) {
                video = (Video) fileOpenEvent.getRecord();
                root.setVisible(true);
            }
        });
        super.initialize(url, resourceBundle);
    }
}
