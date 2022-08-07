package org.fordes.subtitles.view.core;

import javafx.application.Application;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * 启动代理
 *
 * @author fordes on 2022/8/5
 */
public class ProxyLauncher extends Application {

    private ConfigurableApplicationContext context;

    private static Class<? extends ProxyApplication> source;

    private static final long interval = System.currentTimeMillis();


    public static void run(Class<? extends ProxyApplication> clazz, String... var1) {
        source = clazz;
        launch(ProxyLauncher.class, var1);
    }

    @Override
    public void init() throws Exception {
        this.context = new SpringApplicationBuilder()
                .sources(source)
                .run(getParameters().getRaw().toArray(new String[0]));
        super.init();
    }

    @Override
    public void start(Stage stage) throws Exception {
        context.publishEvent(new StageReadyEvent(stage));
    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }
}
