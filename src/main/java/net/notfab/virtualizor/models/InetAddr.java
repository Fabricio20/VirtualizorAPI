package net.notfab.virtualizor.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import net.notfab.virtualizor.advice.NumericBooleanDeserializer;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class InetAddr {

    @JsonProperty("ippid")
    private long poolId;

    @JsonProperty("ip")
    private String address;

    @JsonProperty("ipv6")
    @JsonDeserialize(using = NumericBooleanDeserializer.class)
    private boolean v6;

    @JsonProperty("vpsid")
    private long vpsId;

}
