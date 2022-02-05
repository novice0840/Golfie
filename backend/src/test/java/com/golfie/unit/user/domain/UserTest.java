package com.golfie.unit.user.domain;

import com.golfie.common.fixture.TestUserInfo;
import com.golfie.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class UserTest {

    private User userA;
    private User userB;

    @BeforeEach
    void setup() {
        userA = new User(TestUserInfo.create().toSocialProfile());
        userB = new User(TestUserInfo.create().toSocialProfile());
    }

    @DisplayName("유저 A는 유저 B를 팔로우한다.")
    @Test
    void add_Following() {
        userA.addFollowing(userB);
        Set<User> followers = userB.getFollowers();
        Set<User> following = userA.getFollowing();

        assertThat(followers.size()).isEqualTo(1);
        assertThat(followers.contains(userA)).isTrue();
        assertThat(following.size()).isEqualTo(1);
        assertThat(following.contains(userB)).isTrue();
    }

    @DisplayName("유저 A는 유저 B를 언팔로우한다.")
    @Test
    void stop_Following() {
        userA.addFollowing(userB);
        userA.stopFollowing(userB);

        assertThat(userA.getFollowing().size()).isEqualTo(0);
        assertThat(userB.getFollowers().size()).isEqualTo(0);
    }

    @DisplayName("유저 A가 유저 B를 팔로우있으면 true를 반환한다.")
    @Test
    void is_Following_True() {
        userA.addFollowing(userB);

        assertThat(userA.isFollowing(userB)).isTrue();
    }

    @DisplayName("유저 A가 유저 B를 팔로우하고 있지 않으면 false를 반환한다.")
    @Test
    void is_Following_False() {
        assertThat(userA.isFollowing(userB)).isFalse();
    }
}
