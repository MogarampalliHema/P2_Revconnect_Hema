package com.rev.app.repository;

import com.rev.app.entity.Follow;
import com.rev.app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {

    Optional<Follow> findByFollowerAndFollowed(User follower, User followed);

    boolean existsByFollowerIdAndFollowedId(Long followerId, Long followedId);

    List<Follow> findByFollowerId(Long followerId);

    List<Follow> findByFollowedId(Long followedId);

    @Query("SELECT f.followed.id FROM Follow f WHERE f.follower.id = :userId")
    List<Long> findFollowedUserIdsByFollower(@Param("userId") Long userId);

    long countByFollowedId(Long followedId);

    long countByFollowerId(Long followerId);
}
