package com.moonstoneid.web3login;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "web3login")
@Getter
@Setter
public class AppProperties {

    @Getter
    @Setter
    public static class Api {
        public String key;
    }

    @Getter
    @Setter
    public static class Eth {
        public EthApi api;
    }

    @Getter
    @Setter
    public static class EthApi {
        public String url;
    }

    public Api api;
    public Eth eth;

}
