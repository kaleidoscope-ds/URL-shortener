package org.example.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UrlAccessCounts {
    private long last24HoursCount;
    private long pastWeekCount;
    private long allTimeCount;
    @JsonCreator
    private UrlAccessCounts(@JsonProperty("last24HoursCount") long last24HoursCount,
               @JsonProperty("pastWeekCount") long pastWeekCount,
               @JsonProperty("allTimeCount") long allTimeCount){
       this.allTimeCount = allTimeCount;
       this.last24HoursCount = last24HoursCount;
       this.pastWeekCount = pastWeekCount;
    }
}