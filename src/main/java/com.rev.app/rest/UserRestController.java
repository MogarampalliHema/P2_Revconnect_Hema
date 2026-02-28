package com.rev.app.rest;

import com.rev.app.entity.User;
import com.rev.app.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserRestController {

    private final UserService userService;

    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/search")
    public List<User> searchUsers(@RequestParam String q) {
        return userService.searchUsers(q);
    }

    @DeleteMapping("/{id}")
    public org.springframework.http.ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return org.springframework.http.ResponseEntity.noContent().build();
    }
}
