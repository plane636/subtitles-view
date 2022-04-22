package org.fordes.subtitles.view.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.fordes.subtitles.view.model.DTO.Preferences;
import org.fordes.subtitles.view.model.DTO.ServiceInfoDto;
import org.fordes.subtitles.view.model.PO.ServiceInfo;

import java.util.List;

/**
 * @author fordes on 2022/4/17
 */
@Mapper
public interface ServiceInfoMapper extends BaseMapper<ServiceInfo> {

    @Select("SELECT ss.*,si.id infoId, si.version, si.info FROM `service_support` ss LEFT JOIN `service_info` si ON ss.id = si.support WHERE ss.provider = #{provider} AND ss.type = #{type} ")
    ServiceInfoDto get(@Param("type") Integer type, @Param("provider") Integer provider);

    @Select("SELECT sp.name, si.id FROM `service_info` si LEFT JOIN `service_support` ss ON si.support = ss.id LEFT JOIN service_provider sp ON sp.id = ss.provider WHERE  ss.type = #{type} ")
    List<Preferences> getPreferences(@Param("type") Integer type);
}