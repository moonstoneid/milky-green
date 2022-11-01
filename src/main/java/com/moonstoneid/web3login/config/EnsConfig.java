package com.moonstoneid.web3login.config;

import com.moonstoneid.web3login.AppProperties;
import com.moonstoneid.web3login.ens.EnsUpdateJob;
import com.moonstoneid.web3login.ens.AsyncEnsUpdater;
import com.moonstoneid.web3login.ens.EnsRecordsResolver;
import com.moonstoneid.web3login.ens.EnsUpdater;
import com.moonstoneid.web3login.ens.NoOpEnsUpdater;
import com.moonstoneid.web3login.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

@Configuration
@EnableAsync
@EnableScheduling
@Slf4j
public class EnsConfig {

    private final AppProperties appProperties;
    private final UserService userService;

    public EnsConfig(AppProperties appProperties, UserService userService) {
        this.appProperties = appProperties;
        this.userService = userService;
    }

    @Bean
    public EnsUpdateJob ensUpdateJob() {
        return new EnsUpdateJob(ensUpdater());
    }

    @Bean
    public EnsUpdater ensUpdater() {
        String ethApiUrl = appProperties.eth.api.url;
        if (ethApiUrl == null || ethApiUrl.isEmpty()) {
            log.info("No Ethereum API URL was configured. Retrieval of ENS information is disabled.");
            return new NoOpEnsUpdater();
        }

        return new AsyncEnsUpdater(userService, ensRecordsResolver(ethApiUrl));
    }

    private EnsRecordsResolver ensRecordsResolver(String ethApiUrl) {
        return new EnsRecordsResolver(web3j(ethApiUrl));
    }

    private Web3j web3j(String ethApiUrl) {
        return Web3j.build(new HttpService(ethApiUrl));
    }

}
