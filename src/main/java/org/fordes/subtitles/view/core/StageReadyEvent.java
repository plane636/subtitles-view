package org.fordes.subtitles.view.core;

import javafx.stage.Stage;
import org.springframework.context.ApplicationEvent;

/**
 * 启动事件
 * @author fordes on 2022/2/1
 */
public class StageReadyEvent extends ApplicationEvent {

    public StageReadyEvent(Stage stage) {
        super(stage);
    }
}
