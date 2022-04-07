package org.fordes.subtitles.view.enums;

import cn.hutool.core.util.StrUtil;

/**
 * 响应类型枚举
 *
 * @author fordes on 2022/3/29
 */
public enum ContentType {
    //json
    JSON(),

    //html
    HTML();

    public static ContentType of(String name) {
        return StrUtil.equalsIgnoreCase("json", name) ? JSON : HTML;
    }

}