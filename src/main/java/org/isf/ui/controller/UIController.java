package org.isf.ui.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class UIController {

    @RequestMapping(value = {"/oh20/login"})
    public String loginPage() {
        return "index.html";
    }

}