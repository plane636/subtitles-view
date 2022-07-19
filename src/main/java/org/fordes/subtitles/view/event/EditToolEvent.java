package org.fordes.subtitles.view.event;

import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.control.TextArea;
import lombok.Getter;
import org.fordes.subtitles.view.enums.EditToolEventEnum;
import org.fordes.subtitles.view.model.DTO.Subtitle;


/**
 * 编辑工具 事件
 *
 * @author fordes123 on 2022/7/15
 */
public class EditToolEvent extends Event {

    public static final EventType<EditToolEvent> EVENT_TYPE = new EventType(ANY, "editToolEvent");

    @Getter
    private final TextArea source;

    @Getter
    private final Subtitle subtitle;

    @Getter
    private final EditToolEventEnum type;

    public EditToolEvent(TextArea source, Subtitle subtitle, EditToolEventEnum type) {
        super(EVENT_TYPE);
        this.source = source;
        this.subtitle = subtitle;
        this.type = type;
    }

}
