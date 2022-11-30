package com.moonstoneid.milkygreen.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "User")
public class UserAM {

    private String userName;
    private Boolean isEnabled;

}
