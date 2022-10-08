package com.moonstoneid.web3login.ens.job;

import java.util.List;

import com.moonstoneid.web3login.config.Web3Config;
import com.moonstoneid.web3login.ens.model.TextRecords;
import com.moonstoneid.web3login.ens.resolver.EnsRecordsResolver;
import com.moonstoneid.web3login.model.User;
import com.moonstoneid.web3login.model.UserEns;
import com.moonstoneid.web3login.service.UserService;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class EnsUpdater {

    private final UserService userService;
    private final EnsRecordsResolver ensResolver;

    public EnsUpdater(UserService userService, Web3Config web3Config) {
        this.userService = userService;
        this.ensResolver = web3Config.recordsResolver();
    }

    @Async
    public void updateUserAsync(User user) {
        updateUser(user);
    }

    @Scheduled(fixedDelay = 600000, initialDelay = 2000)
    protected void refresh() {
        List<User> users = userService.getAll();
        for (User user : users) {
            updateUser(user);
        }
    }

    private void updateUser(User user) {
        // trying to resolve address to ens name
        String username = user.getUsername();
        try {
            String ensDomain = ensResolver.reverseResolve(username);
            if (ensDomain != null) {
                setEnsInfo(user, ensDomain);
            } else {
                removeEnsInfo(user);
            }
        } catch (Exception e) {
            return;
        }

        userService.save(user);
    }

    private void setEnsInfo (User user, String ensDomain) {
        UserEns ens = user.getEns();
        if (ens == null) {
            ens = new UserEns();
            ens.setUsername(user.getUsername());
        }
        ens.setEnsDomain(ensDomain);
        ens.setEnsName(ensResolver.getTextRecord(ensDomain, TextRecords.NAME));
        ens.setEnsUrl(ensResolver.getTextRecord(ensDomain, TextRecords.URL));
        ens.setEnsEmail(ensResolver.getTextRecord(ensDomain, TextRecords.EMAIL));
        user.setEns(ens);
    }

    private void removeEnsInfo(User user) {
        user.getEns().setEnsDomain(null);
        user.getEns().setEnsName(null);
        user.getEns().setEnsUrl(null);
        user.getEns().setEnsEmail(null);
        user.getEns().setUsername(null);
    }

}
