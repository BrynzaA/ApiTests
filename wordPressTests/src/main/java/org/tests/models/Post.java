package org.tests.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Post {
    @JsonProperty("id")
    private Integer id;

    @JsonProperty("date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date date;

    @JsonProperty("date_gmt")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date dateGmt;

    @JsonProperty("guid")
    private Guid guid;

    @JsonProperty("link")
    private String link;

    @JsonProperty("modified")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date modified;

    @JsonProperty("modified_gmt")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date modifiedGmt;

    @JsonProperty("slug")
    private String slug;

    @JsonProperty("status")
    private String status;

    @JsonProperty("type")
    private String type;

    @JsonProperty("password")
    private String password;

    @JsonProperty("permalink_template")
    private String permalinkTemplate;

    @JsonProperty("generated_slug")
    private String generatedSlug;

    @JsonProperty("title")
    private Title title;

    @JsonProperty("content")
    private Content content;

    @JsonProperty("author")
    private Integer author;

    @JsonProperty("excerpt")
    private Excerpt excerpt;

    @JsonProperty("featured_media")
    private Integer featuredMedia;

    @JsonProperty("comment_status")
    private String commentStatus;

    @JsonProperty("ping_status")
    private String pingStatus;

    @JsonProperty("format")
    private String format;

    @JsonProperty("meta")
    private Map<String, Object> meta;

    @JsonProperty("sticky")
    private Boolean sticky;

    @JsonProperty("template")
    private String template;

    @Getter
    @Setter
    public static class Guid {
        @JsonProperty("rendered")
        private String rendered;

        @JsonProperty("raw")
        private String raw;
    }

    @Getter
    @Setter
    public static class Title {
        @JsonProperty("rendered")
        private String rendered;

        @JsonProperty("raw")
        private String raw;

        public Title() {}

        public Title(String raw) {
            this.raw = raw;
            this.rendered = raw;
        }
    }

    @Getter
    @Setter
    public static class Content {
        @JsonProperty("rendered")
        private String rendered;

        @JsonProperty("raw")
        private String raw;

        @JsonProperty("block_version")
        private Integer blockVersion;

        @JsonProperty("protected")
        private Boolean protectedContent;

        public Content() {}

        public Content(String raw) {
            this.raw = raw;
            this.rendered = raw;
        }
    }

    @Getter
    @Setter
    public static class Excerpt {
        @JsonProperty("rendered")
        private String rendered;

        @JsonProperty("raw")
        private String raw;

        @JsonProperty("protected")
        private Boolean protectedExcerpt;
    }
}