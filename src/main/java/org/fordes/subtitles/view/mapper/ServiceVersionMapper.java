package org.fordes.subtitles.view.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.fordes.subtitles.view.model.PO.ServiceVersion;

import java.util.List;

/**
 * @author fordes on 2022/4/17
 */
@Mapper
public interface ServiceVersionMapper extends BaseMapper<ServiceVersion> {

    @Select("SELECT sv.* FROM `service_version` sv LEFT JOIN `service_support` ss ON sv.support = ss.id WHERE ss.type = #{type} AND ss.provider = #{provider}")
    List<ServiceVersion> getVersions(@Param("type") Integer type, @Param("provider") Integer provider);
}