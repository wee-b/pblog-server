package com.pblog.user.controller;

import com.pblog.common.vo.CommentForMeVO;
import com.pblog.common.vo.CommentFromMeVO;
import com.pblog.common.vo.CommentVO;
import com.pblog.common.dto.CommentDTO;
import com.pblog.common.dto.PageQueryDTO;
import com.pblog.common.result.PageResult;
import com.pblog.common.result.ResponseResult;
import com.pblog.user.service.CommentService;
import jakarta.validation.Valid;	// 引入依赖spring-boot-starter-validation
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/comment")
public class CommentController {
    /**
     * 服务对象
     */
    @Autowired
    private CommentService commentService;

    /**
     * 分页查询所有数据
     */
//    @GetMapping("/pageQuery")
    public ResponseResult<PageResult> pageQuery(@Valid @RequestBody PageQueryDTO pageQueryDTO) {
        PageResult pageResult = commentService.pageQuery(pageQueryDTO);
        log.info("commentService.pageResult({})", pageResult);
        return ResponseResult.success(pageResult);
    }
    
    /**
     * 查询某个文章的所有评论
     * articleId为0时代表查询留言板信息
     */
    @GetMapping("/all/{id}")
    public ResponseResult<List<CommentVO>> all(@PathVariable("id") @NotNull(message = "ID 不能为空") Integer id) {
        List<CommentVO> res = commentService.all(id);
        log.info("commentService.all");
        return ResponseResult.success(res);
    }

    /**
     * 查询某个用户发布的所有评论
     */
    @GetMapping("/fromMe")
    public ResponseResult<List<CommentFromMeVO>> fromMe() {
        List<CommentFromMeVO> res = commentService.fromMe();
        log.info("commentService.fromMe");
        return ResponseResult.success(res);
    }
    /**
     * 查询所有回复我的评论
     */
    @GetMapping("/forMe")
    public ResponseResult<List<CommentForMeVO>> forMe() {
        List<CommentForMeVO> res = commentService.forMe();
        log.info("commentService.forMe");
        return ResponseResult.success(res);
    }

    /**
     * 通过主键查询单条数据
     */
//    @GetMapping("/queryById/{id}")
    public ResponseResult<CommentVO> queryById(@PathVariable("id") @NotNull(message = "ID 不能为空") Integer id) {
    	CommentVO vo = commentService.queryById(id);
        log.info("commentService.queryById({})", vo);
        return ResponseResult.success(vo);
    }

    /**
     * 发布评论
     */
    @PostMapping("/insert")
    public ResponseResult<String> insert(@Valid @RequestBody CommentDTO commentdto) {
    	commentService.insert(commentdto);
        log.info("commentService.add({})", commentdto);
        return ResponseResult.success();
    }

    /**
     * 发布评论
     */
    @PostMapping("/insertRemark")
    public ResponseResult<String> insertRemark(@Valid @RequestBody CommentDTO commentdto) {
        commentService.insert(commentdto);
        log.info("commentService.add({})", commentdto);
        return ResponseResult.success();
    }

    /**
     * 修改数据
     */
//    @PutMapping("/update/{id}")
    public ResponseResult<String> update(
    	@PathVariable("id") @NotNull(message = "ID 不能为空") Integer id,
    	@Valid @RequestBody CommentDTO commentdto) {
    	commentService.update(id,commentdto);
        log.info("commentService.edit({})", commentdto);
        return ResponseResult.success();
    }

    /**
     * 删除数据
     */
    @DeleteMapping("/delete/{id}")
    public ResponseResult<String> deleteById(@PathVariable("id") Integer id) {
    	commentService.removeById(id);
    	log.info("commentService.removeById({})",id);
        return ResponseResult.success();
    }
}

