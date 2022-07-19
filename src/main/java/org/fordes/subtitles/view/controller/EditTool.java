package org.fordes.subtitles.view.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ReflectUtil;
import com.jfoenix.controls.JFXComboBox;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fordes.subtitles.view.enums.EditToolEventEnum;
import org.fordes.subtitles.view.enums.FileEnum;
import org.fordes.subtitles.view.event.EditToolEvent;
import org.fordes.subtitles.view.event.ToastConfirmEvent;
import org.fordes.subtitles.view.model.ApplicationInfo;
import org.fordes.subtitles.view.model.DTO.Subtitle;
import org.fordes.subtitles.view.utils.SubtitleUtil;
import org.fordes.subtitles.view.utils.submerge.subtitle.ass.ASSTime;
import org.fordes.subtitles.view.utils.submerge.subtitle.common.TimedTextFile;
import org.fordes.subtitles.view.utils.submerge.subtitle.srt.SRTTime;
import org.mozilla.universalchardet.Constants;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.net.URL;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 编辑工具 控制器
 *
 * @author fordes123 on 2022/7/15
 */
@Slf4j
@Component
public class EditTool extends DelayInitController {

    private static final Map<EditToolEventEnum, GridPane> bindMap = MapUtil.newHashMap();

    @FXML
    private ChoiceBox<String> code_choice, font_family;

    @FXML
    private JFXComboBox<Integer> font_size;

    @FXML
    private ChoiceBox<TimelineType> timeline_option;

    @FXML
    private TextField timeline_input;

    @Resource
    private ThreadPoolExecutor globalExecutor;

    private static Subtitle subtitle;

