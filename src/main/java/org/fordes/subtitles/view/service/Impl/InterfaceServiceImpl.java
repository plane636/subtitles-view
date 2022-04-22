package org.fordes.subtitles.view.service.Impl;

import lombok.AllArgsConstructor;
import org.fordes.subtitles.view.enums.ServiceType;
import org.fordes.subtitles.view.mapper.ServiceInfoMapper;
import org.fordes.subtitles.view.mapper.ServiceProviderMapper;
import org.fordes.subtitles.view.mapper.ServiceVersionMapper;
import org.fordes.subtitles.view.model.DTO.Preferences;
import org.fordes.subtitles.view.model.DTO.ServiceInfoDto;
import org.fordes.subtitles.view.model.PO.ServiceProvider;
import org.fordes.subtitles.view.model.PO.ServiceVersion;
import org.fordes.subtitles.view.service.InterfaceService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 接口服务
 *
 * @author fordes on 2022/4/17
 */
@Service
@AllArgsConstructor
public class InterfaceServiceImpl implements InterfaceService {

    private final ServiceProviderMapper providersMapper;

    private final ServiceInfoMapper infoMapper;

    private final ServiceVersionMapper versionMapper;

    @Override
    public List<ServiceProvider> getProviders(ServiceType type) {
        return providersMapper.getProviderByType(type.getValue());
    }


    @Override
    public List<ServiceVersion> getVersions(ServiceType type, ServiceProvider provider) {
       return versionMapper.getVersions(type.getValue(), provider.getId());
    }

    @Override
    public ServiceInfoDto getServiceInfo(ServiceType type, ServiceProvider provider) {
        return infoMapper.get(type.getValue(), provider.getId());
    }

    @Override
    public List<Preferences> getPreferences(ServiceType type) {
        return infoMapper.getPreferences(type.getValue());
    }
}
