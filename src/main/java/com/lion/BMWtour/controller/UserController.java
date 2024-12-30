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
    @ResponseBody
    public ResponseEntity<?> registerProc(
            String user_id,
            String pwd,
            String pwd2,
            String user_nickname,
            @RequestParam(value = "category", required = false) String[] category,
            @RequestParam("file") MultipartFile file) throws IOException {

        if (userService.findByUserId(user_id) == null && pwd.equals(pwd2) && pwd.length() >= 4) {
            String hashedPwd = BCrypt.hashpw(pwd, BCrypt.gensalt());
            String interest1 = category != null && category.length > 0 ? category[0] : null;
            String interest2 = category != null && category.length > 1 ? category[1] : null;
            String interest3 = category != null && category.length > 2 ? category[2] : null;

            String userImgUri = null;
            if (!file.isEmpty()) {
                FileRequest fileRequest = new FileRequest(file);
                String uuid = fileUploadService.uploadFile(fileRequest);
                userImgUri = "https://storage.cloud.google.com/gcs_img_tour_service/" + uuid;
            }

            User user = User.builder()
                    .userId(user_id)
                    .userPw(hashedPwd)
                    .userNickname(user_nickname)
                    .interest1(interest1)
                    .interest2(interest2)
                    .interest3(interest3)
                    .userImgUri(userImgUri)
                    .useYn("yes")
                    .provider("tourApp")
                    .regDate(LocalDate.now())
                    .build();
            userService.registerUser(user);

            return ResponseEntity.ok(Collections.singletonMap("success", true));
        }

        return ResponseEntity.badRequest()
                .body(Collections.singletonMap("success", false));
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
        String url = "/tour/main";
        model.addAttribute("msg",msg);
        model.addAttribute("url", url);

        return "common/alertMsg";
    }
    @GetMapping
    public String logout() {
        return "redirect:/user/login";
    }

    @GetMapping("/update/{user_id}")
    public String updateForm(@PathVariable String user_id, Model model) {
        User user = userService.findByUserId(user_id);
        model.addAttribute("user", user);
        return "user/update";
    }
    @PostMapping("/update")
    public String registerProc(@RequestParam("user_id") String userId,
                               @RequestParam("pwd") String pwd,
                               @RequestParam("pwd2") String pwd2,
                               @RequestParam("user_nickname") String userNickname,
                               @RequestParam("file") MultipartFile file,
                               @RequestParam(value = "category", required = false) String[] category) throws Exception {
        User user = userService.findByUserId(userId);
        if (pwd.equals(pwd2) && pwd.length() >= 4) {
            String hashedPWD = BCrypt.hashpw(pwd, BCrypt.gensalt());
            user.setUserPw(hashedPWD);
        }
        String userImgUri = null;
        if (!file.isEmpty()) {
            FileRequest fileRequest = new FileRequest(file);
            String uuid = fileUploadService.uploadFile(fileRequest);
            userImgUri = "https://storage.cloud.google.com/gcs_img_tour_service/" + uuid;
        }
        String interest1 = category != null && category.length > 0 ? category[0] : null;
        String interest2 = category != null && category.length > 1 ? category[1] : null;
        String interest3 = category != null && category.length > 2 ? category[2] : null;
        user.setUserNickname(userNickname);
        // 이거 null값 처리는 추후에
        //현제 문제점은 최소 선택수를 안설정해서 그럼
        user.setInterest1(interest1);
        user.setInterest2(interest2);
        user.setInterest3(interest3);
        //만약 이미지를 새롭게 설정하지 않으면 저장 안함
        if (userImgUri != null) {
            user.setUserImgUri(userImgUri);
        }
        userService.updateUser(user);
        return "redirect:/mall/list";
    }

}