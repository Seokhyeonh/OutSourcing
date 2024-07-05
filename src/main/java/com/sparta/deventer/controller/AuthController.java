package com.sparta.deventer.controller;

import com.sparta.deventer.dto.LoginRequestDto;
import com.sparta.deventer.dto.SignUpRequestDto;
import com.sparta.deventer.security.UserDetailsImpl;
import com.sparta.deventer.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 일반유저 회원가입
     *
     * @param requestDto 일반유저 회원가입 Request
     * @return 회원가입 완료 메세지
     */
    @PostMapping("/auth/sign-up")
    public ResponseEntity<String> userSignUp(@Valid @RequestBody SignUpRequestDto requestDto) {
        return ResponseEntity.ok().body(authService.userSignUp(requestDto));
    }

    /**
     * 로그인 기능
     *
     * @param requestDto 로그인 ID와 패스워드
     * @param response   억세스토큰과 리플레시토큰 담아줄 Response
     * @return 회원가입 완료 메세지
     */
    @PostMapping("/auth/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequestDto requestDto,
            HttpServletResponse response) {
        return ResponseEntity.ok().body(authService.login(requestDto, response));
    }

    /**
     * 깃허브 소셜 로그인
     *
     * @param code     사용자가 깃허브로부터 받은 인증코드
     * @param response 사용자에게 우리 서비스의 억세스와, 리플레시토큰을 반환해줄 Response
     * @return 성공 메세지
     */
    @GetMapping("/github/callback")
    public String githubCallback(@RequestParam("code") String code, HttpServletResponse response) {
        authService.processGitHubCallback(code, response);
        return "회원가입 및 로그인 성공";
    }

    /**
     * 억세스토큰 재발행 기능
     *
     * @param request  기존의 토큰을 확인용으로 담아서 보낼 Request
     * @param response 새로운 토큰을 새로 담아서 줄 Response
     * @return 토큰 발행 완료 메세지
     */
    @PostMapping("/auth/refresh")
    public ResponseEntity<String> tokenReissue(HttpServletRequest request,
            HttpServletResponse response) {
        return ResponseEntity.ok().body(authService.tokenReissue(request, response));
    }

    /**
     * 로그아웃 기능
     *
     * @param userDetails 시큐리티 컨택스트에 담긴 유저정보(토큰검증후)
     * @return 로그아웃 만료 메세지
     */
    @PostMapping("/auth/logout")
    public ResponseEntity<String> logout(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok().body(authService.logout(userDetails.getUser().getId()));
    }

    /**
     * 회원탈퇴 기능
     *
     * @param userDetails 시큐리티 컨택스트에 담긴 유저정보(토큰검증후)
     * @return 회원탈퇴 만료 메세지
     */
    @DeleteMapping("/auth/withdraw")
    public ResponseEntity<String> withdraw(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok().body(authService.withdraw(userDetails.getUser().getId()));
    }
}