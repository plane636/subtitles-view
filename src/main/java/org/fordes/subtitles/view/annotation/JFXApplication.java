package org.fordes.subtitles.view.annotation;

import javafx.stage.StageStyle;

import java.lang.annotation.*;

@Inherited
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface JFXApplication {

    /**
     * fxml文件路径
     */
    String value() default "";

    /**
     * css 文件路径
     */
    String[] css() default {};

    /**
     * 标题
     */
    String title() default "";

    /**
     * Stage 样式
     */
    StageStyle style() default StageStyle.DECORATED;


    boolean osThemeDetector() default false;

    /**
     * 深色模式 StyleClass，如无将不会自动跟随深色模式
     */
    String darkStyleClass() default "";

    /**
     * 需要加载的字体路径，支持http
     * 更推荐使用css引入字体
     */
    String[] fonts() default {};

    /**
     * 应用图标
     */
    String[] icons() default {};

    /**
     * 全屏退出提示消息
     */
    String fullScreenExitHint() default "";

    /**
     * 系统托盘
     */
    Tray systemTray() default @Tray;
}
