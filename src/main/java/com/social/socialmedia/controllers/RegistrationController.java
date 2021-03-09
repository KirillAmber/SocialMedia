package com.social.socialmedia.controllers;

import com.social.socialmedia.domain.User;
import com.social.socialmedia.domain.dto.CaptchaResponseDto;
import com.social.socialmedia.repos.UserRepo;
import com.social.socialmedia.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.util.Collections;
import java.util.Map;

@Controller
public class RegistrationController {
    private static String CAPTCHA_URL="https://www.google.com/recaptcha/api/siteverify?secret=%s&response=%s";

    private UserService userService;
    private RestTemplate restTemplate;
    @Value("${recaptcha.secret}")
    private String secret;


    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }

    @GetMapping("/registration")
    public String registration(){

        return "registration";
    }

    @PostMapping("/registration")
    public String create(@RequestParam("password2") String passwordConfirm,
                         @RequestParam("g-recaptcha-response") String captchaResponse,
                         @Valid User user, BindingResult bindingResult,
                         Model model){

        String endpoint = String.format(CAPTCHA_URL, secret, captchaResponse);
        CaptchaResponseDto responseDto = restTemplate.postForObject(endpoint, Collections.emptyList(), CaptchaResponseDto.class);

        if (responseDto != null && !responseDto.isSuccess()) {
            model.addAttribute("captchaError", "Fill captcha");

        }


        boolean isConfirmEmpty = StringUtils.isEmpty(passwordConfirm);
        if(isConfirmEmpty){
            model.addAttribute("password2Error", "Password cannot be empty");
        }

        if(user.getPassword()!=null && !user.getPassword().equals(passwordConfirm)){
            model.addAttribute("passwordError", "Passwords aren't equal!");
        }
        if(isConfirmEmpty || bindingResult.hasErrors() || !responseDto.isSuccess()){
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
            model.addAttribute("messageType", "alert alert-success");
        } else{
            model.addAttribute("message", "Activation code isn't found!");
            model.addAttribute("messageType", "alert alert-danger");
        }

        return "login";
    }
}
