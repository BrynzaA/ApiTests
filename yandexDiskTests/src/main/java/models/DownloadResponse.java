package models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DownloadResponse {
    @JsonProperty("href")
    private String href;

    @JsonProperty("method")
    private String method;

    @JsonProperty("templated")
    private boolean templated;
}
