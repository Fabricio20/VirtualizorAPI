package net.notfab.virtualizor.entities;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class VirtualServer {

    private String vpsid;
    private String uuid;
    private String hostname;
    private String os_name; // ubuntu-18.04
    private String space; // HDD in GB
    private String ram;
    private String swap;
    private String cores;
    private String network_speed;
    private String upload_speed;
    private String vncport;
    private String suspended; // 0 or 1
    private String rescue; // 0 or 1
    private String band_suspend; // 0 or 1

    private String admin_managed; // 0 or 1
    private String mac;
    private String server_name; // Hypervisor
    private String email; // Owner
    private Map<String, String> ips;

}