    private static TextArea area;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);
        //各工具面板互斥
        root.getChildren().forEach(node -> {
                    if (node instanceof GridPane) {
                        EditToolEventEnum type = EditToolEventEnum.valueOf((String) node.getUserData());
                        bindMap.put(type, (GridPane) node);
                        node.visibleProperty().addListener((observableValue, aBoolean, t1) -> {
                            if (t1) {
                                bindMap.values().forEach(e -> e.setVisible(node.equals(e)));
                            }
                        });
                    }
                }
        );
        //监听编辑工具事件 唤起对应功能面板
        ApplicationInfo.stage.addEventHandler(EditToolEvent.EVENT_TYPE, editToolEvent -> {

            if (bindMap.containsKey(editToolEvent.getType())) {
                root.setVisible(true);
                bindMap.get(editToolEvent.getType()).setVisible(true);
            }
            subtitle = editToolEvent.getSubtitle();
            area = editToolEvent.getSource();
            switch (editToolEvent.getType()) {
                case SEARCH: //搜索
//                    testLab.setText("搜索");
                    break;
                case REPLACE://替换
//                    testLab.setText("替换");
                    break;
                case JUMP://跳转
//                    testLab.setText("跳转");
                    break;
                case FONT: //字体（样式）

//                    testLab.setText("字体");
                    break;
                case TIMELINE: //时间轴
//                    testLab.setText("时间轴");
                    break;
                case CODE://编码
                    code_choice.getSelectionModel().select(subtitle.getCharset());
//                    testLab.setText("编码");
                    break;
                case REF: //刷新
//                    testLab.setText("刷新");
                    break;
            }
        });
        //异步执行耗时任务
        globalExecutor.execute(this::asyncInit);
    }

    public void asyncInit() {

        code_choice.getItems().addAll(Arrays.stream(ReflectUtil.getFieldsValue(Constants.class))
                .map(Object::toString).toArray(String[]::new));

        //初始化字体大小
        font_size.getItems().addAll(CollUtil.newArrayList(12, 14, 16, 18, 20, 22, 24, 26, 28, 36, 48, 72));
        font_size.getSelectionModel().select(ApplicationInfo.config.getFontSize());

        //初始化字体列表
        font_family.getItems().addAll(Font.getFontNames());
        font_family.getSelectionModel().select(ApplicationInfo.config.getFontFace());

        timeline_option.getItems().addAll(TimelineType.values());
        timeline_option.getSelectionModel().selectedItemProperty().addListener((observableValue, strings, t1)
                -> timeline_input.setPromptText(t1.desc));
        timeline_option.getSelectionModel().select(0);

        timeline_input.textProperty().addListener((observableValue, s, t1) -> {
            timeline_input.getStyleClass().remove("error");
        });

    }


    @FXML
    private void onClose(ActionEvent actionEvent) {
        actionEvent.consume();
        area = null;
        subtitle = null;
        root.setVisible(false);
    }

    @FXML
    private void applyCode(ActionEvent actionEvent) {
        if (subtitle != null && area != null) {
            String original = subtitle.getCharset();
            try {
                subtitle.setCharset(code_choice.getSelectionModel().getSelectedItem());
                SubtitleUtil.readSubtitleFile(subtitle);
                area.setText(SubtitleUtil.subtitleDisplay(subtitle.getTimedTextFile(), false));
            } catch (Exception e) {
                log.error(ExceptionUtil.stacktraceToString(e));
                ApplicationInfo.stage.fireEvent(new ToastConfirmEvent("编码更改出错", "已切换回原编码~"));
                subtitle.setCharset(original);
                code_choice.getSelectionModel().select(original);
            }
        }
        actionEvent.consume();
    }

    @FXML
    private void applyFont(ActionEvent actionEvent) {
        if (subtitle != null && area != null) {
            String originalFontFamily = ApplicationInfo.config.getFontFace();
            Integer originalFontSize = ApplicationInfo.config.getFontSize();
            try {
                ApplicationInfo.config.setFontSize(Convert.toInt(font_size.getValue()));
                ApplicationInfo.config.setFontFace(font_family.getValue());
                area.setFont(new Font(ApplicationInfo.config.getFontFace(), ApplicationInfo.config.getFontSize()));
                area.requestFocus();
            } catch (Exception e) {
                log.error(ExceptionUtil.stacktraceToString(e));
                ApplicationInfo.config.setFontSize(originalFontSize);
                ApplicationInfo.config.setFontFace(originalFontFamily);
                font_family.setValue(originalFontFamily);
                font_size.setValue(originalFontSize);
                ApplicationInfo.stage.fireEvent(new ToastConfirmEvent("字体更改出错", "已切换回原字体~"));
            }

        }
    }

    @FXML
    private void applyTimeline(ActionEvent actionEvent) {
        if (subtitle != null && area != null) {
            LocalTime newTime = null;
            String timeLine = timeline_input.getText();
            TimelineType option = timeline_option.getValue();
            if (TimelineType.TIMELINE.equals(option)) {
                try {
                    newTime = FileEnum.SRT.equals(subtitle.getFormat()) ?
                            SRTTime.fromString(timeLine) : ASSTime.fromString(timeLine);
                } catch (Exception ignored) {
                }

            } else {
                if (NumberUtil.isInteger(timeLine)) {
                    int offset = Convert.toInt(timeLine);
                    LocalTime date = CollUtil.getFirst(subtitle.getTimedTextFile().getTimedLines()).getTime().getStart();
                    newTime = date.plus(offset, option.rate);
                }
            }
            if (newTime != null) {
                //TODO 按选中范围处理 待支持
                TimedTextFile original = subtitle.getTimedTextFile();
                try {
                    TimedTextFile target = SubtitleUtil
                            .reviseTimeLine(subtitle.getTimedTextFile(), newTime, null, false);
                    subtitle.setTimedTextFile(target);
                    area.setText(SubtitleUtil.subtitleDisplay(subtitle.getTimedTextFile(), false));
                } catch (Exception e) {
                    log.error(ExceptionUtil.stacktraceToString(e));
                    subtitle.setTimedTextFile(original);
                    ApplicationInfo.stage.fireEvent(new ToastConfirmEvent("时间轴更改出错", "已切换回原时间轴~"));
                }
            } else timeline_input.getStyleClass().add("error");
        }
        actionEvent.consume();
    }

    @AllArgsConstructor
    static enum TimelineType {

        TIMELINE("时间轴", null, "形如: xx:xx:xx:xx"),
        SECOND("秒", ChronoUnit.SECONDS, "整数，时间偏移量"),
        MILLISECOND("毫秒", ChronoUnit.MILLIS, "整数，时间偏移量"),
        MINUTE("分钟", ChronoUnit.MINUTES, "整数，时间偏移量"),
        HOUR("小时", ChronoUnit.HOURS, "整数，的时间偏移量");

        public final String name;
        public final ChronoUnit rate;
        public final String desc;

        @Override
        public String toString() {
            return this.name;
        }
    }
}
