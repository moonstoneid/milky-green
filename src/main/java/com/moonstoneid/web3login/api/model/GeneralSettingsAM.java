package com.moonstoneid.web3login.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "GeneralSettings")
public class GeneralSettingsAM {

    private Boolean isAllowAutoImport;

}
