package org.fordes.subtitles.view.event;

import cn.hutool.core.io.FileUtil;
import javafx.event.Event;
import javafx.event.EventType;
import lombok.Getter;
import org.fordes.subtitles.view.enums.FileEnum;

import java.io.File;

/**
 * @author fordes on 2022/4/8
 */
public class FileOpenEvent extends Event {

    public static final EventType<FileOpenEvent> FILE_OPEN_EVENT = new EventType(ANY, "fileOpenEvent");

    @Getter
    private final File openFile;

    @Getter
    private final FileEnum type;

    public FileOpenEvent(File openFile) {
        super(FILE_OPEN_EVENT);
        this.openFile = openFile;
        this.type = FileEnum.of(FileUtil.getSuffix(openFile));
    }

    public FileOpenEvent(String filePath) {
        super(FILE_OPEN_EVENT);
        this.openFile = FileUtil.file(filePath);
        this.type = FileEnum.of(FileUtil.getSuffix(filePath));
    }
}
