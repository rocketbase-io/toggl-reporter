package io.rocketbase.toggl.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class LoginController {

    @Autowired
    ServerProperties serverProperties;

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ModelAndView login(@RequestParam(value = "error", required = false) String error,
                              @RequestParam(value = "logged-out", required = false) String loggedOut) {
        ModelAndView modelAndView = new ModelAndView();
        if (error != null) {
            modelAndView.addObject("error", true);
        }
        if (loggedOut != null) {
            modelAndView.addObject("loggedOut", true);
        }
        modelAndView.addObject("contextPath", serverProperties.getContextPath());
        modelAndView.setViewName("login");
        return modelAndView;
    }
}