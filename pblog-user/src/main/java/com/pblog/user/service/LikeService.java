package com.pblog.user.service;

import com.pblog.common.dto.LikeDTO;

public interface LikeService {
    void handleLikeRequest(LikeDTO dto);

    boolean isLiked(String targetType, Integer targetId);

    Integer getLikeCount(String targetType, Integer targetId);

}
