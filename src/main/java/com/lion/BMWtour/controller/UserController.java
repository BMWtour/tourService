package com.lion.BMWtour.controller;

//import lombok.RequiredArgsConstructor;


import com.lion.BMWtour.dto.request.DeleteRequest;
import com.lion.BMWtour.dto.request.FileRequest;
import com.lion.BMWtour.dto.request.RegisterUserRequest;
import com.lion.BMWtour.entity.User;
import com.lion.BMWtour.service.FileUploadService;
import com.lion.BMWtour.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

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
    public ResponseEntity<?> registerProc( @RequestParam("user_id") String userId,
                                           @RequestParam("user_nickname") String userNickname,
                                           @RequestParam("pwd") String password,
                                           @RequestParam("pwd2") String confirmPassword,
                                           @RequestParam(value = "category", required = false) String[] category,
                                           @RequestParam(value = "file", required = false) MultipartFile file ){
        RegisterUserRequest request = new RegisterUserRequest(userId, userNickname, password, confirmPassword, category, file);
        // 파일 디버깅

        try {
            userService.registerUser(request);
            return ResponseEntity.ok(Collections.singletonMap("success", true));
        }catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("success", false));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("success", false));
        }

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
        if (user.getInterestList()== null) {
            String msg = user.getUserNickname() + "님은 관심사를 선택하지 않은 회원입니다. 페이지로 이동합니다";
            String url = "/user/update/info/" + uid;
            model.addAttribute("msg", msg);
            model.addAttribute("url", url);
        }
        else {

            String msg = user.getUserNickname() + "님 환영합니다.";
            String url = "/tour/main";
            model.addAttribute("msg", msg);
            model.addAttribute("url", url);
        }

        return "common/alertMsg";
    }

    @GetMapping("/info/{user_id}")
    public String updateUserInfo(@PathVariable String user_id, HttpSession session, Model model) throws AccessDeniedException {
        userService.validateUserAccess(user_id, session);
//        Stirng test = session.getAttribute(user_id);
//        System.out.println();
        User user = userService.findByUserId(user_id);
        if (user == null) {
            throw new IllegalArgumentException("User not found for ID: " + user_id);
        }
        model.addAttribute("user", user);
        model.addAttribute("userCategories", Arrays.toString(user.getInterestList()));
        return "user/info";
    }
    @GetMapping("/update/info/{user_id}")
    public String updateUserInfoForm(@PathVariable String user_id, Model model, HttpSession session) throws AccessDeniedException {
        userService.validateUserAccess(user_id, session);
        User user = userService.findByUserId(user_id);
        if (user == null) {
            throw new IllegalArgumentException("User not found for ID: " + user_id);
        }
        System.out.println(Arrays.toString(user.getInterestList()));
        System.out.println(user.getUserNickname());
        model.addAttribute("user", user);
        model.addAttribute("userCategories", Arrays.toString(user.getInterestList()));
        return "user/updateInfo";
    }
    @PostMapping("/update/info")
    @ResponseBody
    public ResponseEntity<?> updateUserProc(
            String user_id,
            String user_nickname,
            @RequestParam(value = "category", required = false) String[] category,
            @RequestParam("file") MultipartFile file) throws IOException {

        User user = userService.findByUserId(user_id);

        if (user == null) {
            return ResponseEntity.badRequest()
                    .body(Collections.singletonMap("success", false));
        }

        if (user_nickname != null && !user_nickname.isEmpty()) {
            user.setUserNickname(user_nickname); // 닉네임 변경
        }

        if (category != null && category.length >= 3 && category.length <= 5) {
            user.setInterestList(category); // 관심사 업데이트
        }

        if (!file.isEmpty()) {
            FileRequest fileRequest = new FileRequest(file);
            String uuid = fileUploadService.uploadFile(fileRequest);
            String userImgUri = "https://storage.googleapis.com/gcs_img_tour_service/" + uuid;
            user.setUserImgUri(userImgUri); // 프로필 사진 업데이트
        }

        userService.updateUser(user); // 변경된 데이터 저장
        return ResponseEntity.ok(Collections.singletonMap("success", true));
    }

    @GetMapping("/update/password/{user_id}")
    public String updatePasswordForm(@PathVariable String user_id, Model model,HttpSession session) throws AccessDeniedException {
        userService.validateUserAccess(user_id, session);
        User user = userService.findByUserId(user_id);
        model.addAttribute("user", user);
        return "user/updatePassword";
    }

    @PostMapping("/update/password")
    @ResponseBody
    public ResponseEntity<?> updatePasswordProc(
            String user_id,
            String pwd,
            String pwd2) {


        User user = userService.findByUserId(user_id);
        if (user == null) {
            return ResponseEntity.badRequest()
                    .body(Collections.singletonMap("fail", false));
        }

        if (pwd != null && pwd.equals(pwd2) && pwd.length() >= 4) {
            String hashedPwd = BCrypt.hashpw(pwd, BCrypt.gensalt());
            user.setUserPw(hashedPwd); // 비밀번호만 변경
        }
        userService.updateUser(user); // 변경된 데이터 저장
        return ResponseEntity.ok(Collections.singletonMap("success", true));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteUserProc(@RequestBody DeleteRequest deleteRequest) {
        // "탈퇴하겠습니다" 검증
        if (!"탈퇴하겠습니다".equals(deleteRequest.getConfirmationText())) {
            return ResponseEntity.badRequest().body(Map.of("error", "확인 문구가 올바르지 않습니다."));
        }

        // 탈퇴 처리 로직 (DB에서 사용자 삭제)
        boolean isDeleted = userService.deleteUserById(deleteRequest.getUserId());
        if (isDeleted) {
            System.out.println("controller 탈퇴 성공");
            return ResponseEntity.ok(Map.of("message", "회원 탈퇴가 완료되었습니다."));
        } else {
            // 탈퇴 실패 응답
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "회원 탈퇴에 실패했습니다."));
        }
    }

}