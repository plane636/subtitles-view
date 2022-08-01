package org.fordes.subtitles.view.model.PO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

@Data
@TableName(value = "interface")
public class Interface implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField(value = "provider")
    private String provider;

    @TableField(value = "\"type\"")
    private String type;

    @TableField(value = "auth")
    private String auth;

    @TableField(value = "page")
    private String page;

    @TableField(value = "\"template\"")
    private String template;

    @TableField(value = "version")
    private Integer version;

    private static final long serialVersionUID = 1L;

    public static final String COL_ID = "id";

    public static final String COL_PROVIDER = "provider";

    public static final String COL_TYPE = "type";

    public static final String COL_AUTH = "auth";

    public static final String COL_PAGE = "page";

    public static final String COL_TEMPLATE = "template";

    public static final String COL_VERSION = "version";
}