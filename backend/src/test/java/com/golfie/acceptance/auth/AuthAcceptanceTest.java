package com.golfie.acceptance.auth;

import com.golfie.acceptance.AcceptanceTest;
import com.golfie.auth.application.dto.TokenDto;
import com.golfie.auth.presentation.dto.LoginRequest;
import com.golfie.auth.presentation.dto.SignUpReadyRequest;
import com.golfie.auth.presentation.dto.SignUpReadyResponse;
import com.golfie.auth.presentation.dto.SignUpRequest;
import com.golfie.common.fixture.TestUserInfo;
import com.golfie.user.domain.User;
import com.golfie.user.domain.UserRepository;
import com.golfie.user.domain.profile.*;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Method;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;

public class AuthAcceptanceTest extends AcceptanceTest {

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
    }

    @DisplayName("로그인 - 유저는 로그인에 성공하고 토큰을 발급받는다.")
    @Test
    void login_Success() {
        SignUpReadyRequest signUpReadyRequest = new SignUpReadyRequest("CODE", "TEST");

        SignUpReadyResponse signUpReadyResponse = RestAssured
                .given()
                    .port(port)
                    .accept(MediaType.APPLICATION_JSON_VALUE)
                    .contentType(ContentType.JSON)
                    .body(signUpReadyRequest)
                .when()
                    .request(Method.POST, "/api/signup/oauth/prepare")
                .then()
                    .extract()
                    .as(SignUpReadyResponse.class);


        SignUpRequest signUpRequest = new SignUpRequest(
                signUpReadyResponse.getEmail(),
                signUpReadyResponse.getProfileImage(),
                signUpReadyResponse.getAgeRange(),
                signUpReadyResponse.getGender(),
                signUpReadyResponse.getProviderName(),
                "junslee",
                "hello"
        );

        RestAssured
            .given()
                .port(port)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(ContentType.JSON)
                .body(signUpRequest)
            .when()
                .request(Method.POST, "/api/signup/oauth")
            .then()
                .extract()
                .as(TokenDto.class);


        LoginRequest loginRequest = new LoginRequest("CODE", "TEST");

        TokenDto tokenDto = RestAssured
                .given()
                    .port(port)
                    .contentType(ContentType.JSON)
                    .body(loginRequest)
                .when()
                    .request(Method.POST, "/api/login/oauth")
                .then()
                    .extract()
                    .as(TokenDto.class);

        assertThat(tokenDto).isNotNull();
    }

    @DisplayName("회원가입 준비 - 소셜 서비스 인증 후 계정 정보를 반환한다.")
    @Test
    void signup_Oauth_Prepare() {
        //arrange
        SignUpReadyRequest signUpReadyRequest = new SignUpReadyRequest("CODE", "TEST");
        SignUpReadyResponse target = TestUserInfo.create().toSignUpReadyResponse();

        //act
        SignUpReadyResponse signUpReadyResponse = RestAssured
                .given()
                    .port(port)
                    .accept(MediaType.APPLICATION_JSON_VALUE)
                    .contentType(ContentType.JSON)
                    .body(signUpReadyRequest)
                .when()
                    .request(Method.POST, "/api/signup/oauth/prepare")
                .then()
                    .extract()
                    .as(SignUpReadyResponse.class);

        //assert
        assertThat(signUpReadyResponse)
                .usingRecursiveComparison()
                .isEqualTo(target);
    }

    @DisplayName("회원가입 완료 - 사용자 정보를 저장하고 토큰을 생성하여 반환한다.")
    @Test
    void signup_Oauth_Complete() {
        //arrange
        SignUpRequest signUpRequest = new SignUpRequest(
                "test@test.com",
                "testImageUrl",
                "20-29",
                "MALE",
                "TEST",
                "junslee",
                "hello"
        );

        SocialProfile socialProfile = signUpRequest.toSocialProfile();
        BasicProfile basicProfile = signUpRequest.toBasicProfile();

        User target = new User(1L, basicProfile, socialProfile);

        //act
        TokenDto tokenResponse = RestAssured
                .given()
                    .port(port)
                    .accept(MediaType.APPLICATION_JSON_VALUE)
                    .contentType(ContentType.JSON)
                    .body(signUpRequest)
                .when()
                    .request(Method.POST, "/api/signup/oauth")
                .then()
                    .extract()
                    .as(TokenDto.class);

        //assert
        User user = userRepository.findByEmailAndProviderName("test@test.com", ProviderName.TEST).get();

        assertThat(user)
                .usingRecursiveComparison()
                .isEqualTo(target);
    }

}
