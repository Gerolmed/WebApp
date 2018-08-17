package edu.hm.cs.projektstudium.findlunch.webapp.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Represents a client platform
 */
@Entity
@Table(name="platform")
@ApiModel(
        description = "Represents a client platform"
)
@Getter
@Setter
public class Platform {

    /**
     * The id.
     */
    @ApiModelProperty(notes = "ID")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    /**
     * The platform name
     */
    @ApiModelProperty(notes = "Platform-name")
    @Column(name = "name")
    @NotNull
    private String platformName;

    /**
     * The amount of connections
     */
    @ApiModelProperty(notes = "ConnectionCount")
    @Column(name = "count")
    private int count;

    public Platform() {
        super();
        count = 0;
    }

    public Platform(String platformName) {
        super();

        this.platformName = platformName;
        this.count = 0;
    }
}
