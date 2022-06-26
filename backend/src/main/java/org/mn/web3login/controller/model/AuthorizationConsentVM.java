package org.mn.web3login.controller.model;

import lombok.Data;

@Data
public class AuthorizationConsentVM {

    private String clientId;
    private String clientName;
    private String scopes;

}
