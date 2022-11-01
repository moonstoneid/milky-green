package com.moonstoneid.web3login.ens;

import java.util.List;

import com.moonstoneid.web3login.ens.model.TextRecords;
import com.moonstoneid.web3login.model.User;
import com.moonstoneid.web3login.model.UserEns;
import com.moonstoneid.web3login.service.UserService;
import org.springframework.scheduling.annotation.Async;

public class AsyncEnsUpdater implements EnsUpdater {

    private final UserService userService;
    private final EnsRecordsResolver ensResolver;

    public AsyncEnsUpdater(UserService userService, EnsRecordsResolver ensResolver) {
        this.userService = userService;
        this.ensResolver = ensResolver;
    }

    @Async
    @Override
    public void updateUser(String username) {
        User user = userService.findByUsername(username);
        if (user != null) {
            updateUser(user);
        }
    }

    @Async
    @Override
    public void updateUsers() {
        List<User> users = userService.getAll();
        for (User user : users) {
            updateUser(user);
        }
    }

    private void updateUser(User user) {
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

    private void setEnsInfo(User user, String ensDomain) {
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
