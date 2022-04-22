package org.fordes.subtitles.view.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.fordes.subtitles.view.mapper.ConfigMapper;
import org.fordes.subtitles.view.model.PO.Config;
import org.fordes.subtitles.view.service.ConfigService;
import org.springframework.stereotype.Service;

/**
 * @author fordes on 2022/4/17
 */
@Service
public class ConfigServiceImpl extends ServiceImpl<ConfigMapper, Config> implements ConfigService {

}
