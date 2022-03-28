package org.fordes.subtitles.view.enums;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;

import java.util.Arrays;

/**
 * 文件类型枚举
 *
 * @author fordes on 2022/2/9
 */
@AllArgsConstructor
public enum FileEnum {

    //视频
    MP4("mp4", true, true, false),
    MKV("mkv", true, false, false),
    AVI("avi", true, false, false),
    RMVB("rmvb", true, false, false),
    TS("ts", true, false, false),

    //音频
    MP3("mp3", true, false, false),
    FLAC("flac", true, false, false),
    AAC("aac", true, false, false),

    //字幕
    LRC("lrc", true, false, false),
    SRT("srt", true, false, false),
    ASS("ass", true, false, false);

    public final String suffix;

    public final boolean support;

    public final boolean isMedia;

    public final boolean isSubtitle;

    public static boolean isMedia(String suffix) {
        return Arrays.stream(FileEnum.values())
                .filter(e -> e.isMedia)
                .anyMatch(e -> StrUtil.equalsIgnoreCase(e.suffix, suffix));
    }

    public static boolean isSupport(String suffix) {
        return Arrays.stream(FileEnum.values())
                .filter(e -> e.support)
                .anyMatch(e -> StrUtil.equalsIgnoreCase(e.suffix, suffix));
    }
}