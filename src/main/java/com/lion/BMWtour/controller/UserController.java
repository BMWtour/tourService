package com.lion.BMWtour.controller;

//import lombok.RequiredArgsConstructor;

import com.lion.BMWtour.entitiy.User;
import com.lion.BMWtour.service.UserService;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public String registerForm() {
        return "user/register";
    }

    @PostMapping("/register")
    public String registerProc(
        String user_id, String user_pw, String user_pw2, String user_nickname, String interest1,
        String interest2, String interest3, String user_imgUri) {
        if (userService.findByUid(user_id) == null && user_pw.equals(user_pw2)
            && user_pw.length() >= 4) {
            String hashedPwd = BCrypt.hashpw(user_pw, BCrypt.gensalt());
            User user = User.builder()
                .user_id(user_id)
                .user_pw(hashedPwd)
                .user_nickname(user_nickname)
                .interest1(interest1)
                .interest2(interest2)
                .interest3(interest3)
                // 이미지 추가 내용 필요
                .user_imgUri(user_imgUri)
                .regDate(LocalDate.now())
                .build();
            userService.registerUser(user);
        }
        return "redirect:/user/list";
    }

    @GetMapping("/list")
    public String list(HttpSession session, Model model) {
        List<User> userList = userService.getUsers();
        session.setAttribute("menu", "user");
        model.addAttribute("userList", userList);
        return "user/list";
    }

    @GetMapping("/delete/{user_id}")
    public String delete(@PathVariable String user_id) {
        userService.deleteUser(user_id);
        return "redirect:/user/list";
    }

    @GetMapping("/update/{user_id}")
    public String updateForm(@PathVariable String user_id, Model model) {
        User user = userService.findByUid(user_id);
        model.addAttribute("user", user);
        return "user/update";
    }

    @PostMapping("/update")
    public String updateProc(String user_id, String user_pw, String user_pw2, String user_nickname,
        String interest1, String interest2, String interest3, String user_imgUri) {
        User user = userService.findByUid(user_id);
        if (user_pw.equals(user_pw2) && user_pw.length() >= 4) {
            String hashedPwd = BCrypt.hashpw(user_pw, BCrypt.gensalt());
            user.setUser_pw(hashedPwd);
        }
        user.setUser_nickname(user_nickname);
        user.setInterest1(interest1);
        user.setInterest2(interest2);
        user.setInterest3(interest3);
        user.setUser_imgUri(user_imgUri);
        userService.updateUser(user);
        return "redirect:/user/list";
    }

    @GetMapping("/login")
    public String loginForm() {
        return "user/login";
    }

    @PostMapping("/login")
    public String loginProc(String user_id, String user_pw, HttpSession session, Model model) {
        String msg, url;
        int result = userService.login(user_id, user_pw);
        if (result == UserService.CORRECT_LOGIN) {
            User user = userService.findByUid(user_id);
            session.setAttribute("sessUid", user_id);
            session.setAttribute("sessUname", user.getUser_nickname());
            msg = user.getUser_nickname() + "님 환영합니다.";
            url = "/";
        } else if (result == UserService.WRONG_PASSWORD) {
            msg = "패스워드가 틀렸습니다.";
            url = "/user/login";
        } else {
            msg = "입력한 아이디가 존재하지 않습니다.";
            url = "/user/register";
        }
        model.addAttribute("msg", msg);
        model.addAttribute("url", url);
        return "common/alertMsg";
    }

    @GetMapping("/loginSuccess")
    public String loginSuccess(HttpSession session, Model model) {
        // Spring Security 현재 세션의 사용자 아이디
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String uid = authentication.getName();

        User user = userService.findByUid(uid);
        session.setAttribute("sessUid", uid);
        session.setAttribute("sessUname", user.getUser_nickname());
        String msg = user.getUser_nickname() + "님 환영합니다.";
        String url = "/";
        model.addAttribute("msg", msg);
        model.addAttribute("url", url);
        return "common/alertMsg";
    }

    @GetMapping("/loginFailure")
    public String loginFailure(Model model) {
        String msg = "잘못 입력하였습니다.";
        String url = "/user/login";
        model.addAttribute("msg", msg);
        model.addAttribute("url", url);
        return "common/alertMsg";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/user/login";
    }
}