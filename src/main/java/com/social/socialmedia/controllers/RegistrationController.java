package com.social.socialmedia.controllers;

import com.social.socialmedia.domain.User;
import com.social.socialmedia.repos.UserRepo;
import com.social.socialmedia.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.Map;

@Controller
public class RegistrationController {
    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/registration")
    public String registration(){

        return "registration";
    }

    @PostMapping("/registration")
    public String create(@Valid User user, BindingResult bindingResult, Model model){
        if(user.getPassword()!=null && !user.getPassword().equals(user.getPassword2())){
            model.addAttribute("passwordError", "Passwords aren't equal!");
        }
        if(bindingResult.hasErrors()){
            Map<String, String> errors = UtilsController.getErrors(bindingResult);

            model.mergeAttributes(errors);
            System.out.println(bindingResult);
            return "registration";
        }
        if(!userService.create(user)){
            model.addAttribute("usernameError", "User exists");
            return "registration";
        }
        return "redirect:/login";
    }

    @GetMapping("/activate/{code}")
    public String activate(@PathVariable String code, Model model){
        boolean isActivated = userService.activateUser(code);

        if(isActivated){
            model.addAttribute("message", "User successfully activated");
        } else{
            model.addAttribute("message", "Activation code isn't found!");
        }

        return "login";
    }
}
