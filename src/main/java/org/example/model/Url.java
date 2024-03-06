package org.example.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;
import org.example.util.LocalDateTimeDeserializer;
import java.io.Serializable;
import java.time.LocalDateTime;


@Data
@Builder
public class Url implements Serializable {
    private String shortUrl;
    private String longUrl;
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime lastFetched;
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime expiryDate;
    private UrlAccessCounts urlAccessCounts;

    @JsonCreator
    private Url(@JsonProperty("shortUrl") String shortUrl,
               @JsonProperty("longUrl") String longUrl,
               @JsonProperty("lastFetched") LocalDateTime lastFetched,
               @JsonProperty("expiryDate") LocalDateTime expiryDate,
               @JsonProperty("userAccessCounts") UrlAccessCounts urlAccessCounts) {
        this.shortUrl = shortUrl;
        this.longUrl = longUrl;
        this.lastFetched = lastFetched;
        this.expiryDate = expiryDate;
        this.urlAccessCounts = urlAccessCounts;
    }
}

