package com.rev.app.repository;

import com.rev.app.entity.Post;
import com.rev.app.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class PostSpecificationTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        User user = new User("test", "test@example.com", "pass");
        userRepository.save(user);

        Post post1 = new Post(user, "Regular #fitness post");
        post1.setPostType(Post.PostType.REGULAR);
        post1.setHashtags("#fitness");
        postRepository.save(post1);

        Post post2 = new Post(user, "Promotional #fitness post");
        post2.setPostType(Post.PostType.PROMOTIONAL);
        post2.setHashtags("#fitness");
        postRepository.save(post2);
    }

    @Test
    public void testContainsHashtag() {
        Specification<Post> spec = PostSpecification.containsHashtag("#fitness");
        List<Post> result = postRepository.findAll(spec);
        assertThat(result).hasSize(2);
    }

    @Test
    public void testHasPostType() {
        Specification<Post> spec = PostSpecification.hasPostType(Post.PostType.PROMOTIONAL);
        List<Post> result = postRepository.findAll(spec);
        assertThat(result).hasSize(1);
    }
}
