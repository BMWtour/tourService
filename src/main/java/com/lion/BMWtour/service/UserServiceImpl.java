package com.lion.BMWtour.service;

import com.lion.BMWtour.entity.User;
import com.lion.BMWtour.repository.UserRepository;
import com.lion.BMWtour.dto.request.FileRequest;
import com.lion.BMWtour.dto.request.RegisterUserRequest;
import com.lion.BMWtour.entity.User;
import com.lion.BMWtour.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final FileUploadService fileUploadService;

    @Override
    public User findByUserId(String userId) {
        return userRepository.findByUserId(userId).orElse(null);
    }

    @Override
    public boolean isUidDuplicate(String userId) {
        return userRepository.findByUserId(userId).isPresent();
    }

    @Override
    public boolean isNickNameDuplicate(String userNickName) {
        return userRepository.findByUserNickname(userNickName).isPresent();
    }
    @Override
    public void registerUser(User user) {
        if (userRepository.findByUserId(user.getUserId()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 사용자 ID입니다.");
        }
        userRepository.save(user);
        System.out.println("OAuth 회원 저장 완료");
    }
    public void registerUser(RegisterUserRequest request) throws IOException {
        System.out.println("RegisterUserRequest received: " + request.getUserId());
        validateUserRequest(request);
        System.out.println("Validation passed");
        String hashedPwd = hashPassword(request.getPassword());
        System.out.println("Password hashed");
        String[] interestList = validateAndExtractInterests(request.getCategory());
        System.out.println("Interests validated and extracted");

        String userImgUri = uploadUserImage(request.getFile());
        System.out.println("Image uploaded, URI: " + userImgUri);

        User user = buildUser(request, hashedPwd, interestList, userImgUri);
        System.out.println("User entity built: " + user.getUserId());

        userRepository.save(user);
        System.out.println("저장 완료?");
    }

    private void validateUserRequest(RegisterUserRequest request) {
        if (userRepository.findByUserId(request.getUserId()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 사용자 ID입니다.");
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        if (request.getPassword().length() < 4) {
            throw new IllegalArgumentException("비밀번호는 최소 4자리 이상이어야 합니다.");
        }
    }

    private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    private String[] validateAndExtractInterests(String[] categories) {
        if (categories == null || categories.length < 3 || categories.length > 5) {
            throw new IllegalArgumentException("관심사는 최소 3개 이상, 최대 5개까지 선택해야 합니다.");
        }
        return categories.clone();
    }

    private String uploadUserImage(MultipartFile file) throws IOException {
        if (file != null && !file.isEmpty()) {
            FileRequest fileRequest = new FileRequest(file);
            String uuid = fileUploadService.uploadFile(fileRequest);
            return "https://storage.googleapis.com/gcs_img_tour_service/" + uuid;
        }
        return null;
    }

    private User buildUser(RegisterUserRequest request, String hashedPwd, String[] interestList, String userImgUri) {
        return User.builder()
                .userId(request.getUserId())
                .userPw(hashedPwd)
                .userNickname(request.getNickname())
                .interestList(interestList)
                .userImgUri(userImgUri)
                .useYn("yes")
                .provider("tourApp")
                .regDate(LocalDate.now())
                .build();
    }

    @Override
    public int login(String userId, String userPw) {
        User user = findByUserId(userId);
        if (user == null) {
            return USER_NOT_EXIST;
        }
        if(BCrypt.checkpw(userPw, user.getUserPw()) ) {
            return CORRECT_LOGIN;
        }
        return WRONG_PASSWORD;
    }
    public void updateUser(User user) {
        userRepository.save(user);
    }

    public String getLoggedInUserId(HttpSession session) throws AccessDeniedException {
        String loggedInUserId = (String) session.getAttribute("sessUid"); // 세션 키 확인 필요
        if (loggedInUserId == null) {
            throw new AccessDeniedException("로그인이 필요합니다.");
        }
        return loggedInUserId;
    }
    @Override
    public void validateUserAccess(String userIdFromRequest, HttpSession session) throws AccessDeniedException {
        String loggedInUserId = getLoggedInUserId(session);
        if (!loggedInUserId.equals(userIdFromRequest)) {
            throw new AccessDeniedException("접근 권한이 없습니다.");
        }
    }

}
