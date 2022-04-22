package org.fordes.subtitles.view.model.PO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author fordes on 2022/4/19
 */
@Data
@Accessors(chain = true)
@TableName(value = "service_info")
public class ServiceInfo implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField(value = "support")
    private Integer support;

    @TableField(value = "version")
    private Integer version;

    @TableField(value = "info")
    private String info;

    private static final long serialVersionUID = 1L;
}