package org.fordes.subtitles.view.core;

import lombok.extern.slf4j.Slf4j;
import org.fordes.subtitles.view.annotation.InitStage;
import org.fordes.subtitles.view.annotation.Tray;
import org.fordes.subtitles.view.constant.StyleClassConstant;
import org.fordes.subtitles.view.model.ApplicationInfo;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * 监听启动事件，
 * @author fordes on 2022/2/1
 */
@Slf4j
@Component
public class StageReadyListener {



    @EventListener
    @InitStage(value = "/fxml/main-view.fxml", title = "SubtitlesView Alpha",
            css = {"/css/styles.css", "/css/font.css"}, darkStyleClass = "dark", icons = {"/icon/logo.ico"},
            systemTray = @Tray(value = true, image = "/icon/logo.png", toolTip = "SubtitlesView"))
    public void handleEvent(StageReadyEvent event) throws Exception {
        event.stage.show();
        //监听全屏状态，切换样式
        event.stage.fullScreenProperty().addListener((observableValue, aBoolean, t1) -> {
            ApplicationInfo.root.getStyleClass().remove(t1 ?
                    StyleClassConstant.NORMAL_SCREEN : StyleClassConstant.FULL_SCREEN);
            ApplicationInfo.root.getStyleClass().add(t1 ?
                    StyleClassConstant.FULL_SCREEN : StyleClassConstant.NORMAL_SCREEN);
        });

    }

}
