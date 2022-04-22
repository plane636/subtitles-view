package org.fordes.subtitles.view.enums;

import lombok.AllArgsConstructor;

/**
 * 图标枚举
 *
 * @author fordes on 2022/1/23
 */
@AllArgsConstructor
public enum FontIcon {

    SCENE_CLOSE("\ue648"),
    SCENE_MINIMIZE("\ue634"),
    EXIT_FULL_SCREEN("\ue61f"),
    FULL_SCREEN("\ue628"),
    ITEM_START("\ue669"),
    ITEM_SEARCH("\uec6f"),
    ITEM_TOOL("\ue64a"),
    LOGO("\ue69f"),
    SETTING("\ue711"),
    CHOOSE_FILE("\ue64e"),

    ENGINE_DDZM("\ue63b"),
    ENGINE_ASSRT("\ue609"),
    ENGINE_ZMK("\ue623"),
    ENGINE("\ue60f"),

    PLACE_THE_LEFT("\uec70"),
    PLACE_THE_RIGHT("\ue61a"),

    SETTING_PREFERENCES("\ue63c"),
    SETTING_INTERFACE("\ue62d");


    private final String unicode;

    @Override
    public String toString() {
        return unicode;
    }
}
