package org.fordes.subtitles.view;

import javafx.application.Application;
import org.fordes.subtitles.view.core.LaunchApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SubtitlesViewApplication {

    public static void main(String[] args) {
        Application.launch(LaunchApplication.class, args);
//        SpringApplication.run(SubtitlesViewApplication.class, args);
    }

}
