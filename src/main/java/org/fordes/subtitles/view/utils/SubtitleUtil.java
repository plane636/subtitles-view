package org.fordes.subtitles.view.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.util.StrUtil;
import javafx.scene.control.IndexRange;
import lombok.extern.slf4j.Slf4j;
import org.fordes.subtitles.view.enums.FileEnum;
import org.fordes.subtitles.view.model.DTO.Subtitle;
import org.fordes.subtitles.view.utils.submerge.parser.ASSParser;
import org.fordes.subtitles.view.utils.submerge.parser.ParserFactory;
import org.fordes.subtitles.view.utils.submerge.parser.SRTParser;
import org.fordes.subtitles.view.utils.submerge.parser.SubtitleParser;
import org.fordes.subtitles.view.utils.submerge.subtitle.common.TimedLine;
import org.fordes.subtitles.view.utils.submerge.subtitle.common.TimedObject;
import org.fordes.subtitles.view.utils.submerge.subtitle.common.TimedTextFile;

import java.time.LocalTime;
import java.util.List;
import java.util.Set;

/**
 * @author ChengFengsheng on 2022/7/19
 */
@Slf4j
public class SubtitleUtil {

    /**
     * 时间轴位移
     * @param timedTextFile 字幕
     * @param begin 开始时间
     * @param range 位移范围
     * @param mode  显示模式
     * @return  时间轴位移后的字幕
     */
    public static TimedTextFile reviseTimeLine(TimedTextFile timedTextFile, LocalTime begin, IndexRange range, boolean mode) {
        LocalTime start = CollUtil.getFirst(timedTextFile.getTimedLines()).getTime().getStart();
        long poor = begin.toNanoOfDay() - start.toNanoOfDay();
        if (range != null) {
            long sort = 0;
            for (TimedLine item : timedTextFile.getTimedLines()) {
                sort += subtitleDisplay(item, mode).length();
                if (sort > range.getEnd()) {
                    break;
                } else if (sort >= range.getStart()) {
                    item.getTime().setStart(LocalTime.ofNanoOfDay(item.getTime().getStart().toNanoOfDay() + poor));
                    item.getTime().setEnd(LocalTime.ofNanoOfDay(item.getTime().getEnd().toNanoOfDay() + poor));
                }
            }
        } else {
            for (TimedLine item : timedTextFile.getTimedLines()) {
                reviseTimeLine(item.getTime(), poor);
            }
        }
        return timedTextFile;
    }

    private static void reviseTimeLine(TimedObject timedLine, long poor) {
        timedLine.setStart(LocalTime.ofNanoOfDay(timedLine.getStart().toNanoOfDay() + poor));
        timedLine.setEnd(LocalTime.ofNanoOfDay(timedLine.getEnd().toNanoOfDay() + poor));
    }

    public static TimedTextFile reviseTimeLine(TimedTextFile timedTextFile, LocalTime begin, boolean mode) {
        return reviseTimeLine(timedTextFile, begin, null, mode);
    }

    public static Subtitle editorChange(final Subtitle subtitle, List<String> lines, int row) {
        Set<? extends TimedLine> timedLines = subtitle.getTimedTextFile().getTimedLines();
        int sort = 0;
        for (int i = 0; i < timedLines.size(); i++) {
            TimedLine line = CollUtil.get(timedLines, i);
            sort += line.getTextLines().size();
            if (row <= sort) {
                line.getTextLines().set(sort - row, lines.get(row - 1));
                break;
            }
        }
        return subtitle;
    }


    public static Subtitle readSubtitleFile(Subtitle subtitle) throws Exception {
        TimeInterval timer = DateUtil.timer();
        SubtitleParser parser = ParserFactory.getParser(subtitle.getFormat().suffix);
        TimedTextFile content = parser.parse(subtitle.getFile(), subtitle.getCharset());
        log.debug("解析字幕耗时：{} ms", timer.interval());
        return subtitle.setTimedTextFile(content);
    }

    public static TimedTextFile readSubtitleStr(String str, FileEnum type) throws Exception {
        SubtitleParser parser = null;
        switch (type) {
            case SRT:
                parser = new SRTParser();
                break;
            case ASS:
                parser = new ASSParser();
                break;
//            case LRC:
            default:
                throw new RuntimeException();
        }
        return parser.parse(str, StrUtil.EMPTY);
    }

    /**
     * 字幕结构转换为字符串
     * @param mode 解析模式 f-简洁模式 t-完整模式
     * @return 字符串
     */
    public static String subtitleDisplay(TimedTextFile subtitle, boolean mode) {
        if (!mode) {
            StringBuilder content = new StringBuilder();
            subtitle.getTimedLines().forEach(item
                    -> content.append(CollUtil.join(item.getTextLines(), StrUtil.CRLF)).append(StrUtil.CRLF));
            return content.toString();
        }else {
            return subtitle.toString();
        }
    }
    /**
     * 字幕结构转换为字符串
     *
     * @param mode 解析模式 f-简洁模式 t-完整模式
     * @return 字符串
     */
    public static String subtitleDisplay(TimedLine timedLine, boolean mode) {
        return mode ? timedLine.toString() : CollUtil.join(timedLine.getTextLines(), StrUtil.CRLF);
    }
}
