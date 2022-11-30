package com.moonstoneid.milkygreen.view.model;

import lombok.Data;

@Data
public class AuthorizationVM {

    private String id;
    private String clientName;
    private String issuedAt;
    private String expireAt;

}
