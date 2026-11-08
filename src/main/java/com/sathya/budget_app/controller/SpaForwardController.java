package com.sathya.budget_app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * The React app uses browser-history routing (/login, /transactions, /budgets ...).
 * Reloading or deep-linking to one of those paths hits Spring Boot, which has no such
 * resource, so forward them to index.html and let React Router take over.
 * Paths containing a dot (/favicon.svg, /assets/x.js) are real files and are not matched.
 */
@Controller
public class SpaForwardController {

    @GetMapping({"/{p1:[^\\.]*}", "/{p1:[^\\.]*}/{p2:[^\\.]*}"})
    public String forwardToIndex() {
        return "forward:/index.html";
    }
}
