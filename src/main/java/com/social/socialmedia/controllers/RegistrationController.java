package com.social.socialmedia.controllers;

import com.social.socialmedia.domain.Role;
import com.social.socialmedia.domain.User;
import com.social.socialmedia.repos.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Collections;

@Controller
public class RegistrationController {
    private UserRepo userRepo;

    @Autowired
    public void setUserRepo(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @GetMapping("/registration")
    public String registration(){

        return "registration";
    }

    @PostMapping("/registration")
    public String create(User user, Model model){
        User newUser = userRepo.findByUsername(user.getUsername());

        if(newUser != null){
            model.addAttribute("message", "User exists");
            return "registration";
        };
            user.setActive(true);
            user.setRoles(Collections.singleton(Role.USER));
            userRepo.save(user);

        return "redirect:/login";
    }
}
