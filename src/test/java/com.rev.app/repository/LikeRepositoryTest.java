package com.rev.app.repository;

import com.rev.app.entity.Like;
import com.rev.app.entity.Post;
import com.rev.app.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class LikeRepositoryTest {

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;
    private Post post;

    @BeforeEach
    public void setUp() {
        user = new User("liker", "liker@example.com", "pass");
        userRepository.save(user);

        post = new Post();
        post.setAuthor(user);
        post.setContent("Test Content");
        postRepository.save(post);
    }

    @Test
    public void testExistsByUserIdAndPostId() {
        Like like = new Like();
        like.setUser(user);
        like.setPost(post);
        likeRepository.save(like);

        assertThat(likeRepository.existsByUserIdAndPostId(user.getId(), post.getId())).isTrue();
    }

    @Test
    public void testFindByUserIdAndPostId() {
        Like like = new Like();
        like.setUser(user);
        like.setPost(post);
        likeRepository.save(like);

        Optional<Like> found = likeRepository.findByUserIdAndPostId(user.getId(), post.getId());
        assertThat(found).isPresent();
    }
}
