package com.golfie.auth.infrastructure;

import com.golfie.auth.infrastructure.kakao.KakaoLoginStrategy;
import com.golfie.auth.infrastructure.kakao.KakaoOauthInfo;
import com.golfie.auth.infrastructure.naver.NaverLoginStrategy;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;

public enum SocialProvider {
    KAKAO("KAKAO", new KakaoLoginStrategy()),
    NAVER("NAVER", new NaverLoginStrategy()),
    TEST("TEST");

    private final String providerName;
    private SocialLoginStrategy socialLoginStrategy;

    SocialProvider(String providerName, SocialLoginStrategy socialLoginStrategy) {
        this.providerName = providerName;
        this.socialLoginStrategy = socialLoginStrategy;
    }

    SocialProvider(String providerName) {
        this.providerName = providerName;
    }

    public OauthUserInfo getUserInfo(String code) {
        return socialLoginStrategy.getUserInfo(code);
    }

    public String getProviderName() {
        return providerName;
    }

    public void setSocialLoginStrategy(SocialLoginStrategy socialLoginStrategy) {
        this.socialLoginStrategy = socialLoginStrategy;
    }
}
