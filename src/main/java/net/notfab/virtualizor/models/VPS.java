package net.notfab.virtualizor.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import net.notfab.virtualizor.advice.IpDeserializer;
import net.notfab.virtualizor.advice.NumericBooleanDeserializer;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class VPS {

    @JsonProperty("vpsid")
    private long id;

    @JsonProperty("vps_name")
    private String vid;

    @JsonProperty("uuid")
    private String uuid;

    @JsonProperty("virt")
    private VirtType virtType;

    @JsonProperty("hostname")
    private String hostname;

    @JsonProperty("boot")
    private String bootOrder;

    @JsonProperty("space")
    private double diskSize;

    @JsonProperty("ram")
    private long ram;

    @JsonProperty("swap")
    private long swap;

    @JsonProperty("cores")
    private double cpu;

    @JsonProperty("mac")
    private String mac;

    // -- State

    @JsonProperty("suspended")
    @JsonDeserialize(using = NumericBooleanDeserializer.class)
    private boolean suspended;

    @JsonProperty("rescue")
    @JsonDeserialize(using = NumericBooleanDeserializer.class)
    private boolean rescue;

    @JsonProperty("band_suspend")
    @JsonDeserialize(using = NumericBooleanDeserializer.class)
    private boolean networkSuspended;

    @JsonProperty("admin_managed")
    @JsonDeserialize(using = NumericBooleanDeserializer.class)
    private boolean adminManaged;

    // -- Plan

    @JsonProperty("plid")
    private long planId;

    // -- OS

    @JsonProperty("osid")
    private long osId;

    @JsonProperty("os_name")
    private String osName;

    // -- Hosting Server

    @JsonProperty("sid")
    private long hypervisorId;

    @JsonProperty("server_name")
    private String hypervisorName;

    // -- Owner Account

    @JsonProperty("email")
    private String ownerEmail;

    // -- Ips

    @JsonProperty("ips")
    @JsonDeserialize(using = IpDeserializer.class)
    private List<InetAddr> ips;

}
