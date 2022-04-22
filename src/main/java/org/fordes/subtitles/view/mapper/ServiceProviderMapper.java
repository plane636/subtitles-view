package org.fordes.subtitles.view.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.fordes.subtitles.view.model.PO.ServiceProvider;

import java.util.List;

/**
 * @author fordes on 2022/4/17
 */
@Mapper
public interface ServiceProviderMapper extends BaseMapper<ServiceProvider> {

    @Select("SELECT sp.* FROM  `service_provider` sp LEFT JOIN `service_support` ss ON sp.id = ss.provider WHERE  ss.type = #{type};")
    List<ServiceProvider> getProviderByType(Integer type);
}