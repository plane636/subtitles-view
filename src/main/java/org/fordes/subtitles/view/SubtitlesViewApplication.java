package org.fordes.subtitles.view;

import javafx.application.Application;
import lombok.extern.slf4j.Slf4j;
import org.fordes.subtitles.view.core.LaunchApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author fordes
 */
@Slf4j
@SpringBootApplication
public class SubtitlesViewApplication {

    public static void main(String[] args) {
        Application.launch(LaunchApplication.class, args);
    }

}
