package com.moonstoneid.web3login.config;

import com.moonstoneid.web3login.AppProperties;
import com.moonstoneid.web3login.ens.resolver.EnsRecordsResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

@Configuration
@EnableAsync
public class Web3Config {

    private final AppProperties appProperties;

    public Web3Config(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    @Bean
    public EnsRecordsResolver recordsResolver() {
        Web3j web3j = Web3j.build(new HttpService(appProperties.eth.provider.url));
        return new EnsRecordsResolver(web3j);
    }

}
