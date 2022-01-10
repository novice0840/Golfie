package com.golfie.auth.application;

import com.golfie.auth.infrastructure.OauthUserInfo;
import com.golfie.auth.infrastructure.SocialProvider;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Profile("dev")
@Component
public class SocialProviderSelector implements ProviderSelectorFactory {

    @Override
    public OauthUserInfo getUserInfoFromSocialProvider(String code, String providerName) {
        SocialProvider socialProvider = getSocialProviderByName(providerName);
        return socialProvider.getUserInfo(code);
    }

    private SocialProvider getSocialProviderByName(String providerName) {
        return Arrays.stream(SocialProvider.values())
                .filter(provider -> provider.getProviderName().equals(providerName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(""));
    }

}
