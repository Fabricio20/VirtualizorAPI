package net.notfab.virtualizor.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VzPlan {

    @JsonProperty("plid")
    private String id;

    @JsonProperty("plan_name")
    private String name;

    @JsonProperty("space")
    private String disk;

    private String ram;
    private String swap;
    private String cores;

    @JsonProperty("network_speed")
    private String networkSpeed;

}
