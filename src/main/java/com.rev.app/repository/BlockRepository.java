package com.rev.app.repository;

import com.rev.app.entity.Block;
import com.rev.app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BlockRepository extends JpaRepository<Block, Long> {
    boolean existsByBlockerIdAndBlockedId(Long blockerId, Long blockedId);

    Optional<Block> findByBlockerAndBlocked(User blocker, User blocked);

    List<Block> findByBlockerId(Long blockerId);

    List<Block> findByBlockedId(Long blockedId);
}
