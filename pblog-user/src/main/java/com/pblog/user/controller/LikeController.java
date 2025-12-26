package com.pblog.user.controller;

import com.pblog.common.dto.LikeDTO;
import com.pblog.common.result.ResponseResult;
import com.pblog.user.service.LikeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/like")
public class LikeController {

    @Autowired
    private LikeService likeService;


    /**
     * 点赞/取消点赞接口
     */
    @PostMapping("/operate")
    public ResponseResult<String> operateLike(@Valid @RequestBody LikeDTO dto) {
        likeService.handleLikeRequest(dto);
        return ResponseResult.success();
    }

    /**
     * 查询用户是否点赞
     */
    @GetMapping("/isLiked")
    public ResponseResult<Boolean> isLiked(@RequestParam Integer targetId, @RequestParam String targetType) {
        boolean liked = likeService.isLiked(targetType, targetId);
        return ResponseResult.successData(liked);
    }

    /**
     * 查询点赞总数
     */
    @GetMapping("/count")
    public ResponseResult<Integer> getLikeCount(@RequestParam Integer targetId, @RequestParam String targetType) {
        Integer count = likeService.getLikeCount(targetType, targetId);
        return ResponseResult.successData(count);
    }
}
