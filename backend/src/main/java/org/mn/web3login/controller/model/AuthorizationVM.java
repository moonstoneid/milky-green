package org.mn.web3login.controller.model;

import lombok.Data;

@Data
public class AuthorizationVM {

    private String id;
    private String clientName;
    private String issuedAt;
    private String expireAt;

}
