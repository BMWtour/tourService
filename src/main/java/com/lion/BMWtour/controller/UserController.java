package com.lion.BMWtour.controller;

//import lombok.RequiredArgsConstructor;

import com.lion.BMWtour.entity.User;
import com.lion.BMWtour.request.FileRequest;
import com.lion.BMWtour.service.FileUploadService;
import com.lion.BMWtour.service.UserService;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private FileUploadService fileUploadService;

    @GetMapping("/register")
    public String registerForm() {
        return "user/register";
    }

    @GetMapping("/check-duplicate")
    public ResponseEntity<?> checkDuplicate(@RequestParam("uid") String uid) {
        boolean isDuplicate = userService.isUidDuplicate(uid.trim()); // 공백 제거
        return ResponseEntity.ok(Collections.singletonMap("isDuplicate", isDuplicate));
    }

    @GetMapping("/duplicate_nickname")
    public ResponseEntity<?> checkDuplicateNickName(@RequestParam("user_nickname") String user_nickname) {
        boolean isDuplicateNickName = userService.isNickNameDuplicate(user_nickname.trim()); // 공백 제거
        return ResponseEntity.ok(Collections.singletonMap("isDuplicateNickName", isDuplicateNickName));
    }

    @PostMapping("/register")
    public String registerProc(
        String uid,
        String pwd,
        String pwd2,
        String userNickName,
        @RequestParam(value = "category", required = false) String[] category,
        @RequestParam("file") MultipartFile file
    ) throws IOException {

        // 해야할일
        // 가입시 아이디 중복검사 useYn이 yes 일때만

        if (userService.findByUserId(uid) == null && pwd.equals(pwd2) && pwd.length() >= 4) {
            String hashedPwd = BCrypt.hashpw(pwd, BCrypt.gensalt());
            // 추후에 리스트로 받아서 리스트로 저장
            String interest1 = category != null && category.length > 0 ? category[0] : null;
            String interest2 = category != null && category.length > 1 ? category[1] : null;
            String interest3 = category != null && category.length > 2 ? category[2] : null;

            // 파일 업로드 처리
            String userImgUri = null;
            if (!file.isEmpty()) {
                FileRequest fileRequest = new FileRequest(file);
                String uuid = fileUploadService.uploadFile(fileRequest);
                userImgUri = "https://storage.cloud.google.com/gcs_img_tour_service/" + uuid;
            }

            User user = User.builder()
                    .userId(uid)
                    .userPw(hashedPwd)
                    .userNickname(userNickName)
                    .interest1(interest1)
                    .interest2(interest2)
                    .interest3(interest3)
                    // 이미지 추가 내용 필요
                    .userImgUri(userImgUri)
                    .useYn("yes")
                    .provider("tourApp")
                    .regDate(LocalDate.now())
                    .build();
            userService.registerUser(user);
            System.out.println(user.getRegDate());
        }

        return "redirect:/user/login";
    }

    @GetMapping("/login")
    public String loginForm() {
        return "user/login";
    }

    @GetMapping("/loginSuccess")
    public String loginSuccess(HttpSession session, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String uid = authentication.getName();

        User user = userService.findByUserId(uid);
        session.setAttribute("sessUid", uid);
        session.setAttribute("sessUname", user.getUserNickname());
        String msg = user.getUserNickname() + "님 환영합니다." ;
        //String url = "/book/list";
        String url = "http://104.198.205.64:9200/_cat/indices?v";
        model.addAttribute("msg",msg);
        model.addAttribute("url", url);

        return "common/alertMsg";
    }
    @GetMapping
    public String logout() {
        return "redirect:/user/login";
    }
}