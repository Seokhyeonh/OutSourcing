package com.sparta.deventer.service;

import com.sparta.deventer.dto.GitHubTokenResponseDto;
import com.sparta.deventer.dto.GitHubUserDto;
import com.sparta.deventer.dto.LoginRequestDto;
import com.sparta.deventer.dto.SignUpRequestDto;
import com.sparta.deventer.entity.User;
import com.sparta.deventer.enums.NotFoundEntity;
import com.sparta.deventer.enums.UserLoginType;
import com.sparta.deventer.enums.UserRole;
import com.sparta.deventer.exception.EntityNotFoundException;
import com.sparta.deventer.exception.GitHubTokenException;
import com.sparta.deventer.exception.InvalidAdminCodeException;
import com.sparta.deventer.exception.InvalidTokenException;
import com.sparta.deventer.exception.UserInfoDuplicateException;
import com.sparta.deventer.jwt.JwtProvider;
import com.sparta.deventer.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * 환경변수 저장
     */
    @Value("${admin.code}")
    private String adminCode;

    @Value("${github.client.id}")
    private String clientId;

    @Value("${github.client.secret}")
    private String clientSecret;

    @Value("${github.redirect.url}")
    private String redirectUri;

    @Value("${github.token.url}")
    private String tokenUrl;

    @Value("${github.user.info.url}")
    private String userInfoUrl;

    @Value("${github.user.password}")
    private String githubPassword;

    /**
     * 유저 로그인 로직
     *
     * @param requestDto 일반유저 회원가입 Request
     * @return 완료 메세지
     */
    public String userSignUp(SignUpRequestDto requestDto) {

        checkDuplicateUser(requestDto.getUsername(), requestDto.getNickname(),
                requestDto.getEmail());

        UserRole role = UserRole.USER;

        if (requestDto.isAdminStatus()) {
            if (Objects.equals(adminCode, requestDto.getAdminCode())) {
                role = UserRole.ADMIN;
            } else {
                throw new InvalidAdminCodeException("관리자 키가 일치하지 않습니다.");
            }
        }

        User user = new User(
                requestDto.getUsername(),
                passwordEncoder.encode(requestDto.getPassword()),
                requestDto.getNickname(),
                role,
                requestDto.getEmail(),
                UserLoginType.DEFAULT
        );

        userRepository.save(user);

        return "회원가입이 완료되었습니다.";
    }

    /**
     * 로그인 로직
     *
     * @param requestDto 로그인 Request
     * @param response   토큰을 담기위한 Response
     * @return 완료 메세지
     */
    @Transactional
    public String login(LoginRequestDto requestDto, HttpServletResponse response) {

        User user = getUserByUsername(requestDto.getUsername());

        user.checkUserWithdrawn();

        user.validatePassword(passwordEncoder, requestDto.getPassword());

        // 인증 매니저를 통해서 아이디, 비번을 통해 인증 진행하고 Security Context 에 저장
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(requestDto.getUsername(),
                        requestDto.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        tokenIssuanceAndSave(response, user);

        return "로그인 성공했습니다";
    }

    /**
     * 깃허브 소셜로그인 로직
     *
     * @param code     사용자가 깃허브로부터 받은 인증 코드
     * @param response 사용자에게 토큰을 담아줄 Response
     */
    @Transactional
    public void processGitHubCallback(String code, HttpServletResponse response) {

        GitHubTokenResponseDto accessToken = getAccessToken(code);

        GitHubUserDto gitHubUserDto = getUserInfo(accessToken);

        User user = registerOrUpdateUser(gitHubUserDto);

        tokenIssuanceAndSave(response, user);
    }

    /**
     * 사용자의 인증코드로부터 깃허브에게 억세스토큰을 발급받기
     *
     * @param code 사용자의 깃허브로부터 받은 인증 코드
     * @return 억세스토큰
     */
    private GitHubTokenResponseDto getAccessToken(String code) {

        String accessTokenUrl = UriComponentsBuilder.fromUriString(tokenUrl)
                .queryParam("client_id", clientId)
                .queryParam("client_secret", clientSecret)
                .queryParam("code", code)
                .queryParam("redirect_uri", redirectUri)
                .toUriString();

        HttpHeaders headers = new HttpHeaders();

        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<GitHubTokenResponseDto> tokenResponse = restTemplate.postForEntity(
                accessTokenUrl, entity, GitHubTokenResponseDto.class);

        if (tokenResponse.getBody() == null || tokenResponse.getBody().getAccess_token() == null) {
            throw new GitHubTokenException("깃허브 토큰 문제가 발생했습니다.");
        }

        return tokenResponse.getBody();
    }

    /**
     * 발급받은 억세스토큰으로 사용자의 정보를 깃허브에게 다시 요청
     *
     * @param responseDto 사용자의 인증코드로 발급받은 억세스토큰
     * @return 사용자의 정보를 담은 dto
     */
    private GitHubUserDto getUserInfo(GitHubTokenResponseDto responseDto) {

        HttpHeaders headers = new HttpHeaders();

        headers.set(JwtProvider.ACCESS_HEADER,
                JwtProvider.BEARER_PREFIX + responseDto.getAccess_token());

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<GitHubUserDto> userResponse = restTemplate.exchange(userInfoUrl,
                HttpMethod.GET, entity, GitHubUserDto.class);

        return userResponse.getBody();
    }

    /**
     * 받아온 사용자의 정보로 회원인지 이미 가입된유저인지 확인후 가입된유저라면 기존 정보를 주고 아니라면 회원가입
     *
     * @param userDto 억세스토큰으로 깃허브로부터 받은 유저정보 Dto
     * @return 우리 서비스의 유저 객체
     */
    private User registerOrUpdateUser(GitHubUserDto userDto) {

        String username = userDto.getName();

        String email = userDto.getEmail();

        UserLoginType loginType = UserLoginType.GITHUB;

        Optional<User> existingUser = userRepository.findByEmailAndLoginType(email, loginType);

        if (existingUser.isPresent()) {
            return existingUser.get();
        } else {
            User newUser = new User(
                    username,
                    passwordEncoder.encode(githubPassword),
                    username,
                    UserRole.USER,
                    email,
                    UserLoginType.GITHUB
            );

            return userRepository.save(newUser);
        }
    }

    /**
     * 토큰 재발행 로직
     *
     * @param request  기존의 토큰이 담겨있는 Request
     * @param response 새로운 토큰을 담아줄 Response
     * @return 완료 메세지
     */
    @Transactional
    public String tokenReissue(HttpServletRequest request, HttpServletResponse response) {

        String refreshToken = jwtProvider.getJwtFromHeader(request, JwtProvider.REFRESH_HEADER);

        jwtProvider.validateToken(refreshToken);

        String refreshUsername = jwtProvider.getUsername(refreshToken);

        User user = getUserByUsername(refreshUsername);

        validateRefreshToken(user.getRefreshToken(), request.getHeader(JwtProvider.REFRESH_HEADER));

        tokenIssuanceAndSave(response, user);

        return "토큰이 재발행 되었습니다.";
    }

    /**
     * 로그아웃 로직
     *
     * @param userId 시큐리티 컨택스트에서 가져온 유저의 PK (유저를 직접 가져와도 준영속성 상태라 어차피 영속성 상태로 만들어야함)
     * @return 완료 메세지
     */
    @Transactional
    public String logout(Long userId) {

        User user = getUserByUserId(userId);

        user.saveRefreshToken(null);

        return "로그아웃을 성공했습니다";
    }

    /**
     * 회원 탈퇴 로직
     *
     * @param userId 시큐리티 컨택스트에서 가져온 유저의 PK
     * @return 완료메세지
     */
    @Transactional
    public String withdraw(Long userId) {

        User user = getUserByUserId(userId);

        user.softWithdrawUser();

        return "회원 탈퇴가 정상적으로 되었습니다.";
    }

    /**
     * 아이디, 닉네임, 이메일 중복확인 기능 (API 로 따로따로 기능을빼서 만드는 것이 더 적절해보임)
     *
     * @param username 유저의 로그인 ID
     * @param nickname 유저의 닉네임
     * @param email    유저의 이메일
     */
    private void checkDuplicateUser(String username, String nickname, String email) {
        List<String> duplicateMessage = new ArrayList<>(3);

        if (userRepository.existsByUsername(username)) {
            duplicateMessage.add("ID가 중복됩니다.");
        }

        if (userRepository.existsByNickname(nickname)) {
            duplicateMessage.add("닉네임이 중복됩니다.");
        }

        if (userRepository.existsByEmail(email)) {
            duplicateMessage.add("이메일이 중복됩니다.");
        }

        if (!duplicateMessage.isEmpty()) {
            throw new UserInfoDuplicateException(duplicateMessage);
        }
    }

    /**
     * 유저 객체 유저 로그인 ID로 찾아오기
     *
     * @param username 유저 로그인 ID
     * @return 유저 객체
     */
    private User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(NotFoundEntity.USER_NOT_FOUND));
    }

    /**
     * 유저 객체 PK로 찾아오
     *
     * @param userId 유저 고유번호 (PK)
     * @return 유저 객체
     */
    private User getUserByUserId(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(NotFoundEntity.USER_NOT_FOUND));
    }

    /**
     * 토큰 발행 로직
     *
     * @param response 토큰을 담아줄 Response
     * @param user     리플레시 토큰정보를 저장할 User 객체
     */
    private void tokenIssuanceAndSave(HttpServletResponse response, User user) {
        String accessToken = jwtProvider.createAccessToken(user.getUsername(),
                user.getRole());
        String refreshToken = jwtProvider.createRefreshToken(user.getUsername());

        user.saveRefreshToken(refreshToken);

        // 토큰 담아주기
        response.addHeader(JwtProvider.ACCESS_HEADER, accessToken);
        response.addHeader(JwtProvider.REFRESH_HEADER, refreshToken);
    }

    /**
     * 토큰 비교로직
     *
     * @param userRefreshToken 저장되어있는 리플레시토큰
     * @param refreshToken     비교할 리플레시토큰
     */
    private void validateRefreshToken(String userRefreshToken, String refreshToken) {
        if (!Objects.equals(userRefreshToken, refreshToken)) {
            throw new InvalidTokenException("토큰 정보가 일치하지 않습니다.");
        }
    }
}