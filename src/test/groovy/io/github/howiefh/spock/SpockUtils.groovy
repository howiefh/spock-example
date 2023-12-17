/*
 * @(#)SpockUtils 1.0 2023/12/16
 *
 * Copyright 2023 Feng Hao.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.howiefh.spock;

import groovy.json.JsonSlurper
import org.junit.jupiter.params.shadow.com.univocity.parsers.csv.Csv
import org.junit.jupiter.params.shadow.com.univocity.parsers.csv.CsvParser
import org.springframework.util.ResourceUtils

import java.util.function.Function
import java.util.stream.Collectors

/**
 * Spock工具类.
 *
 * @author fenghao
 * @version 1.0
 * @since 1.0
 */
class SpockUtils {

    /**
     * 从CSV文件解析数据并转换为指定类型的二维列表
     *
     * @param <T> CSV文件中数据转换后的类型
     * @param classPath 类路径下的CSV文件路径
     * @param convertor 字符串到T类型的转换函数
     * @return 转换后的二维列表，其中每个内部列表代表CSV文件的一行
     * @throws FileNotFoundException 如果无法找到指定的类路径资源文件
     */
    static <T> List<List<T>> parseCsv(String classPath, Function<String, T> convertor) throws FileNotFoundException {
        CsvParser csvParser = new CsvParser(Csv.parseRfc4180());
        List<String[]> rows = csvParser.parseAll(ResourceUtils.getFile("classpath:" + classPath));
        return rows.stream().map({ row -> Arrays.stream(row).map(convertor).collect(Collectors.toList()) }).collect(Collectors.toList());
    }

    /**
     * 解析指定类路径下的JSON文件，并返回解析后的列表
     * @param classPath 类路径下的JSON文件路径
     * @return 解析后的列表对象
     * @throws FileNotFoundException 如果指定的类路径资源文件未找到
     */
    static parseJson(String classPath) throws FileNotFoundException {
        return new JsonSlurper().parse(ResourceUtils.getFile("classpath:" + classPath));
    }
}
