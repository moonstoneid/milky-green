package com.moonstoneid.milkygreen.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "KeyPair")
public class KeyPairAM {

    private String privateKey;
    private String publicKey;

}
