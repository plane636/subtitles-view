package org.fordes.subtitles.view.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.jfoenix.controls.JFXComboBox;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fordes.subtitles.view.constant.CommonConstant;
import org.fordes.subtitles.view.constant.StyleClassConstant;
import org.fordes.subtitles.view.enums.EditToolEventEnum;
import org.fordes.subtitles.view.enums.FileEnum;
import org.fordes.subtitles.view.enums.ServiceType;
import org.fordes.subtitles.view.event.EditToolEvent;
import org.fordes.subtitles.view.event.LoadingEvent;
import org.fordes.subtitles.view.event.ToastChooseEvent;
import org.fordes.subtitles.view.event.ToastConfirmEvent;
import org.fordes.subtitles.view.factory.TranslateServiceFactory;
import org.fordes.subtitles.view.model.ApplicationInfo;
import org.fordes.subtitles.view.model.DTO.AvailableServiceInfo;
import org.fordes.subtitles.view.model.DTO.Subtitle;
import org.fordes.subtitles.view.model.PO.Language;
import org.fordes.subtitles.view.service.InterfaceService;
import org.fordes.subtitles.view.service.translate.TranslateService;
import org.fordes.subtitles.view.utils.CacheUtil;
import org.fordes.subtitles.view.utils.SubtitleUtil;
import org.fordes.subtitles.view.utils.submerge.subtitle.ass.ASSTime;
import org.fordes.subtitles.view.utils.submerge.subtitle.common.TimedLine;
import org.fordes.subtitles.view.utils.submerge.subtitle.common.TimedTextFile;
import org.fordes.subtitles.view.utils.submerge.subtitle.srt.SRTTime;
import org.fxmisc.richtext.StyleClassedTextArea;
import org.fxmisc.richtext.model.TwoDimensional;
import org.mozilla.universalchardet.Constants;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.net.URL;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * 编辑工具 控制器
 *
 * @author fordes on 2022/7/15
 */
@Slf4j
@EnableAsync
@Component
public class EditTool extends DelayInitController {

    private static final Map<EditToolEventEnum, GridPane> bindMap = MapUtil.newHashMap();

    @FXML
    private CheckMenuItem search_case, search_regex, replace_case, replace_regex;

    @FXML
    private ChoiceBox<String> code_choice, font_family;

    @FXML
    private ChoiceBox<AvailableServiceInfo> translate_source;

    @FXML
    private ChoiceBox<String> translate_mode;

    @FXML
    private ChoiceBox<Language> translate_original, translate_target;

    @FXML
    private JFXComboBox<Integer> font_size;

    @FXML
    private ChoiceBox<TimelineType> timeline_option;

    @FXML
    private TextField timeline_input, jump_input, search_input, replace_input, replace_find_input;

    @Resource
    private ThreadPoolExecutor globalExecutor;

    @Resource
    private InterfaceService interfaceService;

    private static Subtitle subtitle;

    private static StyleClassedTextArea area;

    private static ToggleButton editMode;

