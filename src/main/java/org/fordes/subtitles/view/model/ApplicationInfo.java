package org.fordes.subtitles.view.model;

import com.jthemedetecor.OsThemeDetector;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.fordes.subtitles.view.model.PO.SearchCases;

import java.util.List;

/**
 * @author fordes on 2022/2/3
 */
public class ApplicationInfo {

    public static List<SearchCases> searchCases;

    public static Stage stage;

    public static Pane root;

    public static OsThemeDetector detector;
}
