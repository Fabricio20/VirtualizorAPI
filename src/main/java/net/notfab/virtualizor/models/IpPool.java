package net.notfab.virtualizor.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import net.notfab.virtualizor.advice.NumericBooleanDeserializer;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class IpPool {

    @JsonProperty("ippid")
    private long id;

    @JsonProperty("ippool_name")
    private String name;

    @JsonProperty("ipv6")
    @JsonDeserialize(using = NumericBooleanDeserializer.class)
    private boolean ipv6;

    @JsonProperty("gateway")
    private String gateway;

    @JsonProperty("netmask")
    private String mask;

    @JsonProperty("totalip")
    private long totalIps;

    @JsonProperty("freeip")
    private long freeIps;

}
