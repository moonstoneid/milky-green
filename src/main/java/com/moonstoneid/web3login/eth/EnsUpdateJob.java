package com.moonstoneid.web3login.eth;

import org.springframework.scheduling.annotation.Scheduled;

public class EnsUpdateJob {

    private final EnsUpdater ensUpdater;

    public EnsUpdateJob(EnsUpdater ensUpdater) {
        this.ensUpdater = ensUpdater;
    }

    @Scheduled(fixedDelay = 600000, initialDelay = 2000)
    protected void run() {
        ensUpdater.updateUsers();
    }

}
