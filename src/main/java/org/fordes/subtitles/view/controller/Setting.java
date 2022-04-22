package org.fordes.subtitles.view.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.swing.DesktopUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;
import lombok.extern.slf4j.Slf4j;
import org.fordes.subtitles.view.enums.ServiceType;
import org.fordes.subtitles.view.event.ThemeChangeEvent;
import org.fordes.subtitles.view.event.ToastConfirmEvent;
import org.fordes.subtitles.view.mapper.ConfigMapper;
import org.fordes.subtitles.view.mapper.ServiceInfoMapper;
import org.fordes.subtitles.view.model.ApplicationInfo;
import org.fordes.subtitles.view.model.DTO.ServiceInfoDto;
import org.fordes.subtitles.view.model.PO.ServiceInfo;
import org.fordes.subtitles.view.model.PO.ServiceProvider;
import org.fordes.subtitles.view.model.PO.ServiceVersion;
import org.fordes.subtitles.view.service.InterfaceService;
import org.fordes.subtitles.view.utils.FileUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.awt.*;
import java.io.File;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 语音转换 控制器
 *
 * @author fordes on 2022/4/8
 */
@Slf4j
@Component
public class Setting extends DelayInitController {

    @FXML
    private GridPane tips, infoPanel;

    @FXML
    private ToggleGroup themeGroup, editorModeGroup, exitModeGroup;

    @FXML
    private JFXComboBox<ServiceVersion> version;

    @FXML
    private JFXComboBox<ServiceType> type;

    @FXML
    private JFXComboBox<ServiceProvider> provider;

    @FXML
    private JFXComboBox<String> fontFace;

    @FXML
    private JFXComboBox<Integer> fontSize;

    @FXML
    private TextField outPath;

    @Resource
    private InterfaceService interfaceService;

    @Resource
    private ServiceInfoMapper serviceInfoMapper;

    @Resource
    private ConfigMapper configMapper;


    @Override
    public void delayInit() {
        initInterface();
        initPreference();
        //监听器用于保存配置
        root.visibleProperty().addListener((observableValue, aBoolean, t1) -> {
            if (!t1) {
                ApplicationInfo.config.setEditMode(Convert.toBool(editorModeGroup.getSelectedToggle().getUserData()));
                ApplicationInfo.config.setExitMode(Convert.toBool(exitModeGroup.getSelectedToggle().getUserData()));
                ApplicationInfo.config.setTheme(Convert.toBool(themeGroup.getSelectedToggle().getUserData()));
                ApplicationInfo.config.setFontFace(fontFace.getValue());
                ApplicationInfo.config.setFontSize(fontSize.getValue());
                if (FileUtil.exist(outPath.getText().trim())) {
                    ApplicationInfo.config.setOutPath(outPath.getText().trim());
                }else {
                    outPath.setText(ApplicationInfo.config.getOutPath());
                }
                configMapper.updateById(ApplicationInfo.config);
            }
        });
        super.delayInit();
    }

