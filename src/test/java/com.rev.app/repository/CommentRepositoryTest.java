package com.rev.app.repository;

import com.rev.app.entity.Comment;
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
public class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    private User author;
    private Post post;

    @BeforeEach
    public void setUp() {
        author = new User("commenter", "commenter@example.com", "pass");
        userRepository.save(author);

        post = new Post();
        post.setAuthor(author);
        post.setContent("Post Content");
        postRepository.save(post);
    }

    @Test
    public void testSaveAndFindByPost() {
        Comment comment = new Comment();
        comment.setAuthor(author);
        comment.setPost(post);
        comment.setContent("Great post!");
        commentRepository.save(comment);

        List<Comment> comments = commentRepository.findByPostIdOrderByCreatedAtAsc(post.getId());
        assertThat(comments).hasSize(1);
        assertThat(comments.get(0).getContent()).isEqualTo("Great post!");
    }

    @Test
    public void testCountByPostId() {
        Comment comment = new Comment();
        comment.setAuthor(author);
        comment.setPost(post);
        comment.setContent("Comment");
        commentRepository.save(comment);

        long count = commentRepository.countByPostId(post.getId());
        assertThat(count).isEqualTo(1);
    }
}
