package net.notfab.virtualizor.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum VirtType {

    @JsonProperty("kvm")
    KVM,
    @JsonProperty("openvz")
    OpenVZ,
    @JsonProperty("xen")
    Xen,
    @JsonProperty("xenhvm")
    XenHVM,
    @JsonProperty("xcp")
    XCP,
    @JsonProperty("xcphvm")
    XcpHVM,
    @JsonProperty("lxc")
    LXC,
    @JsonProperty("vzo")
    VZO,
    @JsonProperty("vzk")
    VZK,
    @JsonProperty("proxo")
    ProxO,
    @JsonProperty("proxk")
    ProxK,
    @JsonProperty("proxl")
    ProxL;

}
