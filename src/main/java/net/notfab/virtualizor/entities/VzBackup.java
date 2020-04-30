package net.notfab.virtualizor.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class VzBackup {

    private long server;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date date;

    @JsonProperty("abs_path")
    private String path;

}
