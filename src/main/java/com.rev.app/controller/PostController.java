package com.rev.app.controller;

import com.rev.app.dto.CommentDTO;
import com.rev.app.dto.PostDTO;
import com.rev.app.entity.Comment;
import com.rev.app.entity.Post;
import com.rev.app.entity.User;
import com.rev.app.service.InteractionService;
import com.rev.app.service.NotificationService;
import com.rev.app.service.PostService;
import com.rev.app.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/posts")
public class PostController {

    private static final Logger logger = LogManager.getLogger(PostController.class);

    private final PostService postService;
    private final UserService userService;
    private final InteractionService interactionService;
    private final NotificationService notificationService;

    public PostController(PostService postService, UserService userService,
            InteractionService interactionService,
            NotificationService notificationService) {
        this.postService = postService;
        this.userService = userService;
        this.interactionService = interactionService;
        this.notificationService = notificationService;
    }

    @GetMapping("/{id}")
    public String viewPost(@PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {
        User currentUser = userService.findByUsername(userDetails.getUsername());
        Post post = postService.findById(id);
        List<Comment> comments = interactionService.getComments(id);

        model.addAttribute("post", post);
        model.addAttribute("comments", comments);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("isLiked", interactionService.isLiked(currentUser.getId(), id));
        model.addAttribute("likeCount", interactionService.getLikeCount(id));
        model.addAttribute("newComment", new CommentDTO());
        model.addAttribute("unreadCount", notificationService.getUnreadCount(currentUser.getId()));
        return "post-detail";
    }

    @GetMapping("/{id}/edit")
    public String editPostForm(@PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {
        User currentUser = userService.findByUsername(userDetails.getUsername());
        Post post = postService.findById(id);
        if (!post.getAuthor().getId().equals(currentUser.getId())) {
            return "redirect:/feed";
        }
        PostDTO dto = new PostDTO();
        dto.setContent(post.getContent());
        dto.setHashtags(post.getHashtags());
        dto.setCtaLabel(post.getCtaLabel());
        dto.setCtaUrl(post.getCtaUrl());
        dto.setPinned(post.isPinned());
        model.addAttribute("postDTO", dto);
        model.addAttribute("postId", id);
        model.addAttribute("currentUser", currentUser);
        return "post-edit";
    }

    @PostMapping("/{id}/edit")
    public String editPost(@PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            @ModelAttribute PostDTO postDTO,
            RedirectAttributes redirectAttributes) {
        User currentUser = userService.findByUsername(userDetails.getUsername());
        postService.updatePost(id, currentUser.getId(), postDTO);
        redirectAttributes.addFlashAttribute("successMessage", "Post updated!");
        return "redirect:/posts/" + id;
    }

    @PostMapping("/{id}/delete")
    public String deletePost(@PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        User currentUser = userService.findByUsername(userDetails.getUsername());
        postService.deletePost(id, currentUser.getId());
        redirectAttributes.addFlashAttribute("successMessage", "Post deleted.");
        return "redirect:/feed";
    }

    @PostMapping("/{id}/like")
    public String likePost(@PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.findByUsername(userDetails.getUsername());
        Post post = postService.findById(id);
        interactionService.toggleLike(post, currentUser);
        return "redirect:/posts/" + id;
    }

    @PostMapping("/{id}/comment")
    public String addComment(@PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            @ModelAttribute CommentDTO commentDTO,
            RedirectAttributes redirectAttributes) {
        User currentUser = userService.findByUsername(userDetails.getUsername());
        Post post = postService.findById(id);
        interactionService.addComment(post, currentUser, commentDTO);
        return "redirect:/posts/" + id;
    }

    @PostMapping("/comments/{commentId}/delete")
    public String deleteComment(@PathVariable Long commentId,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam Long postId) {
        User currentUser = userService.findByUsername(userDetails.getUsername());
        interactionService.deleteComment(commentId, currentUser.getId());
        return "redirect:/posts/" + postId;
    }

    @PostMapping("/{id}/share")
    public String sharePost(@PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        User currentUser = userService.findByUsername(userDetails.getUsername());
        postService.sharePost(id, currentUser);
        redirectAttributes.addFlashAttribute("successMessage", "Post shared!");
        return "redirect:/feed";
    }

    @GetMapping("/search")
    public String searchHashtag(@RequestParam String hashtag,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {
        User currentUser = userService.findByUsername(userDetails.getUsername());
        model.addAttribute("posts", postService.searchByHashtag(hashtag));
        model.addAttribute("hashtag", hashtag);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("unreadCount", notificationService.getUnreadCount(currentUser.getId()));
        return "search-hashtag";
    }
}
