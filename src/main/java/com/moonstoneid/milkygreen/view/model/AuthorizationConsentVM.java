package com.moonstoneid.milkygreen.view.model;

import lombok.Data;

@Data
public class AuthorizationConsentVM {

    private String clientId;
    private String clientName;
    private String scopes;

}
