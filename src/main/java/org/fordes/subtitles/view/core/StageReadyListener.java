package org.fordes.subtitles.view.core;

import lombok.extern.slf4j.Slf4j;
import org.fordes.subtitles.view.annotation.InitStage;
import org.fordes.subtitles.view.annotation.Tray;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * 监听启动事件，
 * @author fordes on 2022/2/1
 */
@Slf4j
@Component
public class StageReadyListener {

    public static String applicationName;

    @Value("${spring.application.name}")
    public void setApplicationName(String applicationName) {
        StageReadyListener.applicationName = applicationName;
    }

    @EventListener
    @InitStage(value = "/fxml/main-view.fxml", title = "SubtitlesView Alpha",
            css = {"/css/styles.css", "/css/font.css"}, darkStyleClass = "dark", icons = {"/icon/logo.ico"},
            systemTray = @Tray(value = true, image = "/icon/logo.png", toolTip = "SubtitlesView"))
    public void handleEvent(StageReadyEvent event) {
        event.stage.show();
        log.info("{} 启动成功！ 耗时: {} ms", applicationName, System.currentTimeMillis() - event.interval);
    }

}
