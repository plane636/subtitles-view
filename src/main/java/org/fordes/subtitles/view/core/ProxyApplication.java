package org.fordes.subtitles.view.core;

import com.jthemedetecor.OsThemeDetector;
import javafx.stage.Stage;
import org.fordes.subtitles.view.annotation.JFXApplication;
import org.fordes.subtitles.view.annotation.Tray;
import org.springframework.context.event.EventListener;

import java.awt.*;

/**
 * 代理主类
 *
 * @author fordes on 2022/8/5
 */
@JFXApplication
public abstract class ProxyApplication {

    @EventListener
    public void handleEvent(StageReadyEvent event) {
        ((Stage) event.getSource()).show();
    }

    /**
     * 加载fxml之前执行，可扩展此方法，执行一些初始化操作
     *
     * @param stage    {@link Stage}
     * @param property {@link JFXApplication}
     */
    public void loadFXMLBefore(Stage stage, JFXApplication property) {
    }

    /**
     * 初始化操作完成后执行，可扩展此类，进行额外操作
     *
     * @param stage {@link Stage}
     */
    public void initAfter(Stage stage) {
    }

    /**
     * 注册主题检测器  使用默认策略，可覆盖此类进行自定义
     * 注意：只有在{@link JFXApplication#osThemeDetector}中开启，才会执行此方法
     *
     * @param detector {@link OsThemeDetector}
     * @param stage    {@link Stage}
     * @param property {@link JFXApplication}
     */
    public void registerOsThemeDetector(OsThemeDetector detector, Stage stage, JFXApplication property) {
        if (property.darkStyleClass() != null && !property.darkStyleClass().equals("")) {
            //默认监听器，深色模式时为root添加 darkStyleClass
            detector.registerListener(isDark -> {
                if (isDark) {
                    if (!stage.getScene().getRoot().getStyleClass().contains(property.darkStyleClass())) {
                        stage.getScene().getRoot().getStyleClass().add(property.darkStyleClass());
                    }
                } else {
                    stage.getScene().getRoot().getStyleClass().remove(property.darkStyleClass());
                }
            });
        }
    }

    /**
     * 设置托盘图标，可扩展此类，对托盘图标进行自定义
     * 注意：只有{@link JFXApplication#systemTray}进行配置，
     * 同时系统支持托盘{@link SystemTray#isSupported()}以及设置了托盘图标{@link Tray#image()}才会执行此方法
     *
     * @param trayIcon {@link TrayIcon}
     * @param stage    {@link Stage}
     * @param property {@link Tray}
     */
    public void initSystemTray(TrayIcon trayIcon, Stage stage, Tray property) {
    }
}
