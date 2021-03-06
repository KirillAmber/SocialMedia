package com.social.socialmedia.controllers;
import com.social.socialmedia.domain.Message;
import com.social.socialmedia.domain.User;
import com.social.socialmedia.repos.MessageRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class MainController {

    private MessageRepo messageRepo;

    @Autowired
    public void setMessageRepo(MessageRepo messageRepo) {
        this.messageRepo = messageRepo;
    }

    @GetMapping("/")
    public String greeting(Model model) {
        return "greeting";
    }

    @GetMapping("/main")
    public String main(@RequestParam(required = false, defaultValue = "") String filterTag, Model model){
        Iterable<Message> messages = messageRepo.findAll();

        if (filterTag != null && !filterTag.isEmpty()){
            messages = messageRepo.findByTag(filterTag); // return list
        } else {
            messages = messageRepo.findAll(); // return iterable
        }
        model.addAttribute("messages", messages);
        model.addAttribute("filter", filterTag);
        return "main";
    }

    @PostMapping("/main")
    public String create(
            @AuthenticationPrincipal User user,
            @RequestParam String text,
            @RequestParam String tag, Model model){
        Message message = new Message(text, tag, user);
        messageRepo.save(message);

        //Need to make new method for this
        Iterable<Message> messages = messageRepo.findAll();
        model.addAttribute("messages", messages);

        return "main";
    }

}
