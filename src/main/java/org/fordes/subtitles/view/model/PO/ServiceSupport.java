package org.fordes.subtitles.view.model.PO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;
import org.fordes.subtitles.view.enums.ServiceType;

import java.io.Serializable;

/**
 * @author fordes on 2022/4/17
 */
@Data
@Accessors(chain = true)
@TableName(value = "service_support")
public class ServiceSupport implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField(value = "type")
    private ServiceType type;

    @TableField(value = "provider")
    private Integer provider;

    @TableField(value = "page")
    private String page;

    @TableField(value = "params")
    private String params;

    @TableField(value = "remark")
    private String remark;

    public ServiceSupport setType(Integer code) {
        this.type = ServiceType.of(code);
        return this;
    }

    private static final long serialVersionUID = 1L;
}