package org.fordes.subtitles.view.model.search;

import lombok.Builder;
import lombok.Getter;

/**
 * @author fordes on 2022/2/12
 */
@Getter
@Builder
public class Result {

    private String caption;

    private String text;

    private Cases next;

    private String[] params;
}
