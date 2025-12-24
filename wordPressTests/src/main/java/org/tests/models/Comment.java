package org.tests.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.Map;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Comment {
    @JsonProperty("id")
    private Integer id;

    @JsonProperty("post")
    private Integer post;

    @JsonProperty("parent")
    private Integer parent;

    @JsonProperty("author")
    private Integer author;

    @JsonProperty("author_name")
    private String authorName;

    @JsonProperty("author_email")
    private String authorEmail;

    @JsonProperty("author_url")
    private String authorUrl;

    @JsonProperty("author_ip")
    private String authorIp;

    @JsonProperty("author_user_agent")
    private String authorUserAgent;

    @JsonProperty("date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date date;

    @JsonProperty("date_gmt")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date dateGmt;

    @JsonProperty("content")
    private Content content;

    @JsonProperty("link")
    private String link;

    @JsonProperty("status")
    private String status;

    @JsonProperty("type")
    private String type;

    @JsonProperty("author_avatar_urls")
    private Map<String, String> authorAvatarUrls;

    @JsonProperty("meta")
    private Map<String, Object> meta;

    @Getter
    @Setter
    public static class Content {
        @JsonProperty("rendered")
        private String rendered;

        @JsonProperty("raw")
        private String raw;

        public Content() {}

        public Content(String raw) {
            this.raw = raw;
            this.rendered = raw;
        }
    }
}
