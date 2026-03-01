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

    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN TRUE ELSE FALSE END FROM Follow f WHERE f.follower.id = :followerId AND f.followed.id = :followedId")
    boolean existsByFollowerIdAndFollowedId(@Param("followerId") Long followerId, @Param("followedId") Long followedId);

    @Query("SELECT f FROM Follow f JOIN FETCH f.follower WHERE f.followed.id = :userId")
    List<Follow> findByFollowedId(@Param("userId") Long userId);

    @Query("SELECT f FROM Follow f JOIN FETCH f.followed WHERE f.follower.id = :userId")
    List<Follow> findByFollowerId(@Param("userId") Long userId);

    @Query("SELECT f.followed.id FROM Follow f WHERE f.follower.id = :userId")
    List<Long> findFollowedUserIdsByFollower(@Param("userId") Long userId);

    long countByFollowedId(Long followedId);

    long countByFollowerId(Long followerId);
}