    void initPreference() {
        //编辑器 字体/字体大小
        fontFace.getItems().addAll(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
        fontSize.getItems().addAll(CollUtil.newArrayList(10, 12, 14, 16, 18, 20, 24, 36));

        //读取配置设置默认值
        fontFace.getSelectionModel().select(ApplicationInfo.config.getFontFace());
        fontSize.getSelectionModel().select(ApplicationInfo.config.getFontSize());
        editorModeGroup.getToggles().forEach(item -> {
            if (Convert.toBool(item.getUserData()).equals(ApplicationInfo.config.getEditMode())) {
                item.setSelected(true);
            }
        });
        themeGroup.getToggles().forEach(item -> {
            if (ObjectUtil.equal(ApplicationInfo.config.getTheme(), Convert.toBool(item.getUserData()))) {
                item.setSelected(true);
            }
        });
        exitModeGroup.getToggles().forEach(item -> {
            if (Convert.toBool(item.getUserData()).equals(ApplicationInfo.config.getExitMode())) {
                item.setSelected(true);
            }
        });
        outPath.setText(ApplicationInfo.config.getOutPath());

        //主题监听，实时生效
        themeGroup.selectedToggleProperty().addListener((observableValue, toggle, t1) ->
                ApplicationInfo.stage.fireEvent(new ThemeChangeEvent(Convert.toBool(t1.getUserData()))));
    }

    void initInterface() {
        //接口类型
        type.getItems().addAll(ServiceType.values());
        type.setConverter(new StringConverter<>() {
            @Override
            public String toString(ServiceType type) {
                return type.getDesc();
            }

            @Override
            public ServiceType fromString(String s) {
                return null;
            }
        });
        type.getSelectionModel().selectedItemProperty().addListener((observableValue, type, t1) -> {
            provider.getItems().clear();
            if (null != t1) {
                provider.getItems().addAll(interfaceService.getProviders(t1));
                if (provider.getItems().size() == 1) {
                    provider.getSelectionModel().select(0);
                }
            }
        });
        //服务商
        provider.setConverter(new StringConverter<>() {
            @Override
            public String toString(ServiceProvider provider) {
                return provider.getName();
            }

            @Override
            public ServiceProvider fromString(String s) {
                return null;
            }
        });
        provider.getSelectionModel().selectedItemProperty().addListener((observableValue, supportDto, t1) -> {
            version.getItems().clear();
            if (null != t1) {
                version.getItems().addAll(interfaceService.getVersions(type.getValue(), t1));
                if (version.getItems().size() == 1) {
                    version.getSelectionModel().select(0);
                }
            }
        });
        //套餐版本
        version.setConverter(new StringConverter<>() {
            @Override
            public String toString(ServiceVersion serviceVersion) {
                return serviceVersion.getName();
            }

            @Override
            public ServiceVersion fromString(String s) {
                return null;
            }
        });
        version.getSelectionModel().selectedItemProperty().addListener((observableValue, serviceVersion, t1) -> {
            if (null != t1) {
                tips.setVisible(false);
                version.setTooltip(new Tooltip(t1.getRemark()));
                buildInfoFrame(interfaceService.getServiceInfo(type.getValue(), provider.getValue()));
            } else {
                tips.setVisible(true);
            }
        });
        //提示区
        tips.visibleProperty().addListener((observableValue, aBoolean, t1) -> infoPanel.setVisible(!t1));
    }

    void buildInfoFrame(ServiceInfoDto infoDto) {
        infoPanel.getChildren().clear();
        JSONObject paramJson = JSONUtil.parseObj(null != infoDto.getInfoId() ? infoDto.getInfo() : infoDto.getParams());
        AtomicReference<Integer> sort = new AtomicReference<>(0);
        paramJson.forEach((k, v) -> {
            Label label = new Label(k);
            label.getStyleClass().add("item");
            GridPane.setHalignment(label, HPos.LEFT);
            GridPane.setValignment(label, VPos.CENTER);
            GridPane.setMargin(label, new Insets(0, 0, 0, 20));
            infoPanel.add(label, 0, sort.get());

            TextField textField = new TextField(ObjectUtil.isNotEmpty(v) ? v.toString() : StrUtil.EMPTY);
            textField.getStyleClass().add("item");
            textField.setUserData(k);
            GridPane.setHalignment(textField, HPos.LEFT);
            GridPane.setValignment(textField, VPos.CENTER);
            infoPanel.add(textField, 1, sort.getAndSet(sort.get() + 1));
        });

        JFXButton save = new JFXButton("保存");
        save.setPrefSize(80, 30);
        save.getStyleClass().add("normal-button");
        save.setUserData(infoDto);
        save.setOnAction(event -> {

                JSONObject param = new JSONObject();
                infoPanel.getChildren().forEach(e -> {
                    if (e instanceof TextField) {
                        param.putOpt((String) e.getUserData(), ((TextField) e).getText());
                    }
                });
                ServiceInfo info = new ServiceInfo()
                        .setId(infoDto.getInfoId())
                        .setSupport(infoDto.getId())
                        .setVersion(version.getValue().getId())
                        .setInfo(param.toString());
            try {
                int result = info.getId() != null ?
                        serviceInfoMapper.updateById(info) : serviceInfoMapper.insert(info);
                if (result > 0) {
                    tips.setVisible(true);
                    ApplicationInfo.stage.fireEvent(new ToastConfirmEvent("保存成功", "接口信息已经保存"));
                    return;
                }
            }catch (Exception e) {
                log.error("接口信息保存失败 => {}", JSONUtil.toJsonStr(info));
                log.error(ExceptionUtil.stacktraceToString(e));
            }
            ApplicationInfo.stage.fireEvent(new ToastConfirmEvent("保存失败", "数据操作失败，错误已记录"));
        });
        GridPane.setHalignment(save, HPos.LEFT);
        GridPane.setValignment(save, VPos.CENTER);
        GridPane.setMargin(save, new Insets(0, 0, 0, 220));
        infoPanel.add(save, 1, sort.get());

        if (StrUtil.isNotEmpty(infoDto.getPage())) {
            JFXButton applyFor = new JFXButton("去申请");
            applyFor.setPrefSize(80, 30);
            applyFor.getStyleClass().add("normal-button");
            applyFor.setTooltip(new Tooltip(infoDto.getPage()));
            applyFor.setOnAction(event -> DesktopUtil.browse(infoDto.getPage()));
            GridPane.setHalignment(applyFor, HPos.LEFT);
            GridPane.setValignment(applyFor, VPos.CENTER);
            GridPane.setMargin(applyFor, new Insets(0, 0, 0, 120));
            infoPanel.add(applyFor, 1, sort.get());
        }
    }

    @FXML
    private void onChooseOutPath(MouseEvent event) {
        File path = FileUtils.choosePath(outPath.getText().trim()).showDialog(ApplicationInfo.stage);
        if (path != null && StrUtil.isNotEmpty(path.getPath())) {
            outPath.setText(path.getPath());
        }
        event.consume();
    }
}
