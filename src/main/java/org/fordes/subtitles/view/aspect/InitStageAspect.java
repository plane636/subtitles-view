package org.fordes.subtitles.view.aspect;

import com.jthemedetecor.OsThemeDetector;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.fordes.subtitles.view.annotation.JFXApplication;
import org.fordes.subtitles.view.annotation.Tray;
import org.fordes.subtitles.view.core.ProxyApplication;
import org.fordes.subtitles.view.core.StageReadyEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;

/**
 * 切面执行初始化
 *
 * @author fordes on 2022/8/5
 */
@Slf4j
@Aspect
@Component
@AllArgsConstructor
public class InitStageAspect {

    private final ConfigurableApplicationContext context;

    private static final FXMLLoader loader = new FXMLLoader();

    private static final String HANDLE_EVENT = "handleEvent";

    @Before("@within(org.fordes.subtitles.view.annotation.JFXApplication)")
    public void handle(JoinPoint jp) throws Throwable {

        MethodSignature signature = (MethodSignature) jp.getSignature();

        if (HANDLE_EVENT.equals(signature.getMethod().getName())) {
            Stage stage = (Stage) ((StageReadyEvent) jp.getArgs()[0]).getSource();
            JFXApplication property  = jp.getTarget().getClass().getAnnotation(JFXApplication.class);

            //执行前置方法
            ProxyApplication application = (ProxyApplication) jp.getTarget();
            application.loadFXMLBefore(stage, property);

            //加载fxml
            Parent root;
            if (property.value() != null && !property.value().equals("")) {
                //加载根布局
                loader.setControllerFactory(context::getBean);
                loader.setLocation(context.getResource(property.value()).getURL());
                root = loader.load();
            } else root = new StackPane();

            //加载字体
            if (property.fonts() != null && property.fonts().length != 0) {
                for (String path : property.fonts()) {
                    Font.loadFont(context.getResource(path).getInputStream(), 0);
                }
            }

            //设置程序图标
            if (property.icons() != null && property.icons().length != 0) {
                for (String icon : property.icons()) {
                    stage.getIcons().add(new Image(context.getResource(icon).getInputStream()));
                }
            }

            //设置样式
            Scene scene = new Scene(root);
            stage.initStyle(property.style());
            stage.setTitle(property.title());
            stage.setFullScreenExitHint(property.fullScreenExitHint());
            stage.setScene(scene);
            if (property.css() != null && property.css().length != 0) {
            root.getStylesheets().addAll(property.css());
            }

            //注册OS主题检测器
            if (property.osThemeDetector()) {
                OsThemeDetector detector = OsThemeDetector.getDetector();
                application.registerOsThemeDetector(detector, stage, property);
            }

            //注册系统托盘
            Tray tray = property.systemTray();
            if (tray != null && tray.value()) {
                System.setProperty("java.awt.headless", String.valueOf(tray.headless()));
                if (SystemTray.isSupported() && tray.image() != null && !tray.image().equals("")) {
                    TrayIcon trayIcon = new TrayIcon(ImageIO.read(context.getResource(tray.image()).getInputStream()));
                    trayIcon.setToolTip(tray.toolTip());
                    trayIcon.setImageAutoSize(tray.imageAutoSize());
                    trayIcon.setActionCommand(tray.actionCommand());
                    application.initSystemTray(trayIcon, stage, tray);
                    SystemTray.getSystemTray().add(trayIcon);
                } else {
                    log.error("SystemTray is not support!");
                }
            }

            //执行后置方法
            application.initAfter(stage);
        }
    }
}
