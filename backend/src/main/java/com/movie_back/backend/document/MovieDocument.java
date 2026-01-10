package com.movie_back.backend.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;

/**
 * Elasticsearch 电影文档
 * 用于全文搜索和综合查询
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "movies")
public class MovieDocument {

    @Id
    private Long id;

    /**
     * 电影标题 - 使用 text 类型支持全文搜索
     * 注意：使用 standard 分词器，如需中文分词请安装 IK 插件后改为 ik_max_word
     */
    @Field(type = FieldType.Text, analyzer = "standard")
    private String title;

    /**
     * 发行年份
     */
    @Field(type = FieldType.Integer)
    private Integer releaseYear;

    /**
     * 时长(分钟)
     */
    @Field(type = FieldType.Integer)
    private Integer duration;

    /**
     * 类型/流派 - 支持分词搜索
     */
    @Field(type = FieldType.Text, analyzer = "standard")
    private String genre;

    /**
     * 语言
     */
    @Field(type = FieldType.Keyword)
    private String language;

    /**
     * 国家/地区
     */
    @Field(type = FieldType.Keyword)
    private String country;

    /**
     * 简介 - 支持全文搜索
     */
    @Field(type = FieldType.Text, analyzer = "standard")
    private String synopsis;

    /**
     * 平均评分
     */
    @Field(type = FieldType.Double)
    private Double averageRating;

    /**
     * 海报URL
     */
    @Field(type = FieldType.Keyword, index = false)
    private String posterUrl;

    /**
     * 演员名称列表 - 支持搜索
     */
    @Field(type = FieldType.Text, analyzer = "standard")
    private List<String> actorNames;

    /**
     * 导演名称列表 - 支持搜索
     */
    @Field(type = FieldType.Text, analyzer = "standard")
    private List<String> directorNames;

    /**
     * 更新时间戳
     */
    @Field(type = FieldType.Long)
    private Long updateTimestamp;
}
