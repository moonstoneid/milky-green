package com.moonstoneid.web3login.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.Instant;

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
