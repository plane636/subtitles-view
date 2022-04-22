package org.fordes.subtitles.view.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import org.fordes.subtitles.view.constant.CommonConstant;
import org.fordes.subtitles.view.enums.FileEnum;
import org.springframework.lang.NonNull;

import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * 文件工具类
 *
 * @author fordes on 2022/1/23
 */
public class FileUtils {

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

    /**
     * 选择文件
     * @param title 选择框标题内容
     * @param items 选项
     * @return 返回指定文件选择器
     */
    public static FileChooser chooseFile(String title, FileEnum... items) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.setInitialDirectory(new File(CommonConstant.PATH_HOME));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("全部文件", "*.*"));
        if (ArrayUtil.isNotEmpty(items)) {
            fileChooser.getExtensionFilters().addAll(Arrays.stream(items)
                    .filter(e -> e.support)
                    .map(e -> new FileChooser.ExtensionFilter(e.suffix, CommonConstant.PREFIX + e.suffix))
                    .collect(Collectors.toList()));
        }
        return fileChooser;
    }


    /**
     * 选择路径
     * @return 文件夹选择器
     */
    public static DirectoryChooser choosePath(String path) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle(CommonConstant.TITLE_PATH);
        directoryChooser.setInitialDirectory(FileUtil.file(StrUtil.isNotEmpty(path)? path: CommonConstant.PATH_HOME));
        return directoryChooser;
    }
}
