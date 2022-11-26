package com.moonstoneid.web3login.config;

import com.moonstoneid.web3login.AppProperties;
import com.moonstoneid.web3login.eth.AsyncEnsUpdater;
import com.moonstoneid.web3login.eth.EnsRecordsResolver;
import com.moonstoneid.web3login.eth.EnsUpdateJob;
import com.moonstoneid.web3login.eth.EnsUpdater;
import com.moonstoneid.web3login.eth.NoOpEnsUpdater;
import com.moonstoneid.web3login.eth.SiweMessageCreator;
import com.moonstoneid.web3login.eth.SiweMessageVerifier;
import com.moonstoneid.web3login.eth.OnChainSiweMessageVerifier;
import com.moonstoneid.web3login.eth.OffChainSiweMessageVerifier;
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
public class EthConfig {

    private final AppProperties appProperties;
    private final UserService userService;

    public EthConfig(AppProperties appProperties, UserService userService) {
        this.appProperties = appProperties;
        this.userService = userService;
    }

    @Bean
    public SiweMessageCreator siweMessageCreator() {
        String serviceProtocol = appProperties.getService().getProtocol();
        String serviceDomain = appProperties.getService().getDomain();
        return new SiweMessageCreator(serviceProtocol, serviceDomain);
    }

    @Bean
    public SiweMessageVerifier siweMessageVerifier() {
        String serviceDomain = appProperties.getService().getDomain();
        Web3j web3j = web3j();
        if (web3j == null) {
            log.info("Verification of Siwe messages signed by contracts is disabled.");
            return new OffChainSiweMessageVerifier(serviceDomain);
        }
        return new OnChainSiweMessageVerifier(serviceDomain, web3j);
    }

    @Bean
    public EnsUpdateJob ensUpdateJob() {
        return new EnsUpdateJob(ensUpdater());
    }

    @Bean
    public EnsUpdater ensUpdater() {
        Web3j web3j = web3j();
        if (web3j == null) {
            log.info("Retrieval of ENS information is disabled.");
            return new NoOpEnsUpdater();
        }
        return new AsyncEnsUpdater(userService, new EnsRecordsResolver(web3j));
    }

    @Bean
    public Web3j web3j() {
        String ethApiUrl = appProperties.getEth().getApi().getUrl();
        if (ethApiUrl == null || ethApiUrl.isEmpty()) {
            log.info("No Ethereum API URL was configured.");
            return null;
        }
        return Web3j.build(new HttpService(ethApiUrl));
    }

}