    private static int max;

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
        ApplicationInfo.stage.addEventHandler(EditToolEvent.EVENT_TYPE, event -> {
            if (ObjectUtil.isNull(event.getEditMode()) || ObjectUtil.isNull(event.getSource()) ||
                    ObjectUtil.isNull(event.getType())) {
                return;
            }

            if (bindMap.containsKey(event.getType())) {
                root.setVisible(true);
                bindMap.get(event.getType()).setVisible(true);
            }
            subtitle = event.getSubtitle();
            area = event.getSource();
            editMode = event.getEditMode();
            switch (event.getType()) {
                case SEARCH: //搜索
                    search_input.requestFocus();
                    break;

                case REPLACE://替换
                    replace_find_input.requestFocus();
                    break;

                case JUMP://跳转
                    jump_input.requestFocus();
                    max = 0;
                    for (TimedLine timedLine : subtitle.getTimedTextFile().getTimedLines()) {
                        max += timedLine.getTextLines().size();
                    }
                    break;

                case FONT: //字体（样式）
                    font_family.getSelectionModel().select(ApplicationInfo.config.getFontFace());
                    font_size.getSelectionModel().select(ApplicationInfo.config.getFontSize());
                    break;

                case TIMELINE: //时间轴
                    TimedLine start = CollUtil.getFirst(subtitle.getTimedTextFile().getTimedLines());
                    timeline_input.setPromptText(start.getTime().getStart().toString());
                    timeline_input.requestFocus();
                    break;

                case CODE://编码
                    code_choice.getSelectionModel().select(subtitle.getCharset());
                    break;

                case TRANSLATE:
                    List<AvailableServiceInfo> list = interfaceService.getAvailableService(ServiceType.TRANSLATE);
                    Collection<AvailableServiceInfo> gap = CollUtil.subtract(list, translate_source.getItems());
                    Collection<AvailableServiceInfo> neg = CollUtil.subtract(translate_source.getItems(), list);
                    if (!gap.isEmpty()) {
                        translate_source.getItems().addAll(gap);
                    }
                    if (!neg.isEmpty()) {
                        translate_source.getItems().removeAll(neg);
                    }
                    break;

                case REF: //刷新
                    try {
                        SubtitleUtil.parse(subtitle);
                        area.clear();
                        area.append(SubtitleUtil.toStr(subtitle.getTimedTextFile(), editMode.isSelected()), StrUtil.EMPTY);
                    } catch (Exception e) {
                        log.error(ExceptionUtil.stacktraceToString(e));
                        ApplicationInfo.stage.fireEvent(new ToastConfirmEvent("编码更改出错", "已切换回原编码~"));
                    }
                    break;
            }
        });
        //异步执行耗时任务
        globalExecutor.execute(this::asyncInit);
    }

    public void asyncInit() {
        //编码选择框
        code_choice.getItems().addAll(Arrays.stream(ReflectUtil.getFieldsValue(Constants.class))
                .map(Object::toString).toArray(String[]::new));
        //初始化字体大小
        font_size.getItems().addAll(CollUtil.newArrayList(12, 14, 16, 18, 20, 22, 24, 26, 28, 36, 48, 72));
        //初始化字体列表
        font_family.getItems().addAll(Font.getFontNames());
        //时间轴校正选项
        timeline_option.getItems().addAll(TimelineType.values());
        timeline_option.getSelectionModel().selectedItemProperty().addListener((observableValue, strings, t1)
                -> timeline_input.setPromptText(t1.desc));
        timeline_option.getSelectionModel().select(0);
        timeline_input.textProperty().addListener((observableValue, s, t1)
                -> timeline_input.getStyleClass().remove("error"));
        //翻译相关
        translate_original.getSelectionModel().selectedItemProperty().addListener((observableValue, strings, t1) -> {
            if (t1 != null) {
                Collection<Language> gap = CollUtil.subtract(t1.getTarget().stream()
                        .filter(e -> e.isGeneral() == ApplicationInfo.config.getLanguageListMode())
                        .collect(Collectors.toList()), translate_target.getItems());
                Collection<Language> neg = CollUtil.subtract(translate_target.getItems(), t1.getTarget());
                if (!gap.isEmpty()) {
                    translate_target.getItems().addAll(gap);
                }
                if (!neg.isEmpty()) {
                    translate_target.getItems().removeAll(neg);
                }
            } else translate_target.getItems().clear();
        });
        translate_source.getSelectionModel().selectedItemProperty()
                .addListener((observableValue, availableServiceInfo, t1) -> {
                    if (t1 != null) {
                        translate_original.getItems().clear();
                        translate_original.getItems().addAll(CacheUtil.getLanguageDict(ServiceType.TRANSLATE, t1.getProvider()));
                        translate_original.getSelectionModel().selectFirst();
                    }
                });
        translate_source.getItems().clear();
        translate_source.getItems().addAll(interfaceService.getAvailableService(ServiceType.TRANSLATE));
        translate_source.getSelectionModel().selectFirst();

        translate_mode.getItems().addAll(CommonConstant.TRANSLATE_REPLACE, CommonConstant.TRANSLATE_BILINGUAL);
        translate_mode.getSelectionModel().selectFirst();
        //回车提交操作
        timeline_input.setOnAction(this::applyTimeline);
        jump_input.setOnAction(this::applyJump);
        search_input.setOnAction(this::applySearch);
        replace_find_input.setOnAction(this::applyReplaceFind);
        replace_input.setOnAction(this::applyReplaceNext);
        //错误输入
        jump_input.textProperty().addListener((observableValue, s, t1)
                -> jump_input.getStyleClass().remove(StyleClassConstant.ERROR));
        search_input.textProperty().addListener((observableValue, s, t1)
                -> search_input.getStyleClass().remove(StyleClassConstant.ERROR));
        timeline_input.textProperty().addListener((observableValue, s, t1)
                -> timeline_input.getStyleClass().remove(StyleClassConstant.ERROR));
    }

    @FXML
    private void onClose(ActionEvent actionEvent) {
        actionEvent.consume();
        area = null;
        subtitle = null;
        editMode = null;
        root.setVisible(false);
    }

    @FXML
    private void applyCode(ActionEvent actionEvent) {
        String original = subtitle.getCharset();
        try {
            subtitle.setCharset(code_choice.getSelectionModel().getSelectedItem());
            SubtitleUtil.parse(subtitle);
            area.clear();
            area.append(SubtitleUtil.toStr(subtitle.getTimedTextFile(), editMode.isSelected()), StrUtil.EMPTY);
        } catch (Exception e) {
            log.error(ExceptionUtil.stacktraceToString(e));
            ApplicationInfo.stage.fireEvent(new ToastConfirmEvent("编码更改出错", "已切换回原编码~"));
            subtitle.setCharset(original);
            code_choice.getSelectionModel().select(original);
        }
        actionEvent.consume();
    }

    @FXML
    private void applyFont(ActionEvent actionEvent) {
        String originalFontFamily = ApplicationInfo.config.getFontFace();
        Integer originalFontSize = ApplicationInfo.config.getFontSize();
        try {
            ApplicationInfo.config.setFontSize(Convert.toInt(font_size.getValue()));
            ApplicationInfo.config.setFontFace(font_family.getValue());
            area.setStyle(StrUtil.format(StyleClassConstant.FONT_STYLE_TEMPLATE,
                    ApplicationInfo.config.getFontSize(), ApplicationInfo.config.getFontFace()));
            area.requestFocus();
        } catch (Exception e) {
            log.error(ExceptionUtil.stacktraceToString(e));
            ApplicationInfo.config.setFontSize(originalFontSize);
            ApplicationInfo.config.setFontFace(originalFontFamily);
            font_family.setValue(originalFontFamily);
            font_size.setValue(originalFontSize);
            ApplicationInfo.stage.fireEvent(new ToastConfirmEvent("字体更改出错", "已切换回原字体~"));
        }
        actionEvent.consume();
    }

    @FXML
    private void applyTimeline(ActionEvent actionEvent) {
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
                        .revise(subtitle.getTimedTextFile(), newTime, null, editMode.isSelected());
                subtitle.setTimedTextFile(target);
                SubtitleUtil.write(subtitle, success -> {
                    ApplicationInfo.stage.fireEvent(new LoadingEvent(!success));
                    if (success) {
                        area.clear();
                        area.append(SubtitleUtil.toStr(subtitle.getTimedTextFile(),
                                editMode.isSelected()), StrUtil.EMPTY);
                    } else throw new RuntimeException("写入失败");
                });
            } catch (Exception e) {
                log.error(ExceptionUtil.stacktraceToString(e));
                subtitle.setTimedTextFile(original);
                ApplicationInfo.stage.fireEvent(new ToastConfirmEvent("时间轴更改出错", "已切换回原时间轴~"));
            }
        } else timeline_input.getStyleClass().add(StyleClassConstant.ERROR);
        actionEvent.consume();
    }

    @FXML
    private void applyJump(ActionEvent actionEvent) {
        String text = jump_input.getText();
        int value = NumberUtil.isInteger(text) ? NumberUtil.parseInt(text) : 0;

        if (value > 0 && value <= max) {
            TwoDimensional.Position position = area.position(value, 1);
            area.moveTo(position.toOffset());
            area.requestFollowCaret();
        } else {
            jump_input.getStyleClass().add(StyleClassConstant.ERROR);
        }
        actionEvent.consume();
    }

    @FXML
    private void applySearch(ActionEvent actionEvent) {
        String str = search_input.getText();
        if (StrUtil.isNotBlank(str)) {
            SubtitleUtil.search(area, str, search_case.isSelected(), search_regex.isSelected());
        } else search_input.getStyleClass().add(StyleClassConstant.ERROR);
        actionEvent.consume();
    }

    @FXML
    private void applyReplaceNext(ActionEvent actionEvent) {
        applyReplace(false);
        actionEvent.consume();
    }

    @FXML
    private void applyReplaceAll(ActionEvent actionEvent) {
        applyReplace(true);
        actionEvent.consume();
    }

    @FXML
    private void applyReplaceFind(ActionEvent actionEvent) {
        String str = replace_find_input.getText();
        if (StrUtil.isNotBlank(str)) {
            SubtitleUtil.find(area, str, replace_case.isSelected(), replace_regex.isSelected());
        }
        actionEvent.consume();
    }

    private void applyReplace(boolean isAll) {
        if (editMode.isSelected()) {
            String replaceText = replace_input.getText();
            String searchText = replace_find_input.getText();
            if (StrUtil.isAllNotBlank(replaceText, searchText)) {
                try {
                    SubtitleUtil.replace(area, subtitle, searchText, replaceText, isAll,
                            replace_case.isSelected(), replace_regex.isSelected());
                } catch (Exception e) {
                    log.error(ExceptionUtil.stacktraceToString(e));
                    ApplicationInfo.stage.fireEvent(new ToastConfirmEvent("替换出错", "已切换回原文本~"));
                }
            }
        } else ApplicationInfo.stage.fireEvent(new ToastChooseEvent("操作受限", "是否切换至完整模式?",
                "切换", () -> editMode.setSelected(true)));
    }

    @FXML
    private void applyTranslate(ActionEvent actionEvent) {
        AvailableServiceInfo source = translate_source.getValue();
        boolean mode = StrUtil.equals(CommonConstant.TRANSLATE_BILINGUAL, translate_mode.getValue());
        Language origin = translate_original.getValue();
        Language target = translate_target.getValue();
        if (source != null && origin != null && target != null) {

            TranslateService service = TranslateServiceFactory.getService(source.getProvider().getValue());
            ApplicationInfo.stage.fireEvent(new LoadingEvent(true));
            globalExecutor.execute(() -> {
                service.translate(subtitle, target.getCode(), origin.getCode(), source.getVersionInfo(), mode, source.getAuth());
            });
        }
        actionEvent.consume();
    }


    /**
     * 时间轴校正 操作类型枚举
     */
    @AllArgsConstructor
    enum TimelineType {

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
