package org.fordes.subtitles.view.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import lombok.extern.slf4j.Slf4j;
import org.fordes.subtitles.view.model.search.Cases;
import org.fordes.subtitles.view.model.search.Result;
import org.fordes.subtitles.view.model.search.Selector;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 在线字幕搜索服务
 *
 * @author fordes on 2022/2/15
 */
@Slf4j
public class SearchService extends Service<List<Result>> {

    private Cases cases;

    private String[] params;

    @Override
    protected Task<List<Result>> createTask() {
        return new Task<>() {
            @Override
            protected List<Result> call() {
                List<Result> result = CollUtil.newArrayList();

                try {
                    if (ObjectUtil.isEmpty(cases.next)) {
                        //TODO 下载以及解压等流程
                    }else {
                        Connection connection = Jsoup.connect(StrUtil.format(cases.url, params));
                        Document doc = connection.get();

                        List<String> captions = getFields(doc, cases.captionQuery);
                        List<String> texts = getFields(doc, cases.textQuery);
                        List<List<String>> paramList = Arrays.stream(cases.params)
                                .map(e -> getFields(doc, e)).collect(Collectors.toList());


                        for (int i = 0; i < captions.size(); i++) {
                            List<String> param = new ArrayList<>(paramList.size());
                            for (List<String> p : paramList) {
                                param.add(p.get(i));
                            }
                            Result item = Result.builder()
                                    .caption(CollUtil.get(captions, i))
                                    .text(CollUtil.get(texts, i))
                                    .params(param.toArray(String[]::new))
                                    .next(cases.next)
                                    .build();
                            result.add(item);
                        }
                    }

                } catch (IOException e) {
                    log.error(ExceptionUtil.stacktraceToString(e));
                }
                return result;
            }
        };
    }


    public void search(Cases cases, String... params) {
        this.cases = cases;
        this.params = params;
        this.restart();
    }


    private static List<String> getFields(Document doc, Selector selector) {
        if (ObjectUtil.isNotEmpty(selector)) {
            return doc.select(selector.css).stream()
                    .map(e -> getField(e, selector.attr, selector.regular, selector.format))
                    .collect(Collectors.toList());
        }else {
            return Collections.emptyList();
        }
    }


    private static String getField(Element element, String attr, String regular, String format) {
        String attrField = StrUtil.isBlank(attr)?
                element.text(): element.attr(attr);
        String regField = StrUtil.isBlank(regular)?
                StrUtil.trim(attrField): CollUtil.join(ReUtil.findAll(regular, attrField, 0), StrUtil.EMPTY);
        return StrUtil.isBlank(format)? regField: StrUtil.format(format, regField);
    }
}
