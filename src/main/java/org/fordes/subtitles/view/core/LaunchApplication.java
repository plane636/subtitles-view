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

    @Override
    public void init() throws Exception {
        this.context = new SpringApplicationBuilder()
                .sources(SubtitlesViewApplication.class)
                .run(getParameters().getRaw().toArray(new String[0]));
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        context.publishEvent(new StageReadyEvent(primaryStage));
    }

    @Override
    public void stop() throws Exception {
        context.close();
        Platform.exit();
    }
}
