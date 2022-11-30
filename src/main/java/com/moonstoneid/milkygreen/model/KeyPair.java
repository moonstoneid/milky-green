package com.moonstoneid.milkygreen.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "jwk_keypair")
public class KeyPair {

    @Id
    @Column(name = "id", length = 100)
    private String id;
    @Column(name = "private_key", length = 2000)
    private String privateKey;
    @Column(name = "public_key", length = 2000)
    private String publicKey;

}
