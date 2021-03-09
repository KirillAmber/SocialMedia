package com.social.socialmedia.controllers;
import com.social.socialmedia.domain.Message;
import com.social.socialmedia.domain.User;
import com.social.socialmedia.repos.MessageRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
public class MainController {

    private MessageRepo messageRepo;

    @Value("${upload.path}")
    private String uploadPath;

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
            @Valid Message message,
            BindingResult bindingResult, // must be before model
            Model model,
            @RequestParam("file") MultipartFile file
            ){
        message.setAuthor(user);

        if(bindingResult.hasErrors()){
            Map<String, String> errorsMap = UtilsController.getErrors(bindingResult);
            model.mergeAttributes(errorsMap);
            model.addAttribute("message", message);
        } else {

        if(file != null && !file.getOriginalFilename().isEmpty()){
            File uploadDir = new File(uploadPath);
            if(!uploadDir.exists()){
                uploadDir.mkdir();
            }

            String uuidFile = UUID.randomUUID().toString(); // anti-collision
            String resultFileName = uuidFile + "." + file.getOriginalFilename();

            try {
                file.transferTo(new File(uploadPath + "/" + resultFileName));
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error related with transferring file");
            }

            message.setFilename(resultFileName);
        }
        model.addAttribute("message", null);
        messageRepo.save(message);
        }

        //Need to make new method for this
        Iterable<Message> messages = messageRepo.findAll();
        model.addAttribute("messages", messages);

        return "main";
    }



}
