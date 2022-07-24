package org.fordes.subtitles.view.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.Pane;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * 控制器抽象，继承并实现delayInit()方法即可在面板首次显示时进行初始化操作
 *
 * @author fordes on 2022/4/22
 */
@Component
public abstract class DelayInitController implements Initializable {

    @FXML
    public Pane root;

    private boolean isInit = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        root.visibleProperty().addListener((observableValue, aBoolean, t1) -> {
            if (!isInit && t1) {
                delayInit();
                isInit = true;
            }
        });
    }

    /**
     * 懒初始化，在首次显示时执行
     */
    public void delayInit() {}
}
