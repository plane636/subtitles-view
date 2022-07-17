package org.fordes.subtitles.view.controller;

import javafx.scene.control.Label;
import lombok.extern.slf4j.Slf4j;
import org.fordes.subtitles.view.event.EditToolEvent;
import org.fordes.subtitles.view.model.ApplicationInfo;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * 编辑工具 控制器
 *
 * @author fordes123 on 2022/7/15
 */
@Slf4j
@Component
public class EditTool extends DelayInitController {

    public Label testLab;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);
        //监听编辑工具事件 唤起对应功能面板
        ApplicationInfo.stage.addEventHandler(EditToolEvent.EVENT_TYPE, editToolEvent -> {
            if (editToolEvent.getType() == null) {
                root.setVisible(false);
                return;
            }
            root.setVisible(true);
            switch (editToolEvent.getType()) {
                case SEARCH: //搜索
                    testLab.setText("搜索");
                    break;
                case REPLACE://替换
                    testLab.setText("替换");
                    break;
                case JUMP://跳转
                    testLab.setText("跳转");
                    break;
                case FONT: //字体（样式）
                    testLab.setText("字体");
                    break;
                case TIMELINE: //时间轴
                    testLab.setText("时间轴");
                    break;
                case CODE://编码
                    testLab.setText("编码");
                    break;
                case REF: //刷新
                    testLab.setText("刷新");
                    break;
            }
        });

    }
}
