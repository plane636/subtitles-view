package org.fordes.subtitles.view.aspect;

import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jthemedetecor.OsThemeDetector;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.fordes.subtitles.view.annotation.InitStage;
import org.fordes.subtitles.view.annotation.Tray;
import org.fordes.subtitles.view.core.StageReadyEvent;
import org.fordes.subtitles.view.mapper.SearchCasesMapper;
import org.fordes.subtitles.view.model.ApplicationInfo;
import org.fordes.subtitles.view.utils.FileUtils;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.io.IOException;

/**
 * 切面初始化stage
 *
 * @author fordes on 2022/2/3
 */
@Slf4j
@Aspect
@Component
@AllArgsConstructor
public class InitStageAspect {

    private final SearchCasesMapper casesMapper;

    @Before("@annotation(org.fordes.subtitles.view.annotation.InitStage)")
    public void handle(JoinPoint point) throws IOException, AWTException {
        for (Object arg : point.getArgs()) {
            if (arg instanceof StageReadyEvent) {
                Stage stage = ((StageReadyEvent) arg).stage;
                ApplicationInfo.stage = stage;

                setConfig();

                //获取注解值
                MethodSignature signature = (MethodSignature) point.getSignature();
                InitStage property = signature.getMethod().getAnnotation(InitStage.class);

                //加载根布局
                ClassPathResource resource = new ClassPathResource(property.value());
                FXMLLoader loader = new FXMLLoader(resource.getUrl());
                Pane root = loader.load();
                ApplicationInfo.root = root;

                //加载字体
                if (ArrayUtil.isNotEmpty(property.fonts())) {
                    for (String path : property.fonts()) {
                        Font.loadFont(FileUtils.getStream(path), 0);
                    }
                }

                //设置css
                root.getStylesheets().addAll(property.css());

                //自动切换 深色/浅色 模式
                if (StrUtil.isNotBlank(property.darkStyleClass())) {
                    ApplicationInfo.detector = OsThemeDetector.getDetector();
                    ApplicationInfo.detector.registerListener(isDark -> {
                        if (isDark) {
                            root.getStyleClass().add(property.darkStyleClass());
                        } else {
                            root.getStyleClass().remove(property.darkStyleClass());
                        }
                    });
                    if (ApplicationInfo.detector.isDark()) {
                        root.getStyleClass().add(property.darkStyleClass());
                    }
                }

                //设置scene
                Scene scene = new Scene(root);
                scene.setFill(Color.valueOf(property.fill()));

                //设置stage
                stage.initStyle(property.style());
                stage.setTitle(property.title());
                stage.setFullScreenExitHint(property.fullScreenExitHint());
                stage.setScene(scene);

                if (ArrayUtil.isNotEmpty(property.icons())) {
                    for (String icon : property.icons()) {
                        stage.getIcons().add(new Image(FileUtils.getStream(icon)));
                    }
                }


                //设置系统托盘
                Tray tray = property.systemTray();
                if (tray.value()) {
                    System.setProperty("java.awt.headless", String.valueOf(tray.headless()));
                    if (SystemTray.isSupported() && StrUtil.isNotEmpty(tray.image())) {
                        TrayIcon trayIcon = new TrayIcon(ImageIO.read(FileUtils.getStream(tray.image())));
                        trayIcon.setToolTip(tray.toolTip());
                        trayIcon.setImageAutoSize(tray.imageAutoSize());
                        trayIcon.setActionCommand(tray.actionCommand());
                        trayIcon.addMouseListener(new MouseAdapter() {
                            @Override
                            public void mouseClicked(java.awt.event.MouseEvent e) {
                                if (e.getButton() == java.awt.event.MouseEvent.BUTTON1) {
                                    //左键点击
                                    stage.show();
//                                trayIcon.displayMessage("666","999", TrayIcon.MessageType.NONE);
//                        showStage(stage);
                                } else if (e.getButton() == java.awt.event.MouseEvent.BUTTON3) {
                                    //右键点击
                                    Platform.runLater(() -> {
//                            systemTrayView.showView(Modality.NONE);
//                            trayMenuController.show(e.getX(), e.getY());
                                    });

                                }


                            }
                        });
                        SystemTray.getSystemTray().add(trayIcon);

                    } else {
                        log.warn("systemTray is not support!");
                    }
                }
                break;
            }
        }
    }

    private void setConfig() {
        //读取配置
        ApplicationInfo.searchCases = casesMapper.selectList(new QueryWrapper<>());
    }

}
