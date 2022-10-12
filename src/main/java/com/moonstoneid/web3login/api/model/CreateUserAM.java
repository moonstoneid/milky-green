package com.moonstoneid.web3login.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "CreateUser")
public class CreateUserAM {

    private String userName;
    private Boolean isEnabled;

}
