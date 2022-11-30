package com.moonstoneid.milkygreen;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "milkygreen")
@Getter
@Setter
public class AppProperties {

    @Getter
    @Setter
    public static class Service {

        private String protocol;
        private String domain;

        public void setProtocol(String protocol) {
            if (protocol == null) {
                throw new Error("Service protocol must be configured!");
            }
            if (!protocol.equals("http") && !protocol.equals("https")) {
                throw new Error("Service protocol is invalid! Only 'http' or 'https' is allowed!'");
            }
            this.protocol = protocol;
        }

        public void setDomain(String domain) {
            if (domain == null || domain.isEmpty()) {
                throw new Error("Service domain must be configured!");
            }
            this.domain = domain;
        }

    }

    @Getter
    @Setter
    public static class Api {
        private String key;
    }

    @Getter
    @Setter
    public static class Eth {
        private EthApi api;
    }

    @Getter
    @Setter
    public static class EthApi {
        private String url;
    }

    private Service service;
    private Api api;
    private Eth eth;

}
