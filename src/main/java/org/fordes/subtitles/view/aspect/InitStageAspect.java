package org.fordes.subtitles.view.aspect;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jthemedetecor.OsThemeDetector;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.fordes.subtitles.view.annotation.InitStage;
import org.fordes.subtitles.view.annotation.Tray;
import org.fordes.subtitles.view.constant.StyleClassConstant;
import org.fordes.subtitles.view.core.StageReadyEvent;
import org.fordes.subtitles.view.event.ThemeChangeEvent;
import org.fordes.subtitles.view.mapper.ConfigMapper;
import org.fordes.subtitles.view.mapper.InterfaceMapper;
import org.fordes.subtitles.view.model.ApplicationInfo;
import org.fordes.subtitles.view.utils.CacheUtil;
import org.fordes.subtitles.view.utils.FileUtils;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.io.IOException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Consumer;

/**
 * 切面初始化stage
 *
 * @author fordes on 2022/2/3
 */
@Slf4j
@Aspect
@Component
public class InitStageAspect {

    @Resource
    private ConfigMapper configMapper;

    @Resource
    private InterfaceMapper interfaceMapper;

    @Resource
    private ThreadPoolExecutor globalExecutor;

    @Resource
    private ConfigurableApplicationContext context;

    private final FXMLLoader loader = new FXMLLoader();

    @Before("@annotation(org.fordes.subtitles.view.annotation.InitStage)")
    public void handle(JoinPoint point) throws IOException {
        for (Object arg : point.getArgs()) {
            if (arg instanceof StageReadyEvent) {
                //读取设置
                ApplicationInfo.config = configMapper.selectOne(new QueryWrapper<>());
                Assert.notNull(ApplicationInfo.config);

                globalExecutor.execute(() -> CacheUtil.initLanguageDict(interfaceMapper.getLanguageList()));


                //获取注解值
                MethodSignature signature = (MethodSignature) point.getSignature();
                InitStage property = signature.getMethod().getAnnotation(InitStage.class);

                //加载根布局
                ClassPathResource resource = new ClassPathResource(property.value());
                loader.setControllerFactory(context::getBean);
                loader.setLocation(resource.getUrl());
                ApplicationInfo.stage = ((StageReadyEvent) arg).stage;
                ApplicationInfo.root = loader.load();

                //设置scene
                Scene scene = new Scene(ApplicationInfo.root);
                scene.setFill(Color.valueOf(property.fill()));

                //设置stage
                ApplicationInfo.stage.initStyle(property.style());
                ApplicationInfo.stage.setTitle(property.title());
                ApplicationInfo.stage.setFullScreenExitHint(property.fullScreenExitHint());
                //监听全屏状态，切换样式
                ApplicationInfo.stage.fullScreenProperty().addListener((observableValue, aBoolean, t1) -> {
                    ApplicationInfo.root.getStyleClass().remove(t1 ?
                            StyleClassConstant.NORMAL_SCREEN : StyleClassConstant.FULL_SCREEN);
                    ApplicationInfo.root.getStyleClass().add(t1 ?
                            StyleClassConstant.FULL_SCREEN : StyleClassConstant.NORMAL_SCREEN);
                });
                ApplicationInfo.stage.setScene(scene);

                //加载字体
                if (ArrayUtil.isNotEmpty(property.fonts())) {
                    for (String path : property.fonts()) {
                        Font.loadFont(FileUtils.getStream(path), 0);
                    }
                }

                //设置css
                ApplicationInfo.root.getStylesheets().addAll(property.css());

                //设置程序图标
                if (ArrayUtil.isNotEmpty(property.icons())) {
                    for (String icon : property.icons()) {
                        ApplicationInfo.stage.getIcons().add(new Image(FileUtils.getStream(icon)));
                    }
                }

                //注册OS主题检测器
                setThemeDetector(property);
                //设置系统托盘
                setSysTray(property);

                ApplicationInfo.stage.fireEvent(new ThemeChangeEvent(ApplicationInfo.config.getTheme()));
                return;
            }
        }
    }

    void setThemeDetector(InitStage property) {
        if (StrUtil.isNotEmpty(property.darkStyleClass())) {
            OsThemeDetector detector = OsThemeDetector.getDetector();
            Consumer<Boolean> themeListener = isDark -> {
                if (isDark) {
                    if (!ApplicationInfo.root.getStyleClass().contains(property.darkStyleClass())) {
                        ApplicationInfo.root.getStyleClass().add(property.darkStyleClass());
                    }
                } else {
                    ApplicationInfo.root.getStyleClass().remove(property.darkStyleClass());
                }
            };
            //监听主题切换事件
            ApplicationInfo.stage.addEventHandler(ThemeChangeEvent.EVENT_TYPE, event -> {
                Boolean isDark = event.getIsDark();
                if (ObjectUtil.isEmpty(isDark)) {
                    detector.registerListener(themeListener);
                    isDark = detector.isDark();
                } else {
                    try {
                        detector.removeListener(themeListener);
                    } catch (Exception ignored) {
                    }
                }
                themeListener.accept(isDark);
            });
        }
    }

    /**
     * 设置系统托盘
     */
    void setSysTray(InitStage property) {
        Tray tray = property.systemTray();
        try {

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
                            //TODO
                            if (e.getButton() == java.awt.event.MouseEvent.BUTTON1) {
                                //左键点击
                            } else if (e.getButton() == java.awt.event.MouseEvent.BUTTON3) {
                                //右键点击
                            }
                        }
                    });
                    SystemTray.getSystemTray().add(trayIcon);
                }
            }
        } catch (AWTException | IOException e) {
            log.error("系统托盘不受支持或初始化失败！");
            log.error(ExceptionUtil.stacktraceToString(e));
        }
    }
}
