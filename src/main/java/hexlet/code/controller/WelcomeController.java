package hexlet.code.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public final class WelcomeController {

    @RequestMapping("/welcome")
    public String welcome() {
        return "Welcome to Spring";
    }
}
