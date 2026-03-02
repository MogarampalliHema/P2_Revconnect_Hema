package com.rev.app.repository;

import com.rev.app.entity.Follow;
import com.rev.app.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class FollowRepositoryTest {

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private UserRepository userRepository;

    private User follower;
    private User followed;

    @BeforeEach
    public void setUp() {
        follower = new User("follower", "follower@example.com", "pass");
        followed = new User("followed", "followed@example.com", "pass");
        userRepository.save(follower);
        userRepository.save(followed);
    }

    @Test
    public void testExistsByFollowerIdAndFollowedId() {
        Follow follow = new Follow();
        follow.setFollower(follower);
        follow.setFollowed(followed);
        followRepository.save(follow);

        assertThat(followRepository.existsByFollowerIdAndFollowedId(follower.getId(), followed.getId())).isTrue();
    }

    @Test
    public void testFindByFollowerId() {
        Follow follow = new Follow();
        follow.setFollower(follower);
        follow.setFollowed(followed);
        followRepository.save(follow);

        List<Follow> result = followRepository.findByFollowerId(follower.getId());
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFollowed().getUsername()).isEqualTo("followed");
    }

    @Test
    public void testCountByFollowedId() {
        Follow follow = new Follow();
        follow.setFollower(follower);
        follow.setFollowed(followed);
        followRepository.save(follow);

        long count = followRepository.countByFollowedId(followed.getId());
        assertThat(count).isEqualTo(1);
    }
}
