package com.moonstoneid.web3login.view.controller;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ErrorController implements org.springframework.boot.web.servlet.error.ErrorController {

    private static final String PATH_ERROR = "/error";

    private static final String VIEW_ERROR = "error";

    private static final String ATTRIBUTE_ERROR_STATUS = "errorStatus";
    private static final String ATTRIBUTE_ERROR_MESSAGE = "errorMessage";

    @RequestMapping(value = PATH_ERROR)
    public String error(HttpServletRequest request, Model model) {
        Integer status = (Integer) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        String message = (String) request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        model.addAttribute(ATTRIBUTE_ERROR_STATUS, status);
        model.addAttribute(ATTRIBUTE_ERROR_MESSAGE, message);
        return VIEW_ERROR;
    }

}
