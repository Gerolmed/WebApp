package edu.hm.cs.projektstudium.findlunch.webapp.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Represents a client-sided version
 */
@Entity
@Table(name="version")
@ApiModel(
        description = "Represents a client-sided version"
)
@Getter
@Setter
public class Version {

    /** The id. */
    @ApiModelProperty(notes = "ID")
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private int id;

    /** The version name*/
    @ApiModelProperty(notes = "Version-name")
    @Column(name="name")
    @NotNull
    private String versionName;

    /** The amount of connections*/
    @ApiModelProperty(notes = "ConnectionCount")
    @Column(name="count")
    private int count;

    public Version() {
        super();
        count = 0;
    }

    public Version(String versionName) {
        super();

        this.versionName = versionName;
        this.count = 0;
    }

}
