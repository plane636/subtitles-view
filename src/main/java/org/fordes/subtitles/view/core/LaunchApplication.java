package org.fordes.subtitles.view.core;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.fordes.subtitles.view.SubtitlesViewApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * 启动器
 * @author fordes on 2022/2/1
 */
@Slf4j
public class LaunchApplication extends Application {

    private ConfigurableApplicationContext context;

    private static long interval;

    @Override
    public void init() {
        interval = System.currentTimeMillis();
        this.context = new SpringApplicationBuilder()
                .sources(SubtitlesViewApplication.class)
                .run(getParameters().getRaw().toArray(new String[0]));
    }

    @Override
    public void start(Stage primaryStage) {
        context.publishEvent(new StageReadyEvent(primaryStage, interval));
    }

    @Override
    public void stop() {
        context.close();
        Platform.exit();
    }
}
