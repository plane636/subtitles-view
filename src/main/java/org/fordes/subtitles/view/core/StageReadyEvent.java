package org.fordes.subtitles.view.core;

import javafx.stage.Stage;
import org.springframework.context.ApplicationEvent;

/**
 * 启动事件
 * @author fordes on 2022/2/1
 */
public class StageReadyEvent extends ApplicationEvent {

    public final Stage stage;

    public final long interval;

    public StageReadyEvent(Stage stage, long interval) {
        super(stage);
        this.stage = stage;
        this.interval = interval;
    }
}
