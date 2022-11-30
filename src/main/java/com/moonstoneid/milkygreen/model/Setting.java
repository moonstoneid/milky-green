package com.moonstoneid.milkygreen.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "settings")
public class Setting {

    @Id
    @Column(name = "setting_name", length = 50)
    private String name;

    @Column(name = "setting_value")
    private String value;

}
