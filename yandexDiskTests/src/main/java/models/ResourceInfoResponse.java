package models;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ResourceInfoResponse {

    @JsonProperty("path")
    private String path;

    @JsonProperty("type")
    private String type;

    @JsonProperty("name")
    private String name;

    @JsonProperty("created")
    private String created;

    @JsonProperty("modified")
    private String modified;

    @JsonProperty("size")
    private Long size;

    @JsonProperty("mime_type")
    private String mimeType;

    @JsonProperty("md5")
    private String md5;

    @JsonProperty("sha256")
    private String sha256;

    @JsonProperty("preview")
    private String preview;

    @JsonProperty("public_key")
    private String publicKey;

    @JsonProperty("public_url")
    private String publicUrl;

    @JsonProperty("_embedded")
    private Embedded embedded;

    @JsonProperty("media_type")
    private String mediaType;

    @JsonProperty("file")
    private String file;

    @JsonProperty("resource_id")
    private String resourceId;

    @JsonProperty("share")
    private ShareInfo share;

    @JsonProperty("revision")
    private Long revision;

    @JsonProperty("comment_ids")
    private CommentIds commentIds;

    @JsonProperty("custom_properties")
    private Map<String, String> customProperties;

    @JsonProperty("exif")
    private ExifInfo exif;

    @JsonProperty("antivirus_status")
    private Map<String, String> antivirusStatus;

    @JsonProperty("photoslice_time")
    private String photosliceTime;

    @JsonProperty("sizes")
    private List<ImageSize> sizes;


    @Setter
    @Getter
    public static class Embedded {
        @JsonProperty("total")
        private Integer total;

        @JsonProperty("limit")
        private Integer limit;

        @JsonProperty("offset")
        private Integer offset;

        @JsonProperty("path")
        private String path;

        @JsonProperty("sort")
        private String sort;

        @JsonProperty("items")
        private List<ResourceInfoResponse> items;

    }

    @Setter
    @Getter
    public static class ShareInfo {
        @JsonProperty("is_owned")
        private Boolean isOwned;

        @JsonProperty("is_root")
        private Boolean isRoot;

        @JsonProperty("rights")
        private String rights;

    }

    @Setter
    @Getter
    public static class CommentIds {
        @JsonProperty("public_resource")
        private String publicResource;

        @JsonProperty("private_resource")
        private String privateResource;

    }

    @Setter
    @Getter
    public static class ExifInfo {
        @JsonProperty("date_time")
        private String dateTime;

        @JsonProperty("gps_latitude")
        private Map<String, Object> gpsLatitude;

        @JsonProperty("gps_longitude")
        private Map<String, Object> gpsLongitude;

    }

    @Setter
    @Getter
    public static class ImageSize {
        @JsonProperty("url")
        private String url;

        @JsonProperty("name")
        private String name;

    }

}