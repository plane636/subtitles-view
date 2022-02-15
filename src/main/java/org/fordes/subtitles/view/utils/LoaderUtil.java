package org.fordes.subtitles.view.utils;

import cn.hutool.core.io.resource.ClassPathResource;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author fordes on 2022/2/9
 */
public class LoaderUtil {

    private static FXMLLoader loader = new FXMLLoader();

    public static <T> T load(InputStream in) throws IOException {
        return loader.load(in);
    }

    public static StackPane loadSearchItem() throws IOException {

        ClassPathResource resource = new ClassPathResource("/fxml/subtitle-search-item.fxml");
        return FXMLLoader.load(resource.getUrl());
    }
}
