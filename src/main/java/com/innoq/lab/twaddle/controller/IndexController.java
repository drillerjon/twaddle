package com.innoq.lab.twaddle.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import java.util.*;
import java.util.ArrayList;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.fromMappingName;

@Controller
public class IndexController
{

   private static ArrayList<String> userList = new ArrayList<String>();

    @GetMapping(name = "index", value = "/twaddle")
    public String index(@RequestParam(value = "name", required = false, defaultValue = "World") String name, Model model) {
        model.addAttribute("name", name);
        return "index";
    }

    @GetMapping("/twaddle/user/add")
    public String addUser() {

        return "usercreate";
    }

    @GetMapping("/twaddle/user/login")
    public String loginUser() {

        return "userlogin";
    }


    @PostMapping("/twaddle/user/create")
    public ModelAndView createUser(@ModelAttribute UserForm userForm) {
        String UserName = userForm.getUserName();
        String firstPsw = userForm.getPasswd();
        String secondPsw = userForm.getPasswdwdh();

        if(userList.contains(UserName)){
            System.out.println("Dein gewünschter Username ist bereits vergeben");
        }
        else if(!firstPsw.equals(secondPsw)) {
            System.out.println("Das Passwort wurde nicht korrekt wiederholt");
        }
        else{
            userList.add (userForm.getUserName());
            userList.add (userForm.getPasswd());;
        }

        System.out.println(userList);
        return new ModelAndView(new RedirectView(fromMappingName("index").arg(0, userForm.getUserName()).build()));
    }

    @PostMapping("/twaddle/user/login")
    public ModelAndView login(@ModelAttribute LoginForm loginForm) {
        int pos_pass;
        String pass = loginForm.getLoginPass();
        int uname_pos = userList.indexOf(loginForm.getLoginName());
        pos_pass = ++uname_pos;
        String password = userList.get(pos_pass);


            if (password.equals(pass)){
                System.out.println("Angemeldet");



            }
            else {
                System.out.println("Falsches Passwort oder falscher");
            }


        return new ModelAndView(new RedirectView(fromMappingName("index").arg(0, loginForm.getLoginName()).build()));
    }
    public static class LoginForm{
        private String loginName;

        public String getLoginName() {
            return loginName;
        }

        public void setLoginName(String loginName) {
            this.loginName = loginName;
        }

        public String getLoginPass() {
            return loginPass;
        }

        public void setLoginPass(String loginPass) {
            this.loginPass = loginPass;
        }

        private String loginPass;
    }

    public static class UserForm {
        private String userName;
        private String passwd;
        private String passwdwdh;

        public String getPasswdwdh() {
            return passwdwdh;
        }

        public void setPasswdwdh(String passwdwdh) {
            this.passwdwdh = passwdwdh;
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


}