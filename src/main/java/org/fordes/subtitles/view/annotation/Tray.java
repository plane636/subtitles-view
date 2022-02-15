package org.fordes.subtitles.view.annotation;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Tray {

    /**
     * 是否启用
     */
    boolean value() default false;

    /**
     * 设置java.awt.headless
     */
    boolean headless() default false;

    /**
     * 托盘图标
     */
    String image() default "";

    /**
     * 图标大小自动调整
     */
    boolean imageAutoSize() default true;

    /**
     *  图标悬停提示
     */
    String toolTip() default "";

    /**
     * ActionCommand
     */
    String actionCommand() default "";
}
