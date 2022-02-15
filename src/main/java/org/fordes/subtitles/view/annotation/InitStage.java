package org.fordes.subtitles.view.annotation;

import javafx.stage.StageStyle;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface InitStage {

    /**
     * fxml文件路径
     */
    String value() default "";

    /**
     * css 文件路径
     */
    String[] css() default {};

    /**
     * 编码
     */
    String encoding() default "UTF-8";

    /**
     * 标题
     */
    String title() default "";

    /**
     * Stage 样式
     */
    StageStyle style() default StageStyle.TRANSPARENT;

    /**
     * scene 填充色
     */
    String fill() default "TRANSPARENT";

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
    Tray systemTray();
}
