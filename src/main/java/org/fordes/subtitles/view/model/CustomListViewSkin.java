package org.fordes.subtitles.view.model;

import cn.hutool.core.util.ReflectUtil;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.skins.JFXListViewSkin;
import com.sun.javafx.scene.control.VirtualScrollBar;
import javafx.scene.control.skin.VirtualFlow;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;

/**
 * @author fordes on 2022/4/1
 */
@Slf4j
public class CustomListViewSkin<T> extends JFXListViewSkin<T> {

    public CustomListViewSkin(JFXListView<T> listView) {
        super(listView);
    }


    public final VirtualScrollBar getVirtualScrollBar(String name) throws IllegalAccessException {
        Field field = ReflectUtil.getField(VirtualFlow.class, name);
        field.setAccessible(true);
        return (VirtualScrollBar) field.get(getVirtualFlow());
    }
}
