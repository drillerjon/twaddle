package com.innoq.lab.twaddle.controller;

import com.innoq.lab.twaddle.model.Message;
import com.innoq.lab.twaddle.model.User;
import com.innoq.lab.twaddle.repository.MessageRepository;
import com.innoq.lab.twaddle.repository.UserRepository;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.fromMappingName;

@Controller
public class IndexController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageRepository messageRepository;

    @GetMapping(name = "index", value = "/twaddle")
    public String index(@RequestParam(value = "name", required = false, defaultValue = "World") String name, Model model) {
        model.addAttribute("name", name);
        return "index";
    }

    @GetMapping(name = "user-form", value = "/twaddle/user/add")
    public String addUser() {
        return "userForm";
    }

    @GetMapping(name = "user-login", value = "/twaddle/login")
    public String loginUser() {
        return "userlogin";
    }

    @GetMapping(name = "chat", value = "/twaddle/messages")
    public String chat(Model model){
        Iterable<Message> messages = messageRepository.findAll();

        if (messages.iterator().hasNext()) {
            List<Message> messageList = new ArrayList<>();
            messages.iterator().forEachRemaining(messageList::add);
            model.addAttribute("messages", messageList);
        }
            return "chat";
    }


    @GetMapping(name = "user-detail", value = "/twaddle/user/{id}")
    public String showUser(@PathVariable("id") Long id, Model model) {
        User user = userRepository.findOne(id);

        if (user != null) {
            model.addAttribute("user", user);
        }
        return "userDetails";
    }

    @GetMapping(name = "user-list", value = "/twaddle/user")
    public String listUsers(Model model) {
        Iterable<User> users = userRepository.findAll();

        if (users.iterator().hasNext()) {
            List<User> userList = new ArrayList<>();
            users.iterator().forEachRemaining(userList::add);
            model.addAttribute("users", userList);
        }
        return "userList";
    }

    @PostMapping("/twaddle/user/create")
    public ModelAndView createUser(@ModelAttribute UserForm userForm, RedirectAttributes redirectAttrs) {
        System.out.println("Username: " + userForm.getUserName());
        System.out.println("Passwd:   " + userForm.getPasswd());

        User user = userRepository.findByUserName(userForm.getUserName());

        if (user == null || !userForm.getPasswd().equals(userForm.getPasswd2()) || userForm.getPasswd().equals("")){
            redirectAttrs.addFlashAttribute("message", "Something went wrong!");
            return new ModelAndView(new RedirectView(fromMappingName("user-form").build()));
        }
        else {
            userRepository.save(new User(userForm.getUserName(), userForm.getPasswd()));
            return new ModelAndView(new RedirectView(fromMappingName("user-list").build()));
        }
        /*
            redirectAttrs.addFlashAttribute("message", "Something went wrong!");
            return new ModelAndView(new RedirectView(fromMappingName("user-form").build()));
        */
    }
    @PostMapping("/twaddle/user/login")
    public ModelAndView loginuser(@ModelAttribute LoginForm loginForm, RedirectAttributes redirectAttrs) {
        User user = userRepository.findByUserName(loginForm.getUserName());
        if (user == null || !user.getPasswd().equals(loginForm.getPassword())){
            redirectAttrs.addFlashAttribute("message", "Wrong Username or Password!");
            return new ModelAndView(new RedirectView(fromMappingName("user-login").build()));
        }



        else{

            return new ModelAndView(new RedirectView(fromMappingName("chat").build()));
        }
    }
    @PostMapping("/twaddle/chat/addmessage")
    public ModelAndView addmessage(@ModelAttribute ChatForm chatform) {
        messageRepository.save(new Message(chatform.getMessage()));
        return new ModelAndView(new RedirectView(fromMappingName("chat").build()));


    }









    @GetMapping(name = "avatar-form", value = "twaddle/user/{id}/avatar/upload")
    public String addAvatar(@PathVariable("id") Long id, Model model) {
        User user = userRepository.findOne(id);

        if (user != null) {
            model.addAttribute("user", user);
        }
        return "avatarForm";
    }

    @PostMapping("/twaddle/user/{id}/avatar")
    public ModelAndView handleFileUpload(
            @RequestParam("avatar") MultipartFile file, @PathVariable("id") Long id, RedirectAttributes redirectAttributes) {

        try {
            User user = userRepository.findOne(id);
            if (user != null){
                user.setAvatar(file.getBytes());
                userRepository.save(user);

                redirectAttributes.addFlashAttribute("message", "You successfully uploaded " + file.getOriginalFilename() + "!");
                return new ModelAndView(new RedirectView(fromMappingName("user-detail").arg(0, user.getId()).build()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        redirectAttributes.addFlashAttribute("message", "Something went wrong");
        return new ModelAndView(new RedirectView(fromMappingName("index").build()));
    }

    @GetMapping("/twaddle/user/{id}/avatar")
    @ResponseBody
    public ResponseEntity<InputStreamResource> downloadUserAvatarImage(@PathVariable("id") Long id) {
        User userAvatar = userRepository.findOne(id);

        if (userAvatar != null) {
            return ResponseEntity.ok()
                    .contentLength(userAvatar.getAvatar().length)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(new InputStreamResource(new ByteArrayInputStream(userAvatar.getAvatar())));
        } else {
            return ResponseEntity.ok(null);
        }
    }

    public static class LoginForm {
        private String userName;
        private String password;

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    public static class UserForm {

        private String userName;

        private String passwd;

        private String passwd2;

        public String getPasswd2() {
            return passwd2;
        }

        public void setPasswd2(String passwd2) {
            this.passwd2 = passwd2;
        }

        public String getPasswd() {
            return passwd;
        }

        public void setPasswd(String passwd) {
            this.passwd = passwd;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }
    }
    public static class ChatForm {
        private String message;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}