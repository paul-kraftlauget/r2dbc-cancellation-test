package no.dnb.r2dbc.test.demo.model;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Data
public class SchemaVersion {
    @Id
    private Long installedRank;
    private String version;
    private String description;
    private String type;
    private String script;
    private int checksum;
    private int executionTime;
    private String installedBy;
    private LocalDateTime installedOn;
    private Boolean success;
}
