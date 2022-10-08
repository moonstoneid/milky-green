package com.moonstoneid.web3login.model;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "user_ens")
public class UserEns {

    @Id
    @Column(name = "username", length = 100)
    private String username;

    @Column(name = "ens_domain", length = 255)
    private String ensDomain;

    @Column(name = "ens_email", length = 255)
    private String ensEmail;

    @Column(name = "ens_url", length = 1000)
    private String ensUrl;

    @Column(name = "ens_name", length = 255)
    private String ensName;

}
