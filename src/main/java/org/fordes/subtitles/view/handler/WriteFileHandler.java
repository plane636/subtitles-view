package org.fordes.subtitles.view.handler;

/**
 * 写入文件回调事件处理器接口
 *
 * @author fordes on 2022/7/20
 */
@FunctionalInterface
public interface WriteFileHandler {

    void handle(boolean success);
}
