package org.fordes.subtitles.view.utils;

import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import org.springframework.lang.NonNull;

import java.io.InputStream;

/**
 * 文件工具类
 *
 * @author fordes on 2022/1/23
 */
public class FileUtil {

    /**
     * 根据路径获取文件流，支持http和resource
     * @param path
     * @return
     */
    public static InputStream getStream(@NonNull String path) {
        if (ReUtil.isMatch("^http[s]?://.*", path)) {
            HttpResponse response = HttpUtil.createGet(path, true).execute();
            if (response.isOk()) {
                return response.bodyStream();
            }
        }else {
            ClassPathResource resource = new ClassPathResource(path);
            return resource.getStream();
        }

        throw new RuntimeException(StrUtil.format("resource: {} not found", path));
    }

}
