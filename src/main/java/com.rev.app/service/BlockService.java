package com.rev.app.service;

import com.rev.app.entity.Block;
import com.rev.app.entity.User;
import com.rev.app.repository.BlockRepository;
import com.rev.app.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BlockService {

    private final BlockRepository blockRepository;
    private final UserRepository userRepository;

    public BlockService(BlockRepository blockRepository, UserRepository userRepository) {
        this.blockRepository = blockRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void blockUser(Long blockerId, Long blockedId) {
        if (blockerId.equals(blockedId))
            return;
        if (blockRepository.existsByBlockerIdAndBlockedId(blockerId, blockedId))
            return;

        User blocker = userRepository.findById(blockerId).orElseThrow();
        User blocked = userRepository.findById(blockedId).orElseThrow();

        Block block = new Block(blocker, blocked);
        blockRepository.save(block);
    }

    @Transactional
    public void unblockUser(Long blockerId, Long blockedId) {
        User blocker = userRepository.findById(blockerId).orElseThrow();
        User blocked = userRepository.findById(blockedId).orElseThrow();
        blockRepository.findByBlockerAndBlocked(blocker, blocked)
                .ifPresent(blockRepository::delete);
    }

    public boolean isBlocked(Long blockerId, Long blockedId) {
        return blockRepository.existsByBlockerIdAndBlockedId(blockerId, blockedId);
    }

    public List<Long> getBlockedUserIds(Long blockerId) {
        return blockRepository.findByBlockerId(blockerId).stream()
                .map(block -> block.getBlocked().getId())
                .collect(Collectors.toList());
    }

    public List<Long> getExcludedUserIds(Long userId) {
        List<Long> excluded = getBlockedUserIds(userId);

        List<Long> blockers = blockRepository.findByBlockedId(userId).stream()
                .map(block -> block.getBlocker().getId())
                .collect(Collectors.toList());

        excluded.addAll(blockers);
        return excluded.stream().distinct().collect(Collectors.toList());
    }
}
