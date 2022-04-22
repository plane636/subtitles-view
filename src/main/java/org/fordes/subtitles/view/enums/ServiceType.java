package org.fordes.subtitles.view.enums;

import com.baomidou.mybatisplus.annotation.IEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 服务类型枚举
 *
 * @author fordes on 2022/4/17
 */
@Getter
@AllArgsConstructor
public enum ServiceType implements IEnum<Integer>  {

    //
    VOICE(1, "语音转写"),

    TRANSLATE(2, "文字翻译");

    private Integer value;
    private String desc;

    public static ServiceType of(Integer value) {
        switch (value) {
            case 1: return VOICE;
            case 2: return TRANSLATE;
        }
        return null;
    }
}