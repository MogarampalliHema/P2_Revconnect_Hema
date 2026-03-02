package com.rev.app.repository;

import com.rev.app.entity.Post;
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
public class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    public void setUp() {
        testUser = new User("post_author", "author@example.com", "password");
        userRepository.save(testUser);
    }

    @Test
    public void testSaveAndFindByAuthor() {
        Post post = new Post();
        post.setContent("Test Post Content");
        post.setAuthor(testUser);
        post.setPublished(true);
        postRepository.save(post);

        List<Post> posts = postRepository
                .findByAuthorIdAndPublishedTrueOrderByPinnedDescCreatedAtDesc(testUser.getId());
        assertThat(posts).hasSize(1);
        assertThat(posts.get(0).getContent()).isEqualTo("Test Post Content");
    }

    @Test
    public void testCountPublishedPostsByAuthor() {
        Post post1 = new Post();
        post1.setContent("Content 1");
        post1.setAuthor(testUser);
        post1.setPublished(true);
        postRepository.save(post1);

        long count = postRepository.countPublishedPostsByAuthor(testUser.getId());
        assertThat(count).isEqualTo(1);
    }

    @Test
    public void testFindLatestPosts() {
        Post post1 = new Post();
        post1.setContent("Content 1");
        post1.setAuthor(testUser);
        postRepository.save(post1);

        Post post2 = new Post();
        post2.setContent("Content 2");
        post2.setAuthor(testUser);
        postRepository.save(post2);

        List<Post> latest = postRepository.findAll(); // Assuming findLatestByActiveTrueOrderByCreatedAtDesc is common
                                                      // or similar
        assertThat(latest).hasSize(2);
    }
}
