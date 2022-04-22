package org.fordes.subtitles.view.model.PO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;
import org.fordes.subtitles.view.constant.CommonConstant;

import java.io.Serializable;

/**
 * @author fordes on 2022/4/17
 */
@Data
@Accessors
@TableName(value = "config")
public class Config implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField(value = "theme")
    private Boolean theme = null;

    @TableField(value = "font_face")
    private String fontFace = "黑体";

    @TableField(value = "font_size")
    private Integer fontSize = 18;

    @TableField(value = "edit_mode")
    private Boolean editMode = Boolean.FALSE;

    @TableField(value = "exit_mode")
    private Boolean exitMode = Boolean.FALSE;

    @TableField(value = "out_path")
    private String outPath = CommonConstant.PATH_HOME;

    private static final long serialVersionUID = 1L;
}