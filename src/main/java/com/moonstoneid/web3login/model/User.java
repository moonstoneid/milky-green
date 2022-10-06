package com.moonstoneid.web3login.model;

import java.util.List;
import javax.persistence.*;

import lombok.Data;

@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @Column(name = "username", length = 50)
    private String username;

    @Column(name = "enabled")
    private boolean enabled;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "authorities", joinColumns = @JoinColumn(name = "username"))
    @Column(name = "authority", length = 100)
    private List<String> authorities;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "username")
    private UserEns ens;

}
