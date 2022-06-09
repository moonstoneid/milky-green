package org.mn.web3login.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class HomeController {

    @GetMapping(value = "/")
    public String home(Principal principal, Model model) {
        return "home";
    }

}
