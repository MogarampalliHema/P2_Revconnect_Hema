package com.rev.app.controller;

import com.rev.app.dto.PostDTO;
import com.rev.app.entity.Post;
import com.rev.app.entity.User;
import com.rev.app.service.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import com.rev.app.entity.Like;
import com.rev.app.entity.Comment;

@Controller
@RequestMapping("/feed")
public class FeedController {

    private static final Logger logger = LogManager.getLogger(FeedController.class);

    private final PostService postService;
    private final UserService userService;
    private final ConnectionService connectionService;
    private final FollowService followService;
    private final NotificationService notificationService;
    private final InteractionService interactionService;
    private final BlockService blockService;

    public FeedController(PostService postService, UserService userService,
                          ConnectionService connectionService, FollowService followService,
                          NotificationService notificationService,
                          InteractionService interactionService, BlockService blockService) {
        this.postService = postService;
        this.userService = userService;
        this.connectionService = connectionService;
        this.followService = followService;
        this.notificationService = notificationService;
        this.interactionService = interactionService;
        this.blockService = blockService;
    }

    @GetMapping
    public String feed(@AuthenticationPrincipal UserDetails userDetails,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(required = false) String hashtag,
                       @RequestParam(required = false) String type,
                       Model model) {
        User currentUser = userService.findByUsername(userDetails.getUsername());
        logger.info("FeedController: Loading feed for user: {}", currentUser.getUsername());

        // Build user ID list: own + connections + following
        List<Long> feedUserIds = new ArrayList<>();
        feedUserIds.add(currentUser.getId());

        List<Long> connectionIds = connectionService.getConnectionIds(currentUser);
        logger.debug("FeedController: Found {} connections", connectionIds.size());
        feedUserIds.addAll(connectionIds);

        List<Long> followedIds = followService.getFollowedIds(currentUser.getId());
        logger.debug("FeedController: Found {} follows", followedIds.size());
        feedUserIds.addAll(followedIds);

        List<Long> excludedUserIds = blockService.getExcludedUserIds(currentUser.getId());
        logger.debug("FeedController: Found {} blocks", excludedUserIds.size());
        feedUserIds.removeAll(excludedUserIds);

        List<Post> posts;
        if (hashtag != null && !hashtag.isBlank()) {
            posts = postService.filterPosts(null, hashtag).stream()
                    .filter(post -> !excludedUserIds.contains(post.getAuthor().getId()))
                    .collect(Collectors.toList());
        } else if (type != null && !type.isBlank()) {
            try {
                Post.PostType postType = Post.PostType.valueOf(type.toUpperCase());
                posts = postService.filterPosts(postType, null).stream()
                        .filter(post -> !excludedUserIds.contains(post.getAuthor().getId()))
                        .collect(Collectors.toList());
            } catch (IllegalArgumentException e) {
                posts = postService.getFeed(feedUserIds, page, 10).getContent();
            }
        } else {
            Page<Post> feedPage = postService.getFeed(feedUserIds, page, 10);
            posts = feedPage.getContent();
            model.addAttribute("totalPages", feedPage.getTotalPages());
            model.addAttribute("currentPage", page);
        }

        List<Post> trendingPosts = postService.getTrendingPosts(0).getContent().stream()
                .filter(post -> !excludedUserIds.contains(post.getAuthor().getId()))
                .collect(Collectors.toList());

        model.addAttribute("posts", posts);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("newPost", new PostDTO());
        model.addAttribute("unreadCount", notificationService.getUnreadCount(currentUser.getId()));
        model.addAttribute("connectionCount", connectionService.getConnections(currentUser).size());
        model.addAttribute("followerCount", followService.countFollowers(currentUser.getId()));
        model.addAttribute("followingCount", followService.countFollowing(currentUser.getId()));
        model.addAttribute("trending", trendingPosts);
        return "feed";
    }

    @PostMapping("/post")
    public String createPost(@AuthenticationPrincipal UserDetails userDetails,
                             @ModelAttribute PostDTO postDTO,
                             @RequestParam(value = "image", required = false) org.springframework.web.multipart.MultipartFile image,
                             RedirectAttributes redirectAttributes) {
        User currentUser = userService.findByUsername(userDetails.getUsername());

        logger.info("Controller: Received post request from user: {}", currentUser.getUsername());
        logger.info("Controller: Content length: {}", postDTO.getContent() != null ? postDTO.getContent().length() : 0);
        logger.info("Controller: Image presence: {}, Original Filename: {}", (image != null),
                (image != null ? image.getOriginalFilename() : "N/A"));

        try {
            postService.createPost(currentUser, postDTO, image);
            redirectAttributes.addFlashAttribute("successMessage", "Post created!");
        } catch (java.io.IOException e) {
            logger.error("Controller: Failed to upload post image", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to upload image.");
        }
        return "redirect:/feed";
    }

    @GetMapping("/post/{id}/likes")
    public String getPostLikes(@PathVariable Long id, Model model) {
        logger.info("Fetching likes for post id from feed: {}", id);
        List<User> users = interactionService.getLikesByPostId(id).stream()
                .map(Like::getUser)
                .collect(Collectors.toList());
        model.addAttribute("users", users);
        model.addAttribute("title", "Liked by");
        return "fragments/user-list :: userList";
    }

    @GetMapping("/post/{id}/shares")
    public String getPostShares(@PathVariable Long id, Model model) {
        logger.info("Fetching shares for post id from feed: {}", id);
        List<User> users = postService.getShares(id).stream()
                .map(Post::getAuthor)
                .collect(Collectors.toList());
        model.addAttribute("users", users);
        model.addAttribute("title", "Reposted by");
        return "fragments/user-list :: userList";
    }

    @GetMapping("/post/{id}/comments")
    public String getPostComments(@PathVariable Long id, Model model) {
        logger.info("Fetching commenters for post id from feed: {}", id);
        List<User> users = interactionService.getComments(id).stream()
                .map(Comment::getAuthor)
                .distinct()
                .collect(Collectors.toList());
        model.addAttribute("users", users);
        model.addAttribute("title", "Commented by");
        return "fragments/user-list :: userList";
    }
}
