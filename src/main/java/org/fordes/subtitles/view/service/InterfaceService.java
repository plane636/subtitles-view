package org.fordes.subtitles.view.service;

import org.fordes.subtitles.view.enums.ServiceType;
import org.fordes.subtitles.view.model.DTO.Preferences;
import org.fordes.subtitles.view.model.DTO.ServiceInfoDto;
import org.fordes.subtitles.view.model.PO.ServiceProvider;
import org.fordes.subtitles.view.model.PO.ServiceVersion;

import java.util.List;

/**
 * 接口服务
 *
 * @author fordes on 2022/4/17
 */
public interface InterfaceService {

    List<ServiceProvider> getProviders(ServiceType support);

    List<ServiceVersion> getVersions(ServiceType type, ServiceProvider provider);

    ServiceInfoDto getServiceInfo(ServiceType type, ServiceProvider provider);

    List<Preferences> getPreferences(ServiceType type);
}
