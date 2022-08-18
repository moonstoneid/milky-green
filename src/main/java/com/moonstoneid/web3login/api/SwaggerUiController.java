package com.moonstoneid.web3login.api;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SwaggerUiController {

    @GetMapping(value = "/api")
    public void api(HttpServletResponse response) throws IOException {
        response.sendRedirect("/api/swagger-ui/index.html");
    }

}
