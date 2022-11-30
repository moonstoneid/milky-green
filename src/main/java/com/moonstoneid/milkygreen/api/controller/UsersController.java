package com.moonstoneid.milkygreen.api.controller;

import java.util.ArrayList;
import java.util.List;

import com.moonstoneid.milkygreen.api.doc.UsersApi;
import com.moonstoneid.milkygreen.api.model.UserAM;
import com.moonstoneid.milkygreen.api.model.CreateUserAM;
import com.moonstoneid.milkygreen.model.User;
import com.moonstoneid.milkygreen.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api")
public class UsersController implements UsersApi {

    private final UserService userService;

    public UsersController(UserService userService) {
        this.userService = userService;
    }

    @Override
    @GetMapping(value = "/users", produces = { "application/json" })
    public @ResponseBody List<UserAM> getUsers() {
        List<User> users = userService.getAll();
        return toApiModel(users);
    }

    @Override
    @GetMapping(value = "/users/{username}", produces = { "application/json" })
    public @ResponseBody UserAM getUser(@PathVariable("username") String username) {
        User user = userService.findByUsername(username);
        checkUserWasFound(username, user);
        return toApiModel(user);
    }

    @Override
    @PostMapping(value = "/users", produces = { "application/json" })
    public @ResponseBody UserAM createUser(@RequestBody CreateUserAM apiCreateUser) {
        validateCreateUserRequest(apiCreateUser);

        checkUsernameIsNotTaken(apiCreateUser.getUserName());

        User user = toModel(apiCreateUser);
        userService.save(user);
        return toApiModel(user);
    }

    @Override
    @DeleteMapping(value = "/users/{username}", produces = { "application/json" })
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable("username") String username) {
        User user = userService.findByUsername(username);
        checkUserWasFound(username, user);
        userService.delete(user);
    }

    @Override
    @PutMapping(value = "/users/{username}/enable", produces = { "application/json" })
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void enableUser(@PathVariable("username") String username) {
        setUserEnabled(username, true);
    }

    @Override
    @DeleteMapping(value = "/users/{username}/enable", produces = { "application/json" })
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void disableUser(@PathVariable("username") String username) {
        setUserEnabled(username, false);
    }

    private void setUserEnabled(String username, boolean enabled) {
        User user = userService.findByUsername(username);
        checkUserWasFound(username, user);
        user.setEnabled(enabled);
        userService.save(user);
    }

    private void checkUsernameIsNotTaken(String username) {
        User user = userService.findByUsername(username);
        if (user != null) {
            throw new NotFoundException(String.format("A user with the username '%s' does " +
                    "already exist.", username));
        }
    }

    private void validateCreateUserRequest(CreateUserAM apiCreateUser) {
        validateUsername(apiCreateUser.getUserName());
    }

    private void validateUsername(String username) {
        if (username == null || username.isEmpty()) {
            throw new ValidationException("username cannot be null or empty.");
        }
        if (!username.matches("^0x[0-9a-fA-F]{40}$")) {
            throw new ValidationException("username is not a valid ethereum address.");
        }
    }

    private static List<UserAM> toApiModel(List<User> users) {
        List<UserAM> apiUsers = new ArrayList<>();
        for (User user : users) {
            apiUsers.add(toApiModel(user));
        }
        return apiUsers;
    }

    private static UserAM toApiModel(User dbUser) {
        UserAM apiUser = new UserAM();
        apiUser.setUserName(dbUser.getUsername());
        apiUser.setIsEnabled(dbUser.isEnabled());
        return apiUser;
    }

    private static User toModel(CreateUserAM apiCreateUser) {
        User user = new User();
        user.setUsername(apiCreateUser.getUserName());
        user.setEnabled(true);
        return user;
    }

    private void checkUserWasFound(String username, User client) {
        if (client == null) {
            throw new NotFoundException(String.format("A user with the username '%s' was not found.",
                    username));
        }
    }

}
