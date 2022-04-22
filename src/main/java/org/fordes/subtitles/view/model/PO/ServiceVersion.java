package org.fordes.subtitles.view.model.PO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author fordes on 2022/4/17
 */
@Data
@Accessors(chain = true)
@TableName(value = "service_version")
public class ServiceVersion implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField(value = "support")
    private Integer support;

    @TableField(value = "\"name\"")
    private String name;

    @TableField(value = "concurrent")
    private Integer concurrent;

    @TableField(value = "carrying")
    private Integer carrying;

    @TableField(value = "remark")
    private String remark;

    private static final long serialVersionUID = 1L;
}