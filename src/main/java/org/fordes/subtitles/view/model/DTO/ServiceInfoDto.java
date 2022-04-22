package org.fordes.subtitles.view.model.DTO;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.fordes.subtitles.view.model.PO.ServiceSupport;

/**
 * @author fordes on 2022/4/19
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class ServiceInfoDto extends ServiceSupport {

    @TableField(value = "info_id")
    private Integer infoId;

    @TableField(value = "version")
    private Integer version;

    @TableField(value = "info")
    private String info;
}
