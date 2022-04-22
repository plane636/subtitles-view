package org.fordes.subtitles.view.event;

import javafx.event.Event;
import javafx.event.EventType;
import lombok.Getter;

/**
 * 主题切换事件
 *
 * @author fordes on 2022/4/13
 */
public class ThemeChangeEvent extends Event {

    public static final EventType<ThemeChangeEvent> EVENT_TYPE = new EventType(ANY, "themeChangeEvent");

    @Getter
    private Boolean isDark;

    public ThemeChangeEvent(Boolean isDark) {
        super(EVENT_TYPE);
        this.isDark = isDark;
    }
}